package edu.jhuapl.sbmt.query.fixedlist;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import edu.jhuapl.saavtk.metadata.FixedMetadata;
import edu.jhuapl.saavtk.metadata.Key;
import edu.jhuapl.saavtk.metadata.SettableMetadata;
import edu.jhuapl.saavtk.metadata.Version;
import edu.jhuapl.sbmt.model.image.ImageSource;
import edu.jhuapl.sbmt.query.SearchMetadata;

public class FixedListSearchMetadata implements SearchMetadata
{
    private static final Version FIXEDLISTSEARCH_DATA_VERSION = Version.of(1, 0);
    /*
     * This Pattern will match on either quoted text or text between commas,
     * including whitespace, and accounting for beginning and end of line. Cribbed
     * from a Stacktrace post.
     */
    private static final Pattern CSV_PATTERN = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");

    // Metadata keys.
    static final Key<String> NAME = Key.of("Search Name");
    static final Key<String> FILE_LIST = Key.of("File list");
    static final Key<String> DATA_PATH = Key.of("Data path");
    static final Key<String> ROOT_PATH = Key.of("Root path");
    static final Key<String> POINTING_SOURCE = Key.of("Pointing Source");

    FixedMetadata searchMetadata;


    public static FixedListSearchMetadata of(String name, String filelist, String datapath, String rootPath, ImageSource pointingSource)
    {
        FixedMetadata metadata = createMetadata(name, filelist, datapath, rootPath, pointingSource);
        return new FixedListSearchMetadata(metadata);
    }

    private static FixedMetadata createMetadata(String name, String filelist, String datapath, String rootPath, ImageSource pointingSource)
    {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(filelist);
        Preconditions.checkNotNull(datapath);
        Preconditions.checkNotNull(pointingSource);

        SettableMetadata metadata = SettableMetadata.of(FIXEDLISTSEARCH_DATA_VERSION);
        metadata.put(FixedListSearchMetadata.NAME, name);

        metadata.put(FixedListSearchMetadata.FILE_LIST, filelist);
        metadata.put(FixedListSearchMetadata.DATA_PATH, datapath);
        metadata.put(FixedListSearchMetadata.ROOT_PATH, rootPath);
        metadata.put(FixedListSearchMetadata.POINTING_SOURCE, pointingSource.toString());
        return FixedMetadata.of(metadata);
    }

    private final FixedMetadata metadata;
    private boolean loadFailed;

    protected FixedListSearchMetadata(FixedMetadata metadata)
    {
        this.metadata = metadata;
        this.loadFailed = false;
    }

    public FixedMetadata getMetadata()
    {
        return metadata;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (loadFailed ? 1231 : 1237);
        result = prime * result
                + ((metadata == null) ? 0 : metadata.hashCode());
        result = prime * result
                + ((searchMetadata == null) ? 0 : searchMetadata.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FixedListSearchMetadata other = (FixedListSearchMetadata) obj;
        if (loadFailed != other.loadFailed)
            return false;
        if (metadata == null)
        {
            if (other.metadata != null)
                return false;
        }
        else if (!metadata.equals(other.metadata))
            return false;
        if (searchMetadata == null)
        {
            if (other.searchMetadata != null)
                return false;
        }
        else if (!searchMetadata.equals(other.searchMetadata))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "FixedListSearchMetadata [searchMetadata=" + searchMetadata
                + ", metadata=" + metadata + ", loadFailed=" + loadFailed + "]";
    }

    public FixedListSearchMetadata copy()
    {
        return new FixedListSearchMetadata(getMetadata().copy());
    }

    public String getFilelist()
    {
        return getMetadata().get(FILE_LIST);
    }

    public String getDatapath()
    {
        return getMetadata().get(DATA_PATH);
    }

    public String getRootpath()
    {
        return getMetadata().get(ROOT_PATH);
    }

    public ImageSource getPointingSource()
    {
        String pointingSourceString = getMetadata().get(POINTING_SOURCE);
        return ImageSource.valueOf(pointingSourceString);
    }

}
