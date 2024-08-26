package edu.jhuapl.sbmt.query.v2;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import edu.jhuapl.ses.jsqrl.api.Key;
import edu.jhuapl.ses.jsqrl.api.Version;
import edu.jhuapl.ses.jsqrl.impl.FixedMetadata;
import edu.jhuapl.ses.jsqrl.impl.SettableMetadata;

public class FixedListSearchMetadata implements ISearchMetadata
{
	private final FixedMetadata metadata;
	
    private static final Version FIXEDLISTSEARCH_DATA_VERSION = Version.of(1, 0);

    // Metadata keys.
    public static final Key<String> NAME = Key.of("Search Name");
    public static final Key<Long> START_DATE = Key.of("Start Date");
    public static final Key<Long> STOP_DATE = Key.of("Stop Date");
    public static final Key<String> SEARCH_STRING = Key.of("Search String");

    /**
     * Helper method to build a FixedListSearchMetadata object given the specified parameters
     * 
     * Example Usage:
     * 
     * 
     * 
     * @param name				The name of this query
     * @param searchString		The search string to use in this query
     * @return
     */
    public static FixedListSearchMetadata of(String name, String searchString)
    {
        FixedMetadata metadata = createMetadata(name, null, null, searchString);
        return new FixedListSearchMetadata(metadata);
    }
    
    /**
     * Helper method to build a FixedListSearchMetadata object given the specified parameters
     * 
     * Example Usage:
     * 
     * 
     * 
     * @param name				The name of this query
     * @param searchString		The search string to use in this query
     * @return
     */
    public static FixedListSearchMetadata of(String name, DateTime startDate, DateTime stopDate, String searchString)
    {
        FixedMetadata metadata = createMetadata(name, startDate, stopDate, searchString);
        return new FixedListSearchMetadata(metadata);
    }


    /**
     * Helper method to build a FixedListSearchMetadata object given the specified parameters
     * 
     * Example Usage:
     * 
     * 
     * 
     * @param name				The name of this query
	 * @param startDate			The start of the time range for this query
	 * @param stopDate			The end of the time range for this query
     * @param searchString		The search string to use in this query
     * @return
     */
    private static FixedMetadata createMetadata(String name, DateTime startDate, DateTime stopDate, String searchString)
    {
    	if (searchString == null) return createMetadata(name, startDate, stopDate, searchString);
        Preconditions.checkNotNull(name);

        SettableMetadata metadata = SettableMetadata.of(FIXEDLISTSEARCH_DATA_VERSION);
        metadata.put(FixedListSearchMetadata.NAME, name);

        metadata.put(FixedListSearchMetadata.SEARCH_STRING, searchString);
        if (startDate != null)
        {
	        metadata.put(FixedListSearchMetadata.START_DATE, startDate.getMillis());
	        metadata.put(FixedListSearchMetadata.STOP_DATE, stopDate.getMillis());
        }
        return FixedMetadata.of(metadata);
    }

    /**
     * Constructor.
     * 
     * @param metadata
     */
    protected FixedListSearchMetadata(FixedMetadata metadata)
    {
        this.metadata = metadata;
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
        result = prime * result
                + ((metadata == null) ? 0 : metadata.hashCode());
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
        if (metadata == null)
        {
            if (other.metadata != null)
                return false;
        }
        else if (!metadata.equals(other.metadata))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "FixedListSearchMetadata [metadata=" + metadata + "]";
    }

    
    /**
     * Returns the search string for this query
     * 
     * @return		String describing the search string to use for this query
     */
    public String getSearchString()
    {
        return getMetadata().get(SEARCH_STRING);
    }
    
    public DateTime getStartDate()
    {
    	return new DateTime(getMetadata().get(START_DATE));
    }
    
    public DateTime getStopDate()
    {
    	return new DateTime(getMetadata().get(STOP_DATE));
    }

	@Override
	public void setSearchString(String searchString)
	{
		// TODO Auto-generated method stub
		
	}

}
