package edu.jhuapl.sbmt.query;

import java.util.List;

import edu.jhuapl.saavtk.metadata.FixedMetadata;

public interface ISearchResultsMetadata
{

	FixedMetadata getMetadata();

	List<List<String>> getResultlist();

}