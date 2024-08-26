package edu.jhuapl.sbmt.query.fixedlist;

import com.google.common.base.Preconditions;

import edu.jhuapl.sbmt.core.pointing.PointingSource;
import edu.jhuapl.sbmt.query.v2.ISearchMetadata;

import edu.jhuapl.ses.jsqrl.api.Key;
import edu.jhuapl.ses.jsqrl.api.Version;
import edu.jhuapl.ses.jsqrl.impl.FixedMetadata;
import edu.jhuapl.ses.jsqrl.impl.SettableMetadata;

public class FixedListSearchMetadata implements ISearchMetadata
{
    private static final Version FIXEDLISTSEARCH_DATA_VERSION = Version.of(1, 0);

    // Metadata keys.
    public static final Key<String> NAME = Key.of("Search Name");
    static final Key<String> FILE_LIST = Key.of("File list");
    public static final Key<String> DATA_PATH = Key.of("Data path");
    public static final Key<String> ROOT_PATH = Key.of("Root path");
    static final Key<String> POINTING_SOURCE = Key.of("Pointing Source");
    static final Key<String> SEARCH_STRING = Key.of("Search String");

    FixedMetadata searchMetadata;
    private String searchString = "";

    public static FixedListSearchMetadata of(String name, String filelist, String datapath, String rootPath, PointingSource pointingSource)
    {
        FixedMetadata metadata = createMetadata(name, filelist, datapath, rootPath, pointingSource);
        return new FixedListSearchMetadata(metadata);
    }

    public static FixedListSearchMetadata of(String name, String filelist, String datapath, String rootPath, PointingSource pointingSource, String searchString)
    {
        FixedMetadata metadata = createMetadata(name, filelist, datapath, rootPath, pointingSource, searchString);
        return new FixedListSearchMetadata(metadata);
    }

    private static FixedMetadata createMetadata(String name, String filelist, String datapath, String rootPath, PointingSource pointingSource)
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
        metadata.put(FixedListSearchMetadata.SEARCH_STRING, null);
        return FixedMetadata.of(metadata);
    }

    private static FixedMetadata createMetadata(String name, String filelist, String datapath, String rootPath, PointingSource pointingSource, String searchString)
    {
    	if (searchString == null) return createMetadata(name, filelist, datapath, rootPath, pointingSource);
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
        metadata.put(FixedListSearchMetadata.SEARCH_STRING, searchString);
        return FixedMetadata.of(metadata);
    }

    private final FixedMetadata metadata;
    private boolean loadFailed;

    protected FixedListSearchMetadata(FixedMetadata metadata)
    {
        this.metadata = metadata;
        this.loadFailed = false;
    }

    @Override
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

    public String getDatapath()
    {
        return getMetadata().get(DATA_PATH);
    }

    public PointingSource getPointingSource()
    {
        String pointingSourceString = getMetadata().get(POINTING_SOURCE);
        return PointingSource.valueOf(pointingSourceString);
    }

    public String getSearchString()
	{
		return searchString;
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

}
