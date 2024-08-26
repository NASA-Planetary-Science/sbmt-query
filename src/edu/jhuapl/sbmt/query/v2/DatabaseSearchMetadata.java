package edu.jhuapl.sbmt.query.v2;

import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import edu.jhuapl.ses.jsqrl.api.Key;
import edu.jhuapl.ses.jsqrl.api.Version;
import edu.jhuapl.ses.jsqrl.impl.FixedMetadata;
import edu.jhuapl.ses.jsqrl.impl.SettableMetadata;

/**
 * @author steelrj1
 *
 */
public class DatabaseSearchMetadata extends FixedListSearchMetadata //implements ISearchMetadata
{
    private static final Version DatabaseSEARCH_DATA_VERSION = Version.of(1, 0);

    // Metadata keys.
    public static final Key<String> NAME = Key.of("Search Name");
//    public static final Key<Long> START_DATE = Key.of("Start Date");
//    public static final Key<Long> STOP_DATE = Key.of("Stop Date");
//    public static final Key<String> SEARCH_STRING = Key.of("Search String");
    public static final Key<List<Integer>> POLYGON_TYPES = Key.of("Polygon Types");
    public static final Key<Double> FROM_INCIDENCE = Key.of("From Incidence");
    public static final Key<Double> TO_INCIDENCE = Key.of("To Incidence");
    public static final Key<Double> FROM_EMISSION = Key.of("From Emission");
    public static final Key<Double> TO_EMISSION = Key.of("To Emission");
    public static final Key<Double> FROM_PHASE = Key.of("From Incidence");
    public static final Key<Double> TO_PHASE = Key.of("To Incidence");
    public static final Key<Double> FROM_DISTANCE = Key.of("From Distance");
    public static final Key<Double> TO_DISTANCE = Key.of("To Distance");

    /**
     * @param name
     * @param startDate
     * @param stopDate
     * @param distanceRange
     * @param searchString
     * @param polygonTypes
     * @param incidenceRange
     * @param emissionRange
     * @param phaseRange
     * @return
     */
    public static DatabaseSearchMetadata of(String name, DateTime startDate, DateTime stopDate,
            Range<Double> distanceRange, String searchString,
            List<Integer> polygonTypes, Range<Double> incidenceRange,
            Range<Double> emissionRange, Range<Double> phaseRange)
    {
        FixedMetadata metadata = FixedMetadata.of(createSettableMetadata(name, startDate, stopDate, distanceRange, searchString,
                                                polygonTypes, incidenceRange, emissionRange, phaseRange));
        return new DatabaseSearchMetadata(metadata);
    }

    /**
     * @param name
     * @param startDate
     * @param stopDate
     * @param distanceRange
     * @param searchString
     * @param polygonTypes
     * @param incidenceRange
     * @param emissionRange
     * @param phaseRange
     * @return
     */
    protected static SettableMetadata createSettableMetadata(String name, DateTime startDate, DateTime stopDate,
                                                    Range<Double> distanceRange, String searchString,
                                                    List<Integer> polygonTypes, Range<Double> incidenceRange,
                                                    Range<Double> emissionRange, Range<Double> phaseRange)
    {
        Preconditions.checkNotNull(name);

        SettableMetadata metadata = SettableMetadata.of(DatabaseSEARCH_DATA_VERSION);
        metadata.put(DatabaseSearchMetadata.NAME, name);

        metadata.put(DatabaseSearchMetadata.START_DATE, startDate.getMillis());
        metadata.put(DatabaseSearchMetadata.STOP_DATE, stopDate.getMillis());
        metadata.put(DatabaseSearchMetadata.FROM_DISTANCE, distanceRange.lowerEndpoint());
        metadata.put(DatabaseSearchMetadata.TO_DISTANCE, distanceRange.upperEndpoint());
        metadata.put(DatabaseSearchMetadata.SEARCH_STRING, searchString);
        metadata.put(DatabaseSearchMetadata.POLYGON_TYPES, polygonTypes);
        metadata.put(DatabaseSearchMetadata.FROM_INCIDENCE, incidenceRange.lowerEndpoint());
        metadata.put(DatabaseSearchMetadata.FROM_EMISSION, emissionRange.lowerEndpoint());
        metadata.put(DatabaseSearchMetadata.FROM_PHASE, phaseRange.lowerEndpoint());
        metadata.put(DatabaseSearchMetadata.TO_INCIDENCE, incidenceRange.upperEndpoint());
        metadata.put(DatabaseSearchMetadata.TO_EMISSION, emissionRange.upperEndpoint());
        metadata.put(DatabaseSearchMetadata.TO_PHASE, phaseRange.upperEndpoint());
        return metadata;
    }

    /**
     * 
     */
    private final FixedMetadata metadata;
    
    private String searchString;

    /**
     * @param metadata
     */
    protected DatabaseSearchMetadata(FixedMetadata metadata)
    {
    	super(metadata);
        this.metadata = metadata;
    }

    /**
     *
     */
    public FixedMetadata getMetadata()
    {
        return metadata;
    }
    
    
	public String getSearchString()
	{
		return searchString;
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
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
        DatabaseSearchMetadata other = (DatabaseSearchMetadata) obj;
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
        return "DatabaseSearchMetadata [metadata=" + metadata + "]";
    }
}
