package edu.jhuapl.sbmt.query.database;

import java.util.List;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import edu.jhuapl.saavtk.metadata.FixedMetadata;
import edu.jhuapl.saavtk.metadata.Key;
import edu.jhuapl.saavtk.metadata.SettableMetadata;
import edu.jhuapl.saavtk.metadata.Version;
import edu.jhuapl.sbmt.query.SearchMetadata;

public class DatabaseSearchMetadata implements SearchMetadata
{
    private static final Version DatabaseSEARCH_DATA_VERSION = Version.of(1, 0);
    /*
     * This Pattern will match on either quoted text or text between commas,
     * including whitespace, and accounting for beginning and end of line. Cribbed
     * from a Stacktrace post.
     */
    private static final Pattern CSV_PATTERN = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");

    // Metadata keys.
    public static final Key<String> NAME = Key.of("Search Name");
    public static final Key<DateTime> START_DATE = Key.of("Start Date");
    public static final Key<DateTime> STOP_DATE = Key.of("Stop Date");
    public static final Key<Range<Double>> DISTANCE_RANGE = Key.of("Distance Range");
//    static final Key<Double> STOP_DISTANCE = Key.of("Stop Distance");
    public static final Key<String> SEARCH_STRING = Key.of("Search String");
    public static final Key<List<Integer>> POLYGON_TYPES = Key.of("Polygon Types");
    public static final Key<Range<Double>> INCIDENCE_RANGE = Key.of("Incidence Range");
    public static final Key<Range<Double>> EMISSION_RANGE = Key.of("Emission Range");
    public static final Key<Range<Double>> PHASE_RANGE = Key.of("Phase Range");
//    static final Key<Double> FROM_INCIDENCE = Key.of("From Incidence");
//    static final Key<Double> TO_INCIDENCE = Key.of("To Incidence");
//    static final Key<Double> FROM_EMISSION = Key.of("From Emission");
//    static final Key<Double> TO_EMISSION = Key.of("To Emission");
//    static final Key<Double> FROM_PHASE = Key.of("From Incidence");
//    static final Key<Double> TO_PHASE = Key.of("To Incidence");

//    static final Key<String> DATA_PATH = Key.of("Data path");
//    static final Key<String> POINTING_SOURCE = Key.of("Pointing Source");

    FixedMetadata searchMetadata;


    public static DatabaseSearchMetadata of(String name, DateTime startDate, DateTime stopDate,
            Range<Double> distanceRange, String searchString,
            List<Integer> polygonTypes, Range<Double> incidenceRange,
            Range<Double> emissionRange, Range<Double> phaseRange)
    {
        FixedMetadata metadata = FixedMetadata.of(createSettableMetadata(name, startDate, stopDate, distanceRange, searchString,
                                                polygonTypes, incidenceRange, emissionRange, phaseRange));
        return new DatabaseSearchMetadata(metadata);
    }

    protected static SettableMetadata createSettableMetadata(String name, DateTime startDate, DateTime stopDate,
                                                    Range<Double> distanceRange, String searchString,
                                                    List<Integer> polygonTypes, Range<Double> incidenceRange,
                                                    Range<Double> emissionRange, Range<Double> phaseRange)
    {
        Preconditions.checkNotNull(name);
//        Preconditions.checkNotNull(filelist);         //TODO: Update these checks
//        Preconditions.checkNotNull(datapath);
//        Preconditions.checkNotNull(pointingSource);

        SettableMetadata metadata = SettableMetadata.of(DatabaseSEARCH_DATA_VERSION);
        metadata.put(DatabaseSearchMetadata.NAME, name);

        metadata.put(DatabaseSearchMetadata.START_DATE, startDate);
        metadata.put(DatabaseSearchMetadata.STOP_DATE, stopDate);
        metadata.put(DatabaseSearchMetadata.DISTANCE_RANGE, distanceRange);
        metadata.put(DatabaseSearchMetadata.SEARCH_STRING, searchString);
        metadata.put(DatabaseSearchMetadata.POLYGON_TYPES, polygonTypes);
        metadata.put(DatabaseSearchMetadata.INCIDENCE_RANGE, incidenceRange);
        metadata.put(DatabaseSearchMetadata.EMISSION_RANGE, emissionRange);
        metadata.put(DatabaseSearchMetadata.PHASE_RANGE, phaseRange);
        return metadata;
//        return FixedMetadata.of(metadata);
    }

    private final FixedMetadata metadata;
    private boolean loadFailed;

    protected DatabaseSearchMetadata(FixedMetadata metadata)
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
        DatabaseSearchMetadata other = (DatabaseSearchMetadata) obj;
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
        return "DatabaseSearchMetadata [searchMetadata=" + searchMetadata
                + ", metadata=" + metadata + ", loadFailed=" + loadFailed + "]";
    }

    public DatabaseSearchMetadata copy()
    {
        return new DatabaseSearchMetadata(getMetadata().copy());
    }

    //TODO: Update these
//    public String getFilelist()
//    {
//        return getMetadata().get(FILE_LIST);
//    }
//
//    public String getDatapath()
//    {
//        return getMetadata().get(DATA_PATH);
//    }
//
//    public ImageSource getPointingSource()
//    {
//        String pointingSourceString = getMetadata().get(POINTING_SOURCE);
//        return ImageSource.valueOf(pointingSourceString);
//    }
}
