package edu.jhuapl.sbmt.query.filter.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;

public class FilterTypeUnit
{
	private static final Map<String, FilterTypeUnit> FILTER_TYPE_UNIT_IDENTIFIERS = new HashMap<>();

	/**
     * Provide a FilterTypeUnit object for the given identifier parameter. If a
     * FilterTypeUnit object already exists for this identity, it is simply
     * returned, otherwise it is created first.
     *
     * @param identifier of the FilterTypeUnit object
     * @return the FilterTypeUnit object
     */
    public static FilterTypeUnit provide(String identifier)
    {
        Preconditions.checkNotNull(identifier);

        FilterTypeUnit result = FILTER_TYPE_UNIT_IDENTIFIERS.get(identifier);
        if (result == null)
        {
            result = new FilterTypeUnit(identifier);
            FILTER_TYPE_UNIT_IDENTIFIERS.put(identifier, result);
        }

        return result;
    }

    /**
     * Create and return a new FilterTypeUnit object identified with the provided
     * identifier string.
     *
     * @param identifier of the FilterTypeUnit object
     * @return the FilterTypeUnit object
     * @throws IllegalArgumentException if there is already a FilterTypeUnit object
     *             associated with the identifier
     */
    private static <C> FilterTypeUnit create(String identifier, Class<C> FilterTypeUnit, Pair<C, C> range)
    {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(!FILTER_TYPE_UNIT_IDENTIFIERS.containsKey(identifier), "Already have a Filter Type object for identifier " + identifier);

        FilterTypeUnit result = new FilterTypeUnit(identifier);
        FILTER_TYPE_UNIT_IDENTIFIERS.put(identifier, result);

        return result;
    }

    /**
     * Create and return a new FilterTypeUnit object identified with the provided
     * identifier string.
     *
     * @param identifier of the FilterTypeUnit object
     * @return the FilterTypeUnit object
     * @throws IllegalArgumentException if there is already a FilterTypeUnit object
     *             associated with the identifier
     */
    private static <C> FilterTypeUnit create(String identifier)
    {
        Preconditions.checkNotNull(identifier);
        Preconditions.checkArgument(!FILTER_TYPE_UNIT_IDENTIFIERS.containsKey(identifier), "Already have a Filter Type object for identifier " + identifier);

        FilterTypeUnit result = new FilterTypeUnit(identifier);
        FILTER_TYPE_UNIT_IDENTIFIERS.put(identifier, result);

        return result;
    }

    public static boolean contains(String identifier)
    {
        Preconditions.checkNotNull(identifier);
        return FILTER_TYPE_UNIT_IDENTIFIERS.containsKey(identifier);
    }

    public static final FilterTypeUnit DEGREES = create("deg");
    public static final FilterTypeUnit KM = create("km");
    public static final FilterTypeUnit METERS_PER_PIXEL = create("mpp");

    private final String identifier;

    private FilterTypeUnit(String identifier)
    {
        this.identifier = identifier;
    }

    public String name()
    {
        return identifier;
    }

    @Override
    public String toString()
    {
        return identifier;
    }
}
