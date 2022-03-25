package edu.jhuapl.sbmt.query.filter.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import com.beust.jcommander.internal.Lists;
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
    public static <C> FilterType<C> provideDynamic(String identifier, Class<C> filterType, DynamicFilterValues<C> dynamicValues, String queryBaseString, boolean useByDefault)
    {
        Preconditions.checkNotNull(identifier);

        FilterType<C> result = FILTER_TYPE_IDENTIFIERS.get(identifier);
        if (result == null)
        {
            result = new FilterType<C>(identifier, filterType, dynamicValues, Optional.ofNullable(null), queryBaseString, useByDefault);
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
    	List vals = Lists.newArrayList();
    	vals.addAll(FILTER_TYPE_IDENTIFIERS.values());
    	Collections.sort(vals, new Comparator<FilterType>()
		{

			@Override
			public int compare(FilterType arg0, FilterType arg1)
			{
				return arg0.toString().compareTo(arg1.toString());
			}
		});
    	return vals;
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
    public static <C> FilterType<C> create(String identifier, Optional<FilterTypeUnit> unit, Class<C> filterType, Pair<C, C> range, String queryBaseString, boolean useByDefault)
    {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(!FILTER_TYPE_IDENTIFIERS.containsKey(identifier), "Already have a Filter Type object for identifier " + identifier);

        ArrayList<C> rangeArray = new ArrayList<C>();
        rangeArray.add(range.getLeft());
        rangeArray.add(range.getRight());
        FilterType<C> result = new FilterType<C>(identifier, filterType, rangeArray, unit, queryBaseString, useByDefault);
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
    public static <C> FilterType<C> create(String identifier, Class<C> filterType, C[] values, String queryBaseString, boolean useByDefault)
    {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(!FILTER_TYPE_IDENTIFIERS.containsKey(identifier), "Already have a Filter Type object for identifier " + identifier);

        ArrayList<C> rangeArray = new ArrayList<C>();
        for (C val : values) rangeArray.add(val);
        FilterType<C> result = new FilterType<C>(identifier, filterType, rangeArray, Optional.ofNullable(null), queryBaseString, useByDefault);
        result.unit = FilterTypeUnit.NONE;
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
        Preconditions.checkArgument(FILTER_TYPE_IDENTIFIERS.containsKey(identifier), "No such identifier " + identifier);
//        return (FilterType)(FILTER_TYPE_IDENTIFIERS.get(identifier).clone());
        return new FilterType(FILTER_TYPE_IDENTIFIERS.get(identifier));
    }

    private final String identifier;
    private Class<C> type;
    private ArrayList<C> range;
    DynamicFilterValues<C> dynamicValues;
    private FilterTypeUnit unit;
    private C selectedRangeValue;
    private String queryBaseString;
    private boolean enabled = false;
    private UUID index;
    private boolean useByDefault;

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

	private FilterType(String identifier, Class<C> type, DynamicFilterValues<C> dynamicValues, Optional<FilterTypeUnit> unit, String queryBaseString, boolean useByDefault)
    {
        this(identifier, type);
        this.dynamicValues = dynamicValues;
        this.range = dynamicValues.getCurrentValues();
        unit.ifPresent(filterUnit -> this.unit = filterUnit);
        this.queryBaseString = queryBaseString;
        this.useByDefault = useByDefault;
    }

    private FilterType(String identifier, Class<C> type, ArrayList<C> range, Optional<FilterTypeUnit> unit, String queryBaseString, boolean useByDefault)
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
        this.useByDefault = useByDefault;
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
		if (isUseByDefault() == false) return args;

		if (selectedRangeValue != null)
		{
			args.put(queryBaseString, String.valueOf(selectedRangeValue));
		}
		else
		{
			ArrayList<C> range = dynamicValues.getCurrentValues();
			C minValue = (C)range.get(0);
			C maxValue = (C)range.get(1);
			if (type == LocalDateTime.class)
			{
				LocalDateTime startTime = (LocalDateTime)range.get(0);
				LocalDateTime stopTime = (LocalDateTime)range.get(1);
				args.put("min" + queryBaseString, ""+TimeUtil.str2et(startTime.toString()));
				args.put("max" + queryBaseString, ""+TimeUtil.str2et(stopTime.toString()));
			}
			else
			{
				args.put("min" + queryBaseString, String.valueOf(convertIfNeeded(minValue)));
				args.put("max" + queryBaseString, String.valueOf(convertIfNeeded(maxValue)));
			}
		}
		return args;
	}

	private C convertIfNeeded(C value)
	{
		if (unit == FilterTypeUnit.DEGREES) {
			Double inRadians = Math.toRadians((Double)value);
			return (C)inRadians;
		}
		else return value;
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

	public boolean isUseByDefault()
	{
		return useByDefault;
	}

	public void setUseByDefault(boolean useByDefault)
	{
		this.useByDefault = useByDefault;
	}

	public FilterType(FilterType<C> type)
	{
		this.index = UUID.randomUUID();
		this.identifier = type.identifier;
		this.type = type.type;
//		this.dynamicValues = type.dynamicValues;
        this.range = new ArrayList<C>();
        if (!type.getRange().isEmpty())
        {
        	for (int i=0; i<type.getRange().size(); i++)
        		this.range.add(type.getRange().get(i));
        }
//        		type.dynamicValues.getCurrentValues();
        this.dynamicValues = new DynamicFilterValues<C>()
		{
        	@Override
        	public ArrayList<C> getCurrentValues()
        	{
        		return FilterType.this.range;
        	}
		};
        this.unit = type.unit;
        this.queryBaseString = type.queryBaseString;
        this.selectedRangeValue = (C)type.getSelectedRangeValue();
        this.enabled = type.enabled;
        this.useByDefault = type.useByDefault;
	}
}
