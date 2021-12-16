package edu.jhuapl.sbmt.query.filter.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;

public final class FilterType<C>
{
	private static final Map<String, FilterType> FILTER_TYPE_IDENTIFIERS = new HashMap<>();

	/**
     * Provide a FilterType object for the given identifier parameter. If a
     * FilterType object already exists for this identity, it is simply
     * returned, otherwise it is created first.
     *
     * @param identifier of the FilterType object
     * @return the FilterType object
     */
    public static <C> FilterType<C> provide(String identifier, Class<C> filterType, C[] values, String queryBaseString)
    {
        Preconditions.checkNotNull(identifier);

        FilterType<C> result = FILTER_TYPE_IDENTIFIERS.get(identifier);
        if (result == null)
        {
        	var rangeArray = new ArrayList<C>();
            for (C val : values) rangeArray.add(val);
            result = new FilterType<C>(identifier, filterType, rangeArray, Optional.ofNullable(null), queryBaseString);
            FILTER_TYPE_IDENTIFIERS.put(identifier, result);
        }

        return result;
    }

    /**
     * Provide a FilterType object for the given identifier parameter. If a
     * FilterType object already exists for this identity, it is simply
     * returned, otherwise it is created first.
     *
     * @param identifier of the FilterType object
     * @return the FilterType object
     */
    public static FilterType provide(String identifier)
    {
        Preconditions.checkNotNull(identifier);

        FilterType result = FILTER_TYPE_IDENTIFIERS.get(identifier);
        if (result == null)
        {
            result = new FilterType(identifier);
            FILTER_TYPE_IDENTIFIERS.put(identifier, result);
        }

        return result;
    }

    public static Collection<FilterType> getRegisteredFilters()
    {
    	return FILTER_TYPE_IDENTIFIERS.values();
    }

    /**
     * Create and return a new FilterType object identified with the provided
     * identifier string.
     *
     * @param identifier of the FilterType object
     * @return the FilterType object
     * @throws IllegalArgumentException if there is already a FilterType object
     *             associated with the identifier
     */
    private static <C> FilterType<C> create(String identifier, Optional<FilterTypeUnit> unit, Class<C> filterType, Pair<C, C> range, String queryBaseString)
    {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(!FILTER_TYPE_IDENTIFIERS.containsKey(identifier), "Already have a Filter Type object for identifier " + identifier);

        var rangeArray = new ArrayList<C>();
        rangeArray.add(range.getLeft());
        rangeArray.add(range.getRight());
        FilterType<C> result = new FilterType<C>(identifier, filterType, rangeArray, unit, queryBaseString);
        FILTER_TYPE_IDENTIFIERS.put(identifier, result);

        return result;
    }

    /**
     * Create and return a new FilterType object identified with the provided
     * identifier string.
     *
     * @param identifier of the FilterType object
     * @return the FilterType object
     * @throws IllegalArgumentException if there is already a FilterType object
     *             associated with the identifier
     */
    private static <C> FilterType<C> create(String identifier, Class<C> filterType, C[] values, String queryBaseString)
    {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(!FILTER_TYPE_IDENTIFIERS.containsKey(identifier), "Already have a Filter Type object for identifier " + identifier);

        var rangeArray = new ArrayList<C>();
        for (C val : values) rangeArray.add(val);
        FilterType<C> result = new FilterType<C>(identifier, filterType, rangeArray, Optional.ofNullable(null), queryBaseString);
        FILTER_TYPE_IDENTIFIERS.put(identifier, result);

        return result;
    }

    public static boolean contains(String identifier)
    {
        Preconditions.checkNotNull(identifier);
        return FILTER_TYPE_IDENTIFIERS.containsKey(identifier);
    }

    public static final FilterType<Double> INCIDENCE_ANGLE = create("Incidence Angle", Optional.of(FilterTypeUnit.DEGREES), Double.class, Pair.of(0.0, 180.0), "Incidence");
    public static final FilterType<Double> EMISSION_ANGLE = create("Emission Angle",  Optional.of(FilterTypeUnit.DEGREES), Double.class, Pair.of(0.0, 180.0), "Emission");
    public static final FilterType<Double> PHASE_ANGLE = create("Phase Angle",  Optional.of(FilterTypeUnit.DEGREES), Double.class, Pair.of(0.0, 180.0), "Phase");
    public static final FilterType<Double> RESOLUTION = create("Image Resolution",  Optional.of(FilterTypeUnit.METERS_PER_PIXEL), Double.class, Pair.of(0.0, 50.0), "Resolution");
    public static final FilterType<Double> SC_DISTANCE = create("SC Distance",  Optional.of(FilterTypeUnit.KM), Double.class, Pair.of(0.0, 1000.0), "ScDistance");
    public static final FilterType<Double> SC_ALTITUDE = create("SC Altitude",  Optional.of(FilterTypeUnit.KM), Double.class, Pair.of(0.0, 1000.0), "ScAltitude");
    public static final FilterType<String> LIMB = create("Limb", String.class, new String[] {"with only", "without only", "with or without"}, "limbType");
    public static final FilterType<String> IMAGE_POINTING = create("Image Pointing", String.class, new String[] {"SPC Derived", "SPICE Derivied"}, "Pointing");
    public static final FilterType<Date> TIME_WINDOW = create("Time Window", Optional.of(FilterTypeUnit.provide("Time Window")), Date.class, Pair.of(new Date(), new Date()), "Date");	//TODO: replace the low bound with a good default


    private final String identifier;
    private Class<C> type;
    private ArrayList<C> range;
    private FilterTypeUnit unit;
    private C selectedRangeValue;
    private String queryBaseString;

    private FilterType(String identifier)
    {
    	 this.identifier = identifier;
    }

    private FilterType(String identifier, Class<C> type)
    {
    	 this(identifier);
         this.type = type;
    }

    private FilterType(String identifier, Class<C> type, ArrayList<C> range, Optional<FilterTypeUnit> unit, String queryBaseString)
    {
        this(identifier, type);
        this.range = range;
        unit.ifPresent(filterUnit -> this.unit = filterUnit);
        this.queryBaseString = queryBaseString;
//        if (unit.isPresent()) this.unit = unit;
    }

    public String name()
    {
        return identifier;
    }

    /**
	 * @return the type
	 */
	public Class<C> getType()
	{
		return type;
	}

	/**
	 * @return the range
	 */
	public ArrayList<C> getRange()
	{
		return range;
	}

	/**
	 * @return the unit
	 */
	public FilterTypeUnit getUnit()
	{
		return unit;
	}

	public C getSelectedRangeValue()
	{
		return selectedRangeValue;
	}

	public void setSelectedRangeValue(C value)
	{
		System.out.println("FilterType: setSelectedRangeValue: setting selected " + value);
		this.selectedRangeValue = value;
	}

	@Override
    public String toString()
    {
        return identifier;
    }

	public HashMap<String, String> getSQLArguments()
	{
		HashMap<String, String> args = new HashMap<>();
		if (selectedRangeValue != null)
		{
			args.put(queryBaseString, String.valueOf(selectedRangeValue));
		}
		else
		{
			args.put("min" + queryBaseString, String.valueOf(range.get(0)));
			args.put("max" + queryBaseString, String.valueOf(range.get(1)));
		}
		return args;
	}
}
