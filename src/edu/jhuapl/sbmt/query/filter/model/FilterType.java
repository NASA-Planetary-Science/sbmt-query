package edu.jhuapl.sbmt.query.filter.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;

import edu.jhuapl.sbmt.util.TimeUtil;

public final class FilterType<C> implements Cloneable
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
    public static <C> FilterType<C> provideDynamic(String identifier, Class<C> filterType, DynamicFilterValues<C> dynamicValues, String queryBaseString)
    {
        Preconditions.checkNotNull(identifier);

        FilterType<C> result = FILTER_TYPE_IDENTIFIERS.get(identifier);
        if (result == null)
        {
//        	var rangeArray = new ArrayList<C>();
//            for (C val : dynamicValues.getCurrentValues()) rangeArray.add(val);
//            System.out.println("FilterType: provideDynamic: number of values " + rangeArray.size());
//            result = new FilterType<C>(identifier, filterType, rangeArray, Optional.ofNullable(null), queryBaseString);
            result = new FilterType<C>(identifier, filterType, dynamicValues, Optional.ofNullable(null), queryBaseString);
            FILTER_TYPE_IDENTIFIERS.put(identifier, result);
        }

        return result;
    }

//    public void updateDynamicValues(String identifier)
//    {
//    	 Preconditions.checkNotNull(identifier);
//
//         FilterType<C> result = FILTER_TYPE_IDENTIFIERS.get(identifier);
//         if (result != null)
//         {
//
//         }
//    }

//	/**
//     * Provide a FilterType object for the given identifier parameter. If a
//     * FilterType object already exists for this identity, it is simply
//     * returned, otherwise it is created first.
//     *
//     * @param identifier of the FilterType object
//     * @return the FilterType object
//     */
//    public static <C> FilterType<C> provide(String identifier, Class<C> filterType, C[] values, String queryBaseString)
//    {
//        Preconditions.checkNotNull(identifier);
//
//        FilterType<C> result = FILTER_TYPE_IDENTIFIERS.get(identifier);
//        if (result == null)
//        {
//        	var rangeArray = new ArrayList<C>();
//            for (C val : values) rangeArray.add(val);
//            result = new FilterType<C>(identifier, filterType, rangeArray, Optional.ofNullable(null), queryBaseString);
//            FILTER_TYPE_IDENTIFIERS.put(identifier, result);
//        }
//
//        return result;
//    }

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

    public static FilterType createInstance(String identifier) throws CloneNotSupportedException
    {
    	Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(FILTER_TYPE_IDENTIFIERS.containsKey(identifier), "No suck identifier " + identifier);
        return (FilterType)(FILTER_TYPE_IDENTIFIERS.get(identifier).clone());
    }

    public static final FilterType<Double> INCIDENCE_ANGLE = create("Incidence Angle", Optional.of(FilterTypeUnit.DEGREES), Double.class, Pair.of(0.0, 180.0), "Incidence");
    public static final FilterType<Double> EMISSION_ANGLE = create("Emission Angle",  Optional.of(FilterTypeUnit.DEGREES), Double.class, Pair.of(0.0, 180.0), "Emission");
    public static final FilterType<Double> PHASE_ANGLE = create("Phase Angle",  Optional.of(FilterTypeUnit.DEGREES), Double.class, Pair.of(0.0, 180.0), "Phase");
    public static final FilterType<Double> RESOLUTION = create("Image Resolution",  Optional.of(FilterTypeUnit.METERS_PER_PIXEL), Double.class, Pair.of(0.0, 50.0), "Resolution");
    public static final FilterType<Double> SC_DISTANCE = create("SC Distance",  Optional.of(FilterTypeUnit.KM), Double.class, Pair.of(0.0, 1000.0), "ScDistance");
    public static final FilterType<Double> SC_ALTITUDE = create("SC Altitude",  Optional.of(FilterTypeUnit.KM), Double.class, Pair.of(0.0, 1000.0), "ScAltitude");
    public static final FilterType<String> LIMB = create("Limb", String.class, new String[] {"with only", "without only", "with or without"}, "limbType");
    public static final FilterType<String> IMAGE_POINTING = create("Image Pointing", String.class, new String[] {"SPC Derived", "SPICE Derivied"}, "Pointing");
    public static final FilterType<LocalDateTime> TIME_WINDOW = create("Time Window", Optional.of(FilterTypeUnit.provide("Time Window")), LocalDateTime.class, Pair.of(LocalDateTime.now(), LocalDateTime.now()), "tdb");	//TODO: replace the low bound with a good default


    private final String identifier;
    private Class<C> type;
    private ArrayList<C> range;
    DynamicFilterValues<C> dynamicValues;
    private FilterTypeUnit unit;
    private C selectedRangeValue;
    private String queryBaseString;
    private boolean enabled = false;
    private UUID index;
//    private Function<Pair, Pair> converter;

    private FilterType(String identifier)
    {
    	this.index = UUID.randomUUID();
    	this.identifier = identifier;
    }

    private FilterType(String identifier, Class<C> type)
    {
    	this(identifier);
        this.type = type;
    }


    @Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dynamicValues == null) ? 0 : dynamicValues.hashCode());
		result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
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
		FilterType other = (FilterType) obj;
		if (dynamicValues == null)
		{
			if (other.dynamicValues != null)
				return false;
		}
		else if (!dynamicValues.equals(other.dynamicValues))
			return false;
		if (identifier == null)
		{
			if (other.identifier != null)
				return false;
		}
		else if (!identifier.equals(other.identifier))
			return false;
		if (index == null)
		{
			if (other.index != null)
				return false;
		}
		else if (!index.equals(other.index))
			return false;
		return true;
	}

	private FilterType(String identifier, Class<C> type, DynamicFilterValues<C> dynamicValues, Optional<FilterTypeUnit> unit, String queryBaseString)
    {
        this(identifier, type);
        this.dynamicValues = dynamicValues;
        this.range = dynamicValues.getCurrentValues();
        unit.ifPresent(filterUnit -> this.unit = filterUnit);
        this.queryBaseString = queryBaseString;
    }

    private FilterType(String identifier, Class<C> type, ArrayList<C> range, Optional<FilterTypeUnit> unit, String queryBaseString)
    {
        this(identifier, type);
        this.dynamicValues = new DynamicFilterValues<C>()
		{
        	@Override
        	public ArrayList<C> getCurrentValues()
        	{
        		return FilterType.this.range;
        	}
		};
        this.range = range;
        unit.ifPresent(filterUnit -> this.unit = filterUnit);
        this.queryBaseString = queryBaseString;
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
		return dynamicValues.getCurrentValues();
	}

	public C getRangeMin()
	{
		return this.range.get(0);
	}

	public void setRangeMin(C minValue)
	{
		this.range.set(0, minValue);
	}

	public C getRangeMax()
	{
		return this.range.get(1);
	}

	public void setRangeMax(C maxValue)
	{
		this.range.set(1, maxValue);
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
			ArrayList<C> range = dynamicValues.getCurrentValues();
			Object minValue = range.get(0);
			Object maxValue = range.get(1);
			if (type == LocalDateTime.class)
			{
				LocalDateTime startTime = (LocalDateTime)range.get(0);
				System.out.println("FilterType: getSQLArguments: start time is " + startTime.toString());
				LocalDateTime stopTime = (LocalDateTime)range.get(1);
				minValue = TimeUtil.str2et(startTime.toString());
				maxValue = TimeUtil.str2et(stopTime.toString());
			}


			args.put("min" + queryBaseString, String.valueOf(minValue));
			args.put("max" + queryBaseString, String.valueOf(maxValue));
		}
		return args;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public String getQueryBaseString()
	{
		return queryBaseString;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		FilterType<C> filter = new FilterType<C>(identifier, type, dynamicValues, Optional.ofNullable(unit), queryBaseString);
//		if (unit != null) filter = new FilterType<C>(identifier, type, dynamicValues, Optional.ofNullable(unit), queryBaseString);
//		else filter = new FilterType<C>(identifier, type, dynamicValues, queryBaseString);
	    filter.selectedRangeValue = selectedRangeValue;
	    filter.enabled = enabled;
	    filter.range = new ArrayList();
	    filter.range.addAll(range);
		return filter;
	}
}
