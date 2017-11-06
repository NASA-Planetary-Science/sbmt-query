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
    protected String rootPath;
    protected String galleryPath;
    protected String spectrumListPrefix;
    protected boolean multiSource;

    public FixedListQuery(String rootPath)
    {
        this(rootPath, null, false);
    }

    public FixedListQuery(String rootPath, String galleryPath)
    {
        this(rootPath, galleryPath, false);
    }

    public FixedListQuery(String rootPath, boolean multiSource)
    {
        this(rootPath, null, multiSource);
    }

    public FixedListQuery(String rootPath, String galleryPath, boolean multiSource)
    {
        this.rootPath = rootPath;
        this.galleryPath = galleryPath;
        this.spectrumListPrefix = "";
        this.multiSource = multiSource;
    }

    @Override
    public String getGalleryPath()
    {
        return galleryPath;
    }

    @Override
    public String getDataPath()
    {
        return rootPath + "/images";
    }

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
            ImageSource imageSource,
            int limbType)
    {
        spectrumListPrefix = "";

        if (multiSource)
        {
            if (imageSource == ImageSource.GASKELL)
                spectrumListPrefix = "sumfiles";
            if (imageSource == ImageSource.CORRECTED)
                spectrumListPrefix = "sumfiles-corrected";
            else if (imageSource == ImageSource.CORRECTED_SPICE)
                spectrumListPrefix = "infofiles-corrected";
        }

        List<List<String>> result = getResultsFromFileListOnServer(rootPath + "/" + spectrumListPrefix + "/imagelist.txt", rootPath + "/images/", galleryPath);

        return result;
    }

}
