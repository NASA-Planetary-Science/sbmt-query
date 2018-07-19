package edu.jhuapl.sbmt.query.fixedlist;

import java.util.List;

import edu.jhuapl.saavtk.metadata.FixedMetadata;
import edu.jhuapl.sbmt.model.image.ImageSource;
import edu.jhuapl.sbmt.query.SearchMetadata;
import edu.jhuapl.sbmt.query.SearchResultsMetadata;

/**
 * A query which simply returns a fixed list of images. No actual search is done.
 * Useful for getting a quick search working without having to update the database.
 */
public class FixedListQuery extends FixedListQueryBase
{
    protected String rootPath;
    protected final boolean multiSource;

    public FixedListQuery()
    {
        this(null, null, false);
    }

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
    public SearchResultsMetadata runQuery(SearchMetadata queryMetadata)
    {
        FixedMetadata metadata = queryMetadata.getMetadata();
        String fileList = metadata.get(FixedListSearchMetadata.FILE_LIST);
        String dataPath = metadata.get(FixedListSearchMetadata.DATA_PATH);
        rootPath = metadata.get(FixedListSearchMetadata.ROOT_PATH);
        String dataListPrefix = "";

        ImageSource imageSource = ImageSource.valueFor(metadata.get(FixedListSearchMetadata.POINTING_SOURCE));
        System.out.println(imageSource.name());
        if (imageSource == ImageSource.GASKELL)
            dataListPrefix = "sumfiles";
        if (imageSource == ImageSource.CORRECTED)
            dataListPrefix = "sumfiles-corrected";
        else if (imageSource == ImageSource.CORRECTED_SPICE)
            dataListPrefix = "infofiles-corrected";
        System.out.println("FixedListQuery: runQuery: rootpath " + rootPath);
        System.out.println("FixedListQuery: runQuery (prefix, filelist, datapath): " + dataListPrefix + " " + fileList + " " + dataPath);
        List<List<String>> results = getResultsFromFileListOnServer(rootPath + "/" /*+ dataListPrefix + "/" */+ fileList, rootPath + "/" + dataPath + "/", getGalleryPath());

        return SearchResultsMetadata.of("", results);   //"" should really be a query name here, if applicable
    }

    public String getRootPath()
    {
        return rootPath;
    }

//    @Override
//    public List<List<String>> runFixedListQuery(
//            ImageSource imageSource,
//            String dataFolderOffRoot,
//            String dataListFileName)
//    {
//        String dataListPrefix = "";
//
//        if (multiSource)
//        {
//            if (imageSource == ImageSource.GASKELL)
//                dataListPrefix = "sumfiles";
//            if (imageSource == ImageSource.CORRECTED)
//                dataListPrefix = "sumfiles-corrected";
//            else if (imageSource == ImageSource.CORRECTED_SPICE)
//                dataListPrefix = "infofiles-corrected";
//        }
//
//        List<List<String>> results = getResultsFromFileListOnServer(rootPath + "/" + dataListPrefix + "/" + dataListFileName, rootPath + "/" + dataFolderOffRoot + "/", getGalleryPath());
//        return results;
//    }

}