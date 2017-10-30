package edu.jhuapl.sbmt.query.eros;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.time.DateTime;

import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.sbmt.model.image.ImageSource;
import edu.jhuapl.sbmt.query.QueryBase;


/**
 * This class provides functions for querying the database.
 */
public class NisQuery extends QueryBase
{
    private static NisQuery ref = null;

    private String getNisPath(List<String> result)
    {
        int id = Integer.parseInt(result.get(0));
        int year = Integer.parseInt(result.get(1));
        int dayOfYear = Integer.parseInt(result.get(2));

        return this.getNisPath(id, year, dayOfYear);
    }

    private String getNisPath(int name, int year, int dayOfYear)
    {
        String str = "/NIS/";
        str += year + "/";

        if (dayOfYear < 10)
            str += "00";
        else if (dayOfYear < 100)
            str += "0";

        str += dayOfYear + "/";

        str += "N0" + name + ".NIS";

        return str;
    }

    public static NisQuery getInstance()
    {
        if (ref == null)
            ref = new NisQuery();
        return ref;
    }

    @Override
    public QueryBase clone()
    {
        return null;
    }

    private NisQuery()
    {
    }

    @Override
    public String getGalleryPath()
    {
        return null;
    }

    @Override
    public String getImagesPath()
    {
        return "/NIS/";
    }

    /**
     * Run a query which searches for msi images between the specified dates.
     * Returns a list of URL's of the fit files that match.
     *
     * @param startDate
     * @param endDate
     */
    @Override
    public List<List<String>> runQuery(
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
            ImageSource msiSource,
            int limbType)
    {
        System.err.println("Error: Not implemented. Do not call.");
        return null;
    }

    public List<String> runQueryNIS(
            DateTime startDate,
            DateTime stopDate,
            List<Integer> filters,
            boolean iofdbl,
            boolean cifdbl,
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
            ImageSource msiSource,
            int limbType)
    {
        List<String> matchedImages = new ArrayList<>();
        List<List<String>> results = null;

        double minIncidence = Math.min(fromIncidence, toIncidence);
        double maxIncidence = Math.max(fromIncidence, toIncidence);
        double minEmission = Math.min(fromEmission, toEmission);
        double maxEmission = Math.max(fromEmission, toEmission);
        double minPhase = Math.min(fromPhase, toPhase);
        double maxPhase = Math.max(fromPhase, toPhase);

        try
        {
            double minScDistance = Math.min(startDistance, stopDistance);
            double maxScDistance = Math.max(startDistance, stopDistance);

            HashMap<String, String> args = new HashMap<>();
            args.put("startDate", String.valueOf(startDate.getMillis()));
            args.put("stopDate", String.valueOf(stopDate.getMillis()));
            args.put("minScDistance", String.valueOf(minScDistance));
            args.put("maxScDistance", String.valueOf(maxScDistance));
            args.put("minIncidence", String.valueOf(minIncidence));
            args.put("maxIncidence", String.valueOf(maxIncidence));
            args.put("minEmission", String.valueOf(minEmission));
            args.put("maxEmission", String.valueOf(maxEmission));
            args.put("minPhase", String.valueOf(minPhase));
            args.put("maxPhase", String.valueOf(maxPhase));
            for (int i=0; i<4; ++i)
            {
                if (polygonTypes.contains(i))
                    args.put("polygonType"+i, "1");
                else
                    args.put("polygonType"+i, "0");
            }
            if (cubeList != null && cubeList.size() > 0)
            {
                String cubesStr = "";
                int size = cubeList.size();
                int count = 0;
                for (Integer i : cubeList)
                {
                    cubesStr += "" + i;
                    if (count < size-1)
                        cubesStr += ",";
                    ++count;
                }
                args.put("cubes", cubesStr);
            }

            results = doQuery("searchnis.php", constructUrlArguments(args));

            for (List<String> res : results)
            {
                String path = this.getNisPath(res);

                matchedImages.add(path);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return matchedImages;
    }

    @Override
    protected List<List<String>> getCachedResults(
            String pathToImageFolder
            )
    {
    	// Create a map of actual files, with key the segment of the
    	// file name that will match the output of getNisPath.
        final List<File> fileList = getCachedFiles(pathToImageFolder);
        final Map<String, File> filesFound = new TreeMap<>();
        for (File file: fileList)
        {
        	// Format for NIS path is /NIS/YYYY/..., basically everything after the
        	// cache directory prefix.
            String path = file.getPath().substring(Configuration.getCacheDir().length());
            filesFound.put(path, file);
        }

        final List<List<String>> result = new ArrayList<>();
        SortedMap<String, List<String>> inventory = getImageInventory();
        // Match the current inventory against the cached file map.
        for (Entry<String, List<String>> each: inventory.entrySet())
        {
            List<String> res = each.getValue();
            String path = this.getNisPath(res);
            if (filesFound.containsKey(path))
                result.add(res);
        }
        return result;
    }
}
