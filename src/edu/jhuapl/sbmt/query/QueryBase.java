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
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.collect.Lists;

import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.FileCache.FileInfo;
import edu.jhuapl.saavtk.util.FileCache.FileInfo.YesOrNo;
import edu.jhuapl.saavtk.util.FileUtil;
import edu.jhuapl.saavtk.util.SafePaths;
import edu.jhuapl.sbmt.model.image.ImageSource;


/**
 * This class represents a database storing information about all the
 * data. It also provides functions for querying the database.
 */
public abstract class QueryBase implements Cloneable
{
    protected final String galleryPath;
    protected Boolean galleryExists;

    protected QueryBase(String galleryPath)
    {
        this.galleryPath = galleryPath;
        this.galleryExists = null;
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

    protected List<List<String>> doQuery(String phpScript, String data) throws IOException
    {
        List<List<String>> results = new ArrayList<>();

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
        updateImageInventory(results);

        return results;
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
            String pathToGalleryFolderOnServer)
    {
        if (!pathToImageFolderOnServer.endsWith("/"))
            pathToImageFolderOnServer += "/";

        if (pathToGalleryFolderOnServer != null && !pathToGalleryFolderOnServer.endsWith("/"))
            pathToGalleryFolderOnServer += "/";

        List<List<String>> results = new ArrayList<>();

        FileInfo info = FileCache.getFileInfoFromServer(pathToFileListOnServer);
        if (!info.isURLAccessAuthorized().equals(YesOrNo.YES) || !info.isExistsOnServer().equals(YesOrNo.YES))
        {
            return getCachedResults(getDataPath());
        }
        File file = FileCache.getFileFromServer(pathToFileListOnServer);

        // Let user know that search uses fixed list and ignores search parameters
        JOptionPane.showMessageDialog(null,
                "Search uses a fixed list and ignores selected search parameters.",
                "Notification",
                JOptionPane.INFORMATION_MESSAGE);

        if (file != null)
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
                    if(pathToGalleryFolderOnServer == null)
                    {
                        res.add(null);
                    }
                    else
                    {
                        res.add(pathToGalleryFolderOnServer + imagePath);
                    }
                    results.add(res);
                }
                updateImageInventory(results);
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

    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("YYYY MM DD HH:MM:SS");
    private static final DateTimeFormatter YYYY_MMM_DD = DateTimeFormatter.ofPattern("YYYY MMM DD HH:MM:SS");
    private static final DateTimeFormatter OUTPUT_TIME_FORMAT = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:MM:SS");

    private String interpretTimeSubStrings(List<String> vals)
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
     * Add the supplied search results to the image inventory for this small body configuration/instrument.
     * New results (dates) supersede previous results for the same image file.
     * Following a call to this method, the image inventory file will thus contain a union of all the search
     * results ever made. Note that this inventory includes all files that were found in a search, whether
     * or not those files have every actually been displayed and cached.
     * @param newResults the results to add
     */
    protected void updateImageInventory(List<List<String>> newResults)
    {
        SortedMap<String, List<String>> inventory = getImageInventory();

        // Add the new results, overwriting any that were previously cached; always assume newer is "better".
        for (List<String> each: newResults)
        {
            inventory.put(each.get(0), each);
        }

        // Write the new inventory file.
        String inventoryFileName = getImageInventoryFileName();
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
     * Return the list of cached results from previous image searches stored in the
     * image inventory file. This particular implementation
     * assumes the first element in each result is the name of a file, and checks this
     * against a list of files that actually exist in the user's cache.
     * @param pathToImageFolder the folder where the image list and images are located
     * @return the image list
     */
    protected List<List<String>> getCachedResults(
            String pathToImageFolder
            )
    {
        // We will reach this if SBMT is unable to connect to server
        JOptionPane.showMessageDialog(null,
                "SBMT had a problem while performing the search. Ignoring search parameters and listing all cached images.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
        final List<File> fileList = getCachedFiles(pathToImageFolder);
        final Map<String, File> filesFound = new TreeMap<>();
        for (File file: fileList)
        {
            // Strip off the local cache part of the prefix.
            String path = file.getPath().substring(Configuration.getCacheDir().length());
            filesFound.put(path, file);
        }

        final List<List<String>> result = new ArrayList<>();
        SortedMap<String, List<String>> inventory = getImageInventory();
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
    protected String getImageInventoryFileName()
    {
        String imagesPath = getDataPath();
        if (imagesPath == null) return null;
        return SafePaths.getString(Configuration.getCacheDir(), "imageInventory.txt");
    }

    /**
     * Return the current content of the image inventory file. Note this inventory
     * should be a superset of the image files that are locally cached.
     * @return the image inventory
     */
    protected SortedMap<String, List<String>> getImageInventory()
    {
        SortedMap<String, List<String>> inventory = new TreeMap<>();
        String inventoryFileName = getImageInventoryFileName();
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
     * @param pathToImageFolder
     * @return the map
     */
    protected List<File> getCachedFiles(
            String pathToImageFolder
            )
    {
        final List<File> filesFound = new ArrayList<>();
        if (pathToImageFolder != null)
        {
            try
            {
                final int maxDepth = 10;
                // Find actual files present.
                Path start = Paths.get(Configuration.getCacheDir(), pathToImageFolder);
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
     * is a list of list of strings where each a list in the containing list
     * contains 2 elements. The first element is the path on the server to the
     * FIT image file. The second is the start time of the image.
     *
     * @param datatype
     * @param startDate
     * @param stopDate
     * @param filters
     * @param userDefined1
     * @param userDefined2
     * @param startDistance
     * @param stopDistance
     * @param startResolution
     * @param stopResolution
     * @param searchString
     * @param polygonTypes
     * @param fromIncidence
     * @param toIncidence
     * @param fromEmission
     * @param toEmission
     * @param fromPhase
     * @param toPhase
     * @param cubeList
     * @param imageSource
     * @param limbType
     * @return
     */
    public abstract List<List<String>> runQuery(
            String type,
            DateTime startDate,
            DateTime stopDate,
            boolean sumOfProductsSearch,
            List<Integer> camerasSelected,
            List<Integer> filtersSelected,
            double startDistance,
            double stopDistance,
            double startResolution,
            double stopResolution,
            String searchString,
            List<Integer> polygonTypes,
            double fromIncidence,
            double toIncidence,
            double fromEmission,
            double toEmission,
            double fromPhase,
            double toPhase,
            TreeSet<Integer> cubeList,
            ImageSource imageSource,
            int limbType);

    public String getGalleryPath()
    {
        if (galleryExists == null)
        {
            galleryExists = Boolean.FALSE;
            if (galleryPath != null)
            {
                FileInfo info = FileCache.getFileInfoFromServer(galleryPath);
                if (info.isExistsLocally() || info.isExistsOnServer().equals(YesOrNo.YES))
                {
                    galleryExists = Boolean.TRUE;
                }
            }
        }
        return galleryExists ? galleryPath : null;
    }
}
