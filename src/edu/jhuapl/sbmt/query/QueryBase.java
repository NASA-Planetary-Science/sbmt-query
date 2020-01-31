package edu.jhuapl.sbmt.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.collect.Lists;

import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.saavtk.util.DownloadableFileState;
import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.FileUtil;
import edu.jhuapl.saavtk.util.SafeURLPaths;
import edu.jhuapl.saavtk.util.UrlInfo.UrlStatus;

import crucible.crust.metadata.api.Key;
import crucible.crust.metadata.api.Metadata;
import crucible.crust.metadata.api.MetadataManager;
import crucible.crust.metadata.impl.SettableMetadata;


/**
 * This class represents a database storing information about all the
 * data. It also provides functions for querying the database.
 */
public abstract class QueryBase implements Cloneable, MetadataManager, IQueryBase
{
    protected String galleryPath;
    private boolean headless = false;

    protected QueryBase(String galleryPath)
    {
        this.galleryPath = galleryPath;
        if (System.getProperty("java.awt.headless") != null && System.getProperty("java.awt.headless").equalsIgnoreCase("true"))
            headless = true;
    }


    @Override
    public QueryBase clone()
    {
        try
        {
            return (QueryBase) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            // Can't happen.
            throw new AssertionError(e);
        }
    }

    public static boolean checkForDatabaseTable(String tableName) throws IOException
    {
        URL u = new URL(Configuration.getQueryRootURL() + "/" + "tableexists.php");
        URLConnection conn = u.openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0");

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write("tableName=" + tableName);
        wr.flush();

        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        String line = in.readLine();
        in.close();

        if (line == null)
        {
            throw new IOException("No database available");
        }
        else if (!line.equalsIgnoreCase("true") && !line.equalsIgnoreCase("false"))
        {
            throw new IOException(line);
        }
        return line.equalsIgnoreCase("true");
    }

    protected List<List<String>> doQuery(String phpScript, String data) throws IOException
    {
        List<List<String>> results = new ArrayList<>();

        if (!checkAuthorizedAccess())
        {
            return results;
        }

        URL u = new URL(Configuration.getQueryRootURL() + "/" + phpScript);
        URLConnection conn = u.openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0");

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();

        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader in = new BufferedReader(isr);

        String line;

        while ((line = in.readLine()) != null)
        {
            line = line.trim();
            if (line.length() == 0)
                continue;

            String[] tokens = line.split("\\s+");
            List<String> words = new ArrayList<>();
            for (String word : tokens)
                words.add(word);
            results.add(words);
        }

        in.close();
        for (List<String> res : results)
        {
            changeDataPathToFullPath(res);
        }

        updateDataInventory(results);

        return results;
    }

    protected boolean checkAuthorizedAccess()
    {
        DownloadableFileState state = FileCache.instance().query(getDataPath(), false);

        if (state.isAccessible())
        {
            return true;
        }
        else if (state.isUrlUnauthorized())
        {
            JOptionPane.showMessageDialog(null,
                    "You are not authorized to access this data.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        return false;
    }
    protected String constructUrlArguments(HashMap<String, String> args)
    {
        String str = "";

        boolean firstKey = true;
        for (String key : args.keySet())
        {
            if (firstKey == true)
                firstKey = false;
            else
                str += "&";

            str += key + "=" + args.get(key);
        }

        return str;
    }

    protected List<List<String>> getResultsFromFileListOnServer(
            String pathToFileListOnServer,
            String pathToImageFolderOnServer,
            String pathToGalleryFolderOnServer,
            String searchString)
    {
        List<List<String>> results = getResultsFromFileListOnServer(pathToFileListOnServer, pathToImageFolderOnServer, pathToGalleryFolderOnServer);

        if (searchString != null && !searchString.isEmpty())
        {
            searchString = wildcardToPathRegex(searchString);
            List<List<String>> unfilteredResults = results;
            results = new ArrayList<>();
            for (List<String> result : unfilteredResults)
            {
                String name = result.get(0);
                if (name.matches(searchString))
                {
                    results.add(result);
                }
            }
        }
        return results;
    }

    protected List<List<String>> getResultsFromFileListOnServer(
            String pathToFileListOnServer,
            String pathToImageFolderOnServer,
            String pathToGalleryFolderOnServer)
    {
    	DownloadableFileState state = FileCache.refreshStateInfo(pathToFileListOnServer);
    	if (state.getUrlState().getStatus() != UrlStatus.ACCESSIBLE)
    	{
    		return getCachedResults(getDataPath());
    	}

        if (!pathToImageFolderOnServer.endsWith("/"))
            pathToImageFolderOnServer += "/";

        if (pathToGalleryFolderOnServer != null && !pathToGalleryFolderOnServer.endsWith("/"))
            pathToGalleryFolderOnServer += "/";

        List<List<String>> results = new ArrayList<>();

        if (!checkAuthorizedAccess())
        {
            return results;
        }

        File file = FileCache.getFileFromServer(pathToFileListOnServer);

//        if (!FileCache.instance().isAccessible(pathToFileListOnServer))
//        {
//            return getCachedResults(getDataPath());
//        }


        // Let user know that search uses fixed list and ignores search parameters
        if (!Boolean.parseBoolean(System.getProperty("java.awt.headless")))
        	JOptionPane.showMessageDialog(null,
                "Search uses a fixed list and ignores all but file name search parameters.",
                "Notification",
                JOptionPane.INFORMATION_MESSAGE);

        if (file.exists())
        {
            try
            {
                List<String> lines = FileUtil.getFileLinesAsStringList(file.getAbsolutePath());
                for (String line : lines)
                {
                    List<String> vals = Lists.newArrayList(line.trim().split("\\s+"));
                    String timeString = interpretTimeSubStrings(vals.subList(1, vals.size()));
                    List<String> res = new ArrayList<>();

                    String imagePath = vals.get(0).replace(pathToImageFolderOnServer, "");
                    res.add(pathToImageFolderOnServer + imagePath);
                    res.add(new Long(new DateTime(timeString, DateTimeZone.UTC).getMillis()).toString());
                    results.add(res);
                }
                updateDataInventory(results);
            }
            catch (FileNotFoundException e)
            {
                // We will reach this if SBMT is unable to connect to server
                JOptionPane.showMessageDialog(null,
                        "Search returned no results because SBMT is unable to retrieve image list from server.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return results;
    }

    protected String wildcardToPathRegex(String wildcard)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("^.*/");
        for (char c : wildcard.toCharArray())
        {
            switch(c) {
            case '*':
                builder.append(".*");
                break;
            case '?':
                builder.append(".");
                break;
                // escape special regexp-characters
            case '(': case ')': case '[': case ']': case '$':
            case '^': case '.': case '{': case '}': case '|':
            case '\\':
                builder.append("\\");
                builder.append(c);
                break;
            default:
                builder.append(c);
                break;
            }
        }
        builder.append('$');
        return builder.toString();
    }

    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("YYYY MM DD HH:MM:SS");
    private static final DateTimeFormatter YYYY_MMM_DD = DateTimeFormatter.ofPattern("YYYY MMM DD HH:MM:SS");
    private static final DateTimeFormatter OUTPUT_TIME_FORMAT = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:MM:SS");

    protected String interpretTimeSubStrings(List<String> vals)
    {
        if (vals.isEmpty()) return null;
        if (vals.size() == 1) return vals.get(0);
        String timeString = String.join (" ", vals.subList(1, vals.size()));
        try
        {
            LocalDate date = LocalDate.parse(timeString, YYYY_MMM_DD);
            return date.format(OUTPUT_TIME_FORMAT).replace(" ", "T");
        }
        catch (@SuppressWarnings("unused") DateTimeParseException e)
        {
        }
        try
        {
            LocalDate date = LocalDate.parse(timeString, YYYY_MM_DD);
            return date.format(OUTPUT_TIME_FORMAT).replace(" ", "T");
        }
        catch (@SuppressWarnings("unused") DateTimeParseException e)
        {
        }
        return null;
    }

    /**
     * Add the supplied search results to the data inventory for this small body configuration/instrument.
     * New results (dates) supersede previous results for the same image file.
     * Following a call to this method, the image inventory file will thus contain a union of all the search
     * results ever made. Note that this inventory includes all files that were found in a search, whether
     * or not those files have every actually been displayed and cached.
     * @param newResults the results to add
     */
    protected void updateDataInventory(List<List<String>> newResults)
    {
        SortedMap<String, List<String>> inventory = getDataInventory();

        // Add the new results, overwriting any that were previously cached; always assume newer is "better".
        for (List<String> each: newResults)
        {
            inventory.put(each.get(0), each);
        }

        // Write the new inventory file.
        String inventoryFileName = getDataInventoryFileName();
        if (inventoryFileName != null) {
            PrintWriter writer = null;
            File newInventoryFile = null;
            try
            {
                // Write as a temporary file first, then rename to keep things clean/atomic.
                String prefix = inventoryFileName.substring(inventoryFileName.lastIndexOf(File.separator) + File.separator.length(), inventoryFileName.lastIndexOf('.'));
                String suffix = inventoryFileName.substring(inventoryFileName.lastIndexOf('.'));
                File directory = new File(inventoryFileName.substring(0, inventoryFileName.lastIndexOf(File.separator)));
                if (!directory.exists())
                    directory.mkdirs();
                newInventoryFile = File.createTempFile(prefix, suffix, directory);
                writer = new PrintWriter(newInventoryFile, "UTF-8");
                for (Entry<String, List<String>> each: inventory.entrySet())
                {
                    writer.println(String.join(" ", each.getValue()));
                }
            }
            catch (@SuppressWarnings("unused") IOException e)
            {
                // Ignore any problems writing this file.
                // e.printStackTrace();
            }
            finally
            {
                if (writer != null)
                    writer.close();
                if (newInventoryFile != null)
                {
                    newInventoryFile.renameTo(new File(inventoryFileName));
                }
            }
        }
    }

    /**
     * Return the list of cached results from previous data searches stored in the
     * data inventory file. This particular implementation
     * assumes the first element in each result is the name of a file, and checks this
     * against a list of files that actually exist in the user's cache.
     * @param pathToImageFolder the folder where the image list and images are located
     * @return the image list
     */
    protected List<List<String>> getCachedResults(
            String pathToDataFolder
            )
    {
        if (headless  == false)
        {
            // We will reach this if SBMT is unable to connect to server
            JOptionPane.showMessageDialog(null,
                "Unable to perform online search. Ignoring search parameters and listing all cached data products.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        final List<File> fileList = getCachedFiles(pathToDataFolder);
        final Map<String, File> filesFound = new TreeMap<>();
        for (File file: fileList)
        {
            // Strip off the local cache part of the prefix.
            String path = file.getPath().substring(Configuration.getCacheDir().length());
            filesFound.put(path, file);
        }

        final List<List<String>> result = new ArrayList<>();
        SortedMap<String, List<String>> inventory = getDataInventory();
        for (Entry<String, List<String>> each: inventory.entrySet())
        {
            if (filesFound.containsKey(each.getKey()))
                result.add(each.getValue());
        }
        return result;
    }

    /**
     * Return the full path name of the inventory file.
     * @return the inventory file name
     */
    protected String getDataInventoryFileName()
    {
        String dataPath = getDataPath();
        if (dataPath == null) return null;
        return SafeURLPaths.instance().getString(Configuration.getCacheDir(), "dataInventory.txt");
    }

    /**
     * Return the current content of the data inventory file. Note this inventory
     * should be a superset of the data files that are locally cached.
     * @return the data inventory
     */
    protected SortedMap<String, List<String>> getDataInventory()
    {
        SortedMap<String, List<String>> inventory = new TreeMap<>();
        String inventoryFileName = getDataInventoryFileName();
        if (inventoryFileName != null)
        {
            try
            {
                List<String> lines = FileUtil.getFileLinesAsStringList(inventoryFileName);
                for (String line: lines)
                {
                    String[] values = line.trim().split("\\s+");
                    inventory.put(values[0], Lists.newArrayList(values));
                }
            }
            catch (@SuppressWarnings("unused") IOException e)
            {
                // Ignore any problems reading any previous inventories.
                // e.printStackTrace();
            }
        }

        return inventory;
    }

    /**
     * Return a list of files that actually exist on disk in the user's cache.
     * @param pathToDataFolder
     * @return the map
     */
    protected List<File> getCachedFiles(
            String pathToDataFolder
            )
    {
        final List<File> filesFound = new ArrayList<>();
        if (pathToDataFolder != null)
        {
            try
            {
                final int maxDepth = 10;
                // Find actual files present.
                Path start = Paths.get(Configuration.getCacheDir(), pathToDataFolder);
                Files.walkFileTree(start, EnumSet.allOf(FileVisitOption.class), maxDepth, new SimpleFileVisitor<Path>()
                {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
                    {
                        FileVisitResult result = FileVisitResult.CONTINUE;
                        try
                        {
                            result = super.visitFile(path, attrs);
                            File file = path.toFile();
                            if (file.isFile())
                                filesFound.add(file);
                        }
                        catch (@SuppressWarnings("unused") IOException e)
                        {
                            // Ignore problems that occur while traversing the file tree.
                            // e.printStackTrace();
                        }
                        return result;
                    }

                });
            }
            catch (IOException e)
            {
                // Report this for debugging purposes, but no need for a pop-up.
                e.printStackTrace();
            }
        }

        return filesFound;
    }

    public abstract String getDataPath();

    /**
     * Run a query and return an array containing the results. The returned array
     * is a list of Metadata objects that contain data specific to that instrument's
     * query request, which is also encapsulated in a metadata bundle.
     *
     */
    public abstract ISearchResultsMetadata runQuery(SearchMetadata queryMetadata);

    public String getGalleryPath()
    {
        return galleryPath != null && FileCache.instance().isAccessible(galleryPath) ? galleryPath : null;
    }

    // Convert the 0th element of the result (the path to the data)
    // with the full path, but only if the result does not already have
    // a full path.
    protected void changeDataPathToFullPath(List<String> result)
    {
        String fullPath = result.get(0);
        if (!fullPath.contains("/"))
        {
            result.set(0, getDataPath() + "/" + fullPath);
        }
    }

    protected <T> void write(Key<T> key, T value, SettableMetadata configMetadata)
    {
        if (value != null)
        {
            configMetadata.put(key, value);
        }
    }

    protected <T> T read(Key<T> key, Metadata configMetadata)
    {
        T value = configMetadata.get(key);
        if (value != null)
            return value;
        return null;
    }


	@Override
	public int hashCode()
	{
		return Objects.hash(galleryPath, headless);
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryBase other = (QueryBase) obj;
		return Objects.equals(galleryPath, other.galleryPath) && headless == other.headless;
	}
}
