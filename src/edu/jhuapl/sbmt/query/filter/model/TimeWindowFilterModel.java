package edu.jhuapl.sbmt.query.filter.model;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

import com.github.davidmoten.guavamini.Lists;

public class TimeWindowFilterModel extends FilterModel
{
	@Override
	public void addFilter(FilterType filterType)
	{
		List<FilterType> allItems = Lists.newArrayList();
		allItems.addAll(getAllItems());
		allItems.add(filterType);
		setAllItems(allItems);
	}

	@Override
	public String getSQLQueryString()
	{
		String queryString = "";
		Iterator filterIterator = getAllItems().iterator();
		while (filterIterator.hasNext())
		{
			FilterType<LocalDateTime> filter = (FilterType)filterIterator.next();
			Iterator<String> iterator = filter.getSQLArguments().keySet().iterator();
			queryString += filter.getQueryBaseString() + " BETWEEN ";
			while (iterator.hasNext())
			{
				String key = iterator.next();
				queryString += filter.getSQLArguments().get(key);
				if (iterator.hasNext()) queryString += " AND ";
			}
			if (filterIterator.hasNext()) queryString += " AND ";
		}
		System.out.println("FilterModel: getSQLQueryString: query string is " + queryString);
		return queryString;
	}
}
