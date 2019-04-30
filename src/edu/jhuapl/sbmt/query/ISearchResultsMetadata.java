package edu.jhuapl.sbmt.query;

import java.util.List;

import crucible.crust.metadata.impl.FixedMetadata;

public interface ISearchResultsMetadata
{

	FixedMetadata getMetadata();

	List<List<String>> getResultlist();

}