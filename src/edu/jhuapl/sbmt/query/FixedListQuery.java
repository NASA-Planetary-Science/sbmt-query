package edu.jhuapl.sbmt.query;

import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;

import edu.jhuapl.sbmt.model.image.ImageSource;

/**
 * A query which simply returns a fixed list of images. No actual search is done.
 * Useful for getting a quick search working without having to update the database.
 */
public class FixedListQuery extends QueryBase
{
    private String rootPath;
    private String imageListPrefix;
    private boolean multiSource;

    public FixedListQuery(String rootPath)
    {
        this.rootPath = rootPath;
        imageListPrefix = "";
        this.multiSource = false;
    }

    public FixedListQuery(String rootPath, boolean multiSource)
    {
        this.rootPath = rootPath;
        this.imageListPrefix = "";
        this.multiSource = multiSource;
    }

    @Override
    public String getImagesPath()
    {
        return rootPath + "/images";
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
        imageListPrefix = "";

        if (multiSource)
        {
            if (imageSource == ImageSource.GASKELL)
                imageListPrefix = "sumfiles";
            if (imageSource == ImageSource.CORRECTED)
                imageListPrefix = "sumfiles-corrected";
            else if (imageSource == ImageSource.CORRECTED_SPICE)
                imageListPrefix = "infofiles-corrected";
        }

        List<List<String>> result = getResultsFromFileListOnServer(rootPath + "/" + imageListPrefix + "/imagelist.txt", rootPath + "/images/");

        return result;
    }

}
