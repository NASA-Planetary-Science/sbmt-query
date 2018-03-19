package edu.jhuapl.sbmt.query;

import java.util.ArrayList;
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
    protected final String rootPath;
    protected final boolean multiSource;

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
        super(galleryPath);
        this.rootPath = rootPath;
        this.multiSource = multiSource;
    }

    @Override
    public FixedListQuery clone()
    {
        return (FixedListQuery) super.clone();
    }

    @Override
    public String getDataPath()
    {
        return rootPath + "/images";
    }

    @Override
    public List<List<String>> runQuery(
            @SuppressWarnings("unused") String type,
            @SuppressWarnings("unused") DateTime startDate,
            @SuppressWarnings("unused") DateTime stopDate,
            @SuppressWarnings("unused") boolean sumOfProductsSearch,
            @SuppressWarnings("unused") List<Integer> camerasSelected,
            @SuppressWarnings("unused") List<Integer> filtersSelected,
            @SuppressWarnings("unused") double startDistance,
            @SuppressWarnings("unused") double stopDistance,
            @SuppressWarnings("unused") double startResolution,
            @SuppressWarnings("unused") double stopResolution,
            @SuppressWarnings("unused") String searchString,
            @SuppressWarnings("unused") List<Integer> polygonTypes,
            @SuppressWarnings("unused") double fromIncidence,
            @SuppressWarnings("unused") double toIncidence,
            @SuppressWarnings("unused") double fromEmission,
            @SuppressWarnings("unused") double toEmission,
            @SuppressWarnings("unused") double fromPhase,
            @SuppressWarnings("unused") double toPhase,
            @SuppressWarnings("unused") TreeSet<Integer> cubeList,
            ImageSource imageSource,
            @SuppressWarnings("unused") int limbType)
    {
        String spectrumListPrefix = "";

        if (multiSource)
        {
            if (imageSource == ImageSource.GASKELL)
                spectrumListPrefix = "sumfiles";
            if (imageSource == ImageSource.CORRECTED)
                spectrumListPrefix = "sumfiles-corrected";
            else if (imageSource == ImageSource.CORRECTED_SPICE)
                spectrumListPrefix = "infofiles-corrected";
        }

        List<List<String>> results = getResultsFromFileListOnServer(rootPath + "/" + spectrumListPrefix + "/imagelist.txt", rootPath + "/images/", getGalleryPath());

        if (searchString != null)
        {
            searchString = ".*/" + searchString;
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

}
