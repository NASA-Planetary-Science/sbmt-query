package edu.jhuapl.sbmt.query.fixedlist;

import java.util.List;

import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.SafeURLPaths;
import edu.jhuapl.saavtk.util.UnauthorizedAccessException;
import edu.jhuapl.sbmt.core.pointing.PointingSource;
import edu.jhuapl.sbmt.query.ISearchResultsMetadata;
import edu.jhuapl.sbmt.query.SearchResultsMetadata;
import edu.jhuapl.sbmt.query.v2.ISearchMetadata;

import edu.jhuapl.ses.jsqrl.api.Key;
import edu.jhuapl.ses.jsqrl.api.Metadata;
import edu.jhuapl.ses.jsqrl.api.Version;
import edu.jhuapl.ses.jsqrl.impl.FixedMetadata;
import edu.jhuapl.ses.jsqrl.impl.SettableMetadata;

/**
 * A query which simply returns a fixed list of images. No actual search is done.
 * Useful for getting a quick search working without having to update the database.
 */
public class FixedListQuery<T> extends FixedListQueryBase
{
    protected String rootPath;
    protected /*final*/ boolean multiSource;

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
    public FixedListQuery<T> copy()
    {
        return new FixedListQuery<T>(rootPath, galleryPath, multiSource);
    }

    @Override
    public String getDataPath()
    {
        return rootPath + "/images";
    }

    @Override
    public ISearchResultsMetadata<T> runQuery(ISearchMetadata queryMetadata)
    {
        FixedMetadata metadata = queryMetadata.getMetadata();
        String fileListRoot = metadata.get(FixedListSearchMetadata.FILE_LIST);
        String dataPath = metadata.get(FixedListSearchMetadata.DATA_PATH);
        String searchString = metadata.get(FixedListSearchMetadata.SEARCH_STRING);
        rootPath = metadata.get(FixedListSearchMetadata.ROOT_PATH);

        String fileListSuffix = null;
        PointingSource imageSource = PointingSource.valueFor(metadata.get(FixedListSearchMetadata.POINTING_SOURCE));
        switch (imageSource)
        {
            case GASKELL:
                fileListSuffix = "sum";
            break;
            case CORRECTED:
                fileListSuffix = "sum";
            break;
            case SPICE:
                fileListSuffix = "info";
            break;
            case CORRECTED_SPICE:
                fileListSuffix = "info";
            break;
            default:
                // No pointing-specific suffix, use a blank.
                fileListSuffix = "";
            break;
        }
        String fileList = getFileList(fileListRoot, fileListSuffix);

        List<List<String>> results;
       	results = getResultsFromFileListOnServer(rootPath + "/" /*+ dataListPrefix + "/"*/ + fileList, rootPath + "/" + dataPath + "/", getGalleryPath(), searchString);

        return SearchResultsMetadata.of("", results);   //"" should really be a query name here, if applicable
    }

    private String getFileList(final String fileList, String fileListSuffix)
    {
        // -----------------------------------------------
        // This whole section is just for backward compatibility with early (indirect) callers of this method. Need to
        // accept the following variations:
        // fileList == "imgagelist-sum.txt", "imagelist-sum", "imagelist.txt"
        String fileListRoot = fileList;
        if (fileListRoot.endsWith(".txt"))
        {
            fileListRoot = fileListRoot.substring(0, fileListRoot.length() - ".txt".length());
        }
        if (fileListRoot.endsWith("-info"))
        {
            if (!fileListSuffix.equals("info"))
            {
                throw new IllegalArgumentException("Mismatch between pointing type (" + fileListSuffix + ") and name of file list: " + fileList);
            }
            fileListRoot = fileListRoot.substring(0, fileListRoot.length() - "-info".length());
        }
        else if (fileListRoot.endsWith("-sum"))
        {
            if (!fileListSuffix.equals("sum"))
            {
                throw new IllegalArgumentException("Mismatch between pointing type (" + fileListSuffix + ") and name of file list: " + fileList);
            }
            fileListRoot = fileListRoot.substring(0, fileListRoot.length() - "-sum".length());
        }
        // End backward-compatibility section.
        // -----------------------------------------------

        // This is the "real" guts of this method.
        final String fileListWithoutSuffix = fileListRoot + ".txt";

        if (!fileListSuffix.isEmpty())
        {
            final String fileListWithSuffix = fileListRoot + "-" + fileListSuffix + ".txt";
            try
            {
                if (FileCache.instance().isAccessible(SafeURLPaths.instance().getString(rootPath, fileListWithSuffix)))
                {
                    return fileListWithSuffix;
                }
                else
                {
                    System.out.println("Could not find " + fileListWithSuffix + ". Trying " + fileListWithoutSuffix + " instead");
                }
            }
            catch (@SuppressWarnings("unused") UnauthorizedAccessException e)
            {
                System.out.println("Could not access " + fileListWithSuffix + ". Trying " + fileListWithoutSuffix + " instead");
            }
        }
        return fileListWithoutSuffix;
    }

    public String getRootPath()
    {
        return rootPath;
    }

    private static final Key<String> rootPathKey = Key.of("rootPath");
    private static final Key<Boolean> multiSourceKey = Key.of("multiSource");
    private static final Key<String> galleryPathKey = Key.of("galleryPath");

    @Override
    public Metadata store()
    {
        SettableMetadata configMetadata = SettableMetadata.of(Version.of(1, 0));
        write(rootPathKey, rootPath, configMetadata);
        write(multiSourceKey, multiSource, configMetadata);
        write(galleryPathKey, galleryPath, configMetadata);
        return configMetadata;
    }

    @Override
    public void retrieve(Metadata source)
    {
        rootPath = read(rootPathKey, source);
        multiSource = read(multiSourceKey, source);
        if (source.hasKey(galleryPathKey))
        	galleryPath = read(galleryPathKey, source);
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
