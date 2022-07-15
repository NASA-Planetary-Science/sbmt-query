package edu.jhuapl.sbmt.query.filter.model;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import com.beust.jcommander.internal.Lists;

public class TimeWindowFilterModel extends FilterModel<LocalDateTime>
{
	@Override
	public void addFilter(FilterType<LocalDateTime> filterType)
	{
		List<FilterType<LocalDateTime>> allItems = Lists.newArrayList();
		allItems.addAll(getAllItems());
		allItems.add(filterType);
		setAllItems(allItems);
	}

	@Override
	public List<String> getSQLQueryString()
	{
		List<String> queryElements = Lists.newArrayList();
		String queryString = "";
		Iterator<FilterType<LocalDateTime>> filterIterator = getAllItems().iterator();
		while (filterIterator.hasNext())
		{
			queryString = "";
			FilterType<LocalDateTime> filter = filterIterator.next();
			if (!filter.isEnabled()) continue;
			queryString += filter.getQueryBaseString() + " BETWEEN ";
			queryString += filter.getSQLArguments().get("min" + filter.getQueryBaseString()) + " AND ";
			queryString += filter.getSQLArguments().get("max" + filter.getQueryBaseString()) ;
			queryElements.add(queryString);
		}
		return queryElements;
	}
}
