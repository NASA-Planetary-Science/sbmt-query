package edu.jhuapl.sbmt.query.database;

import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;

import com.google.common.collect.Range;

import crucible.crust.metadata.api.Key;
import crucible.crust.metadata.impl.FixedMetadata;
import crucible.crust.metadata.impl.SettableMetadata;

public class SpectraDatabaseSearchMetadata extends DatabaseSearchMetadata
{
    public static final Key<TreeSet<Integer>> CUBE_LIST = Key.of("Cube List");
    public static final Key<String> MODEL_NAME = Key.of("Model Name");
    public static final Key<String> DATA_TYPE = Key.of("Data Type");

    protected SpectraDatabaseSearchMetadata(FixedMetadata metadata)
    {
        super(metadata);
        // TODO Auto-generated constructor stub
    }

    public static SpectraDatabaseSearchMetadata of(String name, DateTime startDate, DateTime stopDate,
            Range<Double> distanceRange, String searchString,
            List<Integer> polygonTypes, Range<Double> incidenceRange,
            Range<Double> emissionRange, Range<Double> phaseRange, TreeSet<Integer> cubeList, String modelName, String dataType)
    {
        FixedMetadata metadata = FixedMetadata.of(createSettableMetadata(name, startDate, stopDate, distanceRange, searchString,
                                                polygonTypes, incidenceRange, emissionRange, phaseRange, cubeList, modelName, dataType));
        return new SpectraDatabaseSearchMetadata(metadata);
    }

    protected static SettableMetadata createSettableMetadata(String name, DateTime startDate, DateTime stopDate,
            Range<Double> distanceRange, String searchString,
            List<Integer> polygonTypes, Range<Double> incidenceRange,
            Range<Double> emissionRange, Range<Double> phaseRange, TreeSet<Integer> cubeList, String modelName, String dataType)
    {
        SettableMetadata metadata = createSettableMetadata(name, startDate, stopDate, distanceRange, searchString, polygonTypes, incidenceRange, emissionRange, phaseRange);
        metadata.put(CUBE_LIST, cubeList);
        metadata.put(MODEL_NAME, modelName);
        metadata.put(DATA_TYPE, dataType);
        return metadata;
    }

}
