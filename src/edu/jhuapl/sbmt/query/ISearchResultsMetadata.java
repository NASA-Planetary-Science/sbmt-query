package edu.jhuapl.sbmt.query;

import java.util.List;

import crucible.crust.metadata.impl.FixedMetadata;

public interface ISearchResultsMetadata<T>
{

	FixedMetadata getMetadata();

	List<T> getResultlist();

}