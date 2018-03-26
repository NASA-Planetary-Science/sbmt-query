package edu.jhuapl.sbmt.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;

import edu.jhuapl.sbmt.client.SmallBodyViewConfig;
import edu.jhuapl.sbmt.model.image.ImageSource;

public class GenericPhpQuery extends QueryBase
{
    private final String rootPath;
    private final String tablePrefix;

    @Override
    public GenericPhpQuery clone()
    {
        return (GenericPhpQuery) super.clone();
    }

    public GenericPhpQuery(String rootPath, String tablePrefix)
    {
        this(rootPath, tablePrefix, null);
    }

    public GenericPhpQuery(String rootPath, String tablePrefix, String galleryPath)
    {
        super(galleryPath);
        this.rootPath = rootPath;
        this.tablePrefix = tablePrefix.toLowerCase();
    }

    @Override
    public String getDataPath()
    {
        return rootPath + "/images";
    }

    // Append the full path to the image gallery to this search result.
    private void addGalleryFullPath(List<String> result)
    {
        final String galleryPath = getGalleryPath();
        if(galleryPath == null)
        {
            result.add(null);
        }
        else
        {
            result.add(galleryPath + "/" + result.get(0));
        }
    }

    // Convert the 0th element of the result (the path to the image)
    // with the full path, but only if the result does not already have
    // a full path.
    private void changeImagePathToFullPath(List<String> result)
    {
        String fullPath = result.get(0);
        if (!fullPath.contains("/"))
        {
            result.set(0, rootPath + "/images/" + fullPath);
        }
    }

    @Override
    public List<List<String>> runQuery(
            @SuppressWarnings("unused") String type,
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
            @SuppressWarnings("unused") List<Integer> polygonTypes,
            double fromIncidence,
            double toIncidence,
            double fromEmission,
            double toEmission,
            double fromPhase,
            double toPhase,
            TreeSet<Integer> cubeList,
            ImageSource imageSource,
            int limbType)
    {
        final String galleryPath = getGalleryPath();
        if (imageSource == ImageSource.CORRECTED)
        {
            return getResultsFromFileListOnServer(rootPath + "/sumfiles-corrected/imagelist.txt",
                    rootPath + "/images/", galleryPath);
        }
        else if (imageSource == ImageSource.CORRECTED_SPICE)
        {
            return getResultsFromFileListOnServer(rootPath + "/infofiles-corrected/imagelist.txt",
                    rootPath + "/images/", galleryPath);
        }
        /*else if (imageSource == ImageSource.GASKELL_UPDATED)
        {
            return getResultsFromFileListOnServer(rootPath + "/sumfiles_to_be_delivered/imagelist.txt",
                    rootPath + "/images/", galleryPath);
        }*/

        List<List<String>> results = new ArrayList<>();

        double minIncidence = Math.min(fromIncidence, toIncidence);
        double maxIncidence = Math.max(fromIncidence, toIncidence);
        double minEmission = Math.min(fromEmission, toEmission);
        double maxEmission = Math.max(fromEmission, toEmission);
        double minPhase = Math.min(fromPhase, toPhase);
        double maxPhase = Math.max(fromPhase, toPhase);

        // Get table name.  Examples: erosimages_gaskell, amicacubes_pds_beta
        String imagesDatabase = tablePrefix + "images_" + imageSource.getDatabaseTableName();
        String cubesDatabase = tablePrefix + "cubes_" + imageSource.getDatabaseTableName();
        if(SmallBodyViewConfig.betaMode)
        {
            imagesDatabase += "_beta";
            cubesDatabase += "_beta";
        }

        if (searchString != null)
        {
            HashMap<String, String> args = new HashMap<>();
            args.put("imagesDatabase", imagesDatabase);
            args.put("imageSource", imageSource.toString());
            args.put("searchString", searchString);

            results = doQuery("searchimages.php", constructUrlArguments(args));

            if (results != null && results.size() > 0)
            {
                for (List<String> res : results)
                {
                    this.addGalleryFullPath(res);
                    this.changeImagePathToFullPath(res);
                }
            }
            return results;
        }

        try
        {
            double minScDistance = Math.min(startDistance, stopDistance);
            double maxScDistance = Math.max(startDistance, stopDistance);
            double minResolution = Math.min(startResolution, stopResolution) / 1000.0;
            double maxResolution = Math.max(startResolution, stopResolution) / 1000.0;

            HashMap<String, String> args = new HashMap<>();
            args.put("imagesDatabase", imagesDatabase);
            args.put("cubesDatabase", cubesDatabase);
            args.put("minResolution", String.valueOf(minResolution));
            args.put("maxResolution", String.valueOf(maxResolution));
            args.put("minScDistance", String.valueOf(minScDistance));
            args.put("maxScDistance", String.valueOf(maxScDistance));
            args.put("startDate", String.valueOf(startDate.getMillis()));
            args.put("stopDate", String.valueOf(stopDate.getMillis()));
            args.put("minIncidence", String.valueOf(minIncidence));
            args.put("maxIncidence", String.valueOf(maxIncidence));
            args.put("minEmission", String.valueOf(minEmission));
            args.put("maxEmission", String.valueOf(maxEmission));
            args.put("minPhase", String.valueOf(minPhase));
            args.put("maxPhase", String.valueOf(maxPhase));
            args.put("limbType", String.valueOf(limbType));

            // Populate args for camera and filter search
            if(sumOfProductsSearch)
            {
                // Sum of products (hierarchical) search: (CAMERA 1 AND FILTER 1) OR ... OR (CAMERA N AND FILTER N)
                args.put("sumOfProductsSearch", "1");
                Integer[] camerasSelectedArray = camerasSelected.toArray(new Integer[0]);
                Integer[] filtersSelectedArray = filtersSelected.toArray(new Integer[0]);
                int numProducts = camerasSelectedArray.length;

                // Populate search parameters
                args.put("numProducts", new Integer(numProducts).toString());
                for(int i=0; i<numProducts; i++)
            {
                    args.put("cameraType"+i, new Integer(camerasSelectedArray[i]+1).toString());
                    args.put("filterType"+i, new Integer(filtersSelectedArray[i]+1).toString());
                }
            }
                else
            {
                // Product of sums (legacy) search: (CAMERA 1 OR ... OR CAMERA N) AND (FILTER 1 OR ... FILTER M)
                args.put("sumOfProductsSearch", "0");

                // Populate search parameters
                for(Integer c : camerasSelected)
                {
                    args.put("cameraType"+(c+1), "1");
            }
                for(Integer f : filtersSelected)
            {
                    args.put("filterType"+(f+1), "1");
                }
            }
            if (cubeList != null && cubeList.size() > 0)
            {
                String cubes = "";
                int size = cubeList.size();
                int count = 0;
                for (Integer i : cubeList)
                {
                    cubes += "" + i;
                    if (count < size-1)
                        cubes += ",";
                    ++count;
                }
                args.put("cubes", cubes);
            }

            results = doQuery("searchimages.php", constructUrlArguments(args));

            for (List<String> res : results)
            {
                this.addGalleryFullPath(res);
                this.changeImagePathToFullPath(res);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            results = getResultsFromFileListOnServer(rootPath + "/imagelist.txt", getDataPath(), getGalleryPath());
        }

        return results;
    }

    public String getTablePrefix()
    {
        return tablePrefix;
    }
}
