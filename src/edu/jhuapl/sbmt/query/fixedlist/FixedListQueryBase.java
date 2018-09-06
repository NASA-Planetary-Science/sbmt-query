package edu.jhuapl.sbmt.query.fixedlist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.collect.Lists;

import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.FileCache.FileInfo;
import edu.jhuapl.saavtk.util.FileCache.FileInfo.YesOrNo;
import edu.jhuapl.saavtk.util.FileUtil;
import edu.jhuapl.sbmt.query.QueryBase;

/**
 * This class represents a database storing information about all the
 * data. It also provides functions for querying the database.
 */
public abstract class FixedListQueryBase extends QueryBase
{
    protected FixedListQueryBase(String galleryPath)
    {
        super(galleryPath);
        this.galleryPath = galleryPath;
        this.galleryExists = null;
    }

    @Override
    public FixedListQueryBase clone()
    {
//        try
//        {
            return (FixedListQueryBase) super.clone();
//        }
//        catch (CloneNotSupportedException e)
//        {
//            // Can't happen.
//            throw new AssertionError(e);
//        }
    }

    protected List<List<String>> getResultsFromFileListOnServer(
            String pathToFileListOnServer,
            String pathToDataFolderOnServer,
            String pathToGalleryFolderOnServer,
            String searchString)
    {
        List<List<String>> results = getResultsFromFileListOnServer(pathToFileListOnServer, pathToDataFolderOnServer, pathToGalleryFolderOnServer);

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
            String pathToDataFolderOnServer,
            String pathToGalleryFolderOnServer)
    {
        if (!pathToDataFolderOnServer.endsWith("/"))
            pathToDataFolderOnServer += "/";

        if (pathToGalleryFolderOnServer != null && !pathToGalleryFolderOnServer.endsWith("/"))
            pathToGalleryFolderOnServer += "/";

        List<List<String>> results = new ArrayList<>();

        if (!checkAuthorizedAccess())
        {
            return results;
        }

        FileInfo info = FileCache.getFileInfoFromServer(pathToFileListOnServer);
        if (!info.isURLAccessAuthorized().equals(YesOrNo.YES) || !info.isExistsOnServer().equals(YesOrNo.YES))
        {
            return getCachedResults(getDataPath());
        }
        File file = FileCache.getFileFromServer(pathToFileListOnServer);

        // Let user know that search uses fixed list and ignores search parameters
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

                    String dataPath = vals.get(0).replace(pathToDataFolderOnServer, "");
                    res.add(pathToDataFolderOnServer + dataPath);
                    res.add(new Long(new DateTime(timeString, DateTimeZone.UTC).getMillis()).toString());
                    results.add(res);
                }
                updateDataInventory(results);
            }
            catch (FileNotFoundException e)
            {
                // We will reach this if SBMT is unable to connect to server
                JOptionPane.showMessageDialog(null,
                        "Search returned no results because SBMT is unable to retrieve data list from server.",
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


//    /**
//     * Run a query and return an array containing the results. The returned array
//     * is a list of list of strings where each a list in the containing list
//     * contains 2 elements. The first element is the path on the server to the
//     * FIT image file. The second is the start time of the image.
//     *
//     */
//    public abstract List<List<String>> runFixedListQuery(
//            ImageSource imageSource,
//            String dataFolderOffRoot,
//            String dataListFileName);


}
