package edu.jhuapl.sbmt.query;

import java.util.List;

import edu.jhuapl.ses.jsqrl.impl.FixedMetadata;

public interface ISearchResultsMetadata<T>
{

	FixedMetadata getMetadata();

	List<T> getResultlist();

}