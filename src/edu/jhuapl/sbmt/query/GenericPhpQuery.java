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
    private String rootPath;
    private String tablePrefix;
    private String galleryPath;

    public GenericPhpQuery clone()
    {
        return new GenericPhpQuery(rootPath, tablePrefix, galleryPath);
    }

    public GenericPhpQuery(String rootPath, String tablePrefix)
    {
        this(rootPath, tablePrefix, null);
    }

    public GenericPhpQuery(String rootPath, String tablePrefix, String galleryPath)
    {
        this.rootPath = rootPath;
        this.tablePrefix = tablePrefix.toLowerCase();
        this.galleryPath = galleryPath;
    }

    public String getImagesPath()
    {
        return rootPath + "/images";
    }

    public String getGalleryPath()
    {
        return galleryPath;
    }

    private void setGalleryFullPath(List<String> result)
    {
        if(galleryPath == null)
        {
            result.add(null);
        }
        else
        {
            result.add(galleryPath + "/" + result.get(0));
        }
    }

    private void changePathToFullPath(List<String> result)
    {
        result.set(0, rootPath + "/images/" + result.get(0));
    }

    @Override
    public List<List<String>> runQuery(
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
            int limbType)
    {
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

        List<List<String>> results = new ArrayList<List<String>>();

        double minIncidence = Math.min(fromIncidence, toIncidence);
        double maxIncidence = Math.max(fromIncidence, toIncidence);
        double minEmission = Math.min(fromEmission, toEmission);
        double maxEmission = Math.max(fromEmission, toEmission);
        double minPhase = Math.min(fromPhase, toPhase);
        double maxPhase = Math.max(fromPhase, toPhase);

        String imagesDatabase = "";
        String cubesDatabase = "";
        if (imageSource == ImageSource.GASKELL)
        {
            imagesDatabase = tablePrefix + "images_gaskell";
            cubesDatabase = tablePrefix + "cubes_gaskell";
        }
        else
        {
            imagesDatabase = tablePrefix + "images_pds";
            cubesDatabase = tablePrefix + "cubes_pds";
        }

        String tablePostfix = SmallBodyViewConfig.betaMode ? "_beta" : "";
        imagesDatabase += tablePostfix;
        cubesDatabase += tablePostfix;

        if (searchString != null)
        {
            HashMap<String, String> args = new HashMap<String, String>();
            args.put("imagesDatabase", imagesDatabase);
            args.put("imageSource", imageSource.toString());
            args.put("searchString", searchString);

            results = doQuery("searchimages.php", constructUrlArguments(args));

            if (results != null && results.size() > 0)
            {
                for (List<String> res : results)
                {
                    this.setGalleryFullPath(res);
                    this.changePathToFullPath(res);
                }
            }
            return results;
        }

        List<Integer> filters = new ArrayList<Integer>();
        for (int i=0; i<filtersChecked.size(); ++i)
        {
            if (filtersChecked.get(i))
            {
                filters.add(i+1);
            }
        }
        if (!filtersChecked.isEmpty() && filters.isEmpty())
            return results;

        List<Integer> cameras = new ArrayList<Integer>();
        for (int i=0; i<camerasChecked.size(); ++i)
        {
            if (camerasChecked.get(i))
            {
                cameras.add(i+1);
            }
        }
        if (!camerasChecked.isEmpty() && cameras.isEmpty())
            return results;

        try
        {
            double minScDistance = Math.min(startDistance, stopDistance);
            double maxScDistance = Math.max(startDistance, stopDistance);
            double minResolution = Math.min(startResolution, stopResolution) / 1000.0;
            double maxResolution = Math.max(startResolution, stopResolution) / 1000.0;

            HashMap<String, String> args = new HashMap<String, String>();
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
            for (int i=1; i<=filtersChecked.size(); ++i)
            {
                if (filters.contains(i))
                    args.put("filterType"+i, "1");
                else
                    args.put("filterType"+i, "0");
            }
            for (int i=1; i<=camerasChecked.size(); ++i)
            {
                if (cameras.contains(i))
                    args.put("cameraType"+i, "1");
                else
                    args.put("cameraType"+i, "0");
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
                this.setGalleryFullPath(res);
                this.changePathToFullPath(res);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return results;
    }

}
