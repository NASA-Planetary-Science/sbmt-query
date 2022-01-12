package edu.jhuapl.sbmt.query.filter.model;

import java.util.Iterator;
import java.util.List;

import com.github.davidmoten.guavamini.Lists;

import glum.item.BaseItemManager;

public class FilterModel extends BaseItemManager<FilterType>
{
	public void addFilter(FilterType filterType)
	{
		List<FilterType> allItems = Lists.newArrayList();
		allItems.addAll(getAllItems());
		allItems.add(filterType);
		setAllItems(allItems);
	}

	public List<String> getSQLQueryString()
	{
		List<String> queryElements = Lists.newArrayList();
		String queryString = "";
		Iterator filterIterator = getAllItems().iterator();
		while (filterIterator.hasNext())
		{
			queryString = "";
			FilterType filter = (FilterType)filterIterator.next();
			if (!filter.isEnabled()) continue;
			Iterator<String> iterator = filter.getSQLArguments().keySet().iterator();
			while (iterator.hasNext())
			{
				String key = iterator.next();
				queryString += key + "=" + filter.getSQLArguments().get(key);
				if (iterator.hasNext()) queryString += " AND ";
			}
			queryElements.add(queryString);
//			if (filterIterator.hasNext()) queryString += " AND ";
		}
//		return queryString;

		return queryElements;
	}
}
