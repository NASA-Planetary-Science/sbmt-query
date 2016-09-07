package edu.jhuapl.sbmt.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.FileUtil;
import edu.jhuapl.sbmt.model.image.ImageSource;


/**
 * This class represents a database storing information about all the
 * data. It also provides functions for querying the database.
 */
abstract public class QueryBase
{
    public QueryBase clone()
    {
        return null;
    }

    protected List<List<String>> doQuery(String phpScript, String data)
    {
        List<List<String>> results = new ArrayList<List<String>>();

        try
        {
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
                List<String> words = new ArrayList<String>();
                for (String word : tokens)
                    words.add(word);
                results.add(words);
            }

            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

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
            String pathToImageFolderOnServer)
    {
        if (!pathToImageFolderOnServer.endsWith("/"))
            pathToImageFolderOnServer += "/";

        List<List<String>> results = new ArrayList<List<String>>();

        File file = FileCache.getFileFromServer(pathToFileListOnServer);

        if (file != null)
        {
            try
            {
                List<String> lines = FileUtil.getFileLinesAsStringList(file.getAbsolutePath());
                for (String line : lines)
                {
                    String[] vals = line.trim().split("\\s+");
                    List<String> res = new ArrayList<String>();
                    res.add(pathToImageFolderOnServer + vals[0]);
                    res.add(new Long(new DateTime(vals[1], DateTimeZone.UTC).getMillis()).toString());
                    results.add(res);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return results;
    }

    abstract public String getImagesPath();

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
    abstract public List<List<String>> runQuery(
            String type,
            DateTime startDate,
            DateTime stopDate,
            List<Boolean> filtersChecked,
            List<Boolean> camerasChecked,
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
}
