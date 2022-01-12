package edu.jhuapl.sbmt.query.filter.model;

import java.util.Iterator;
import java.util.List;

import com.github.davidmoten.guavamini.Lists;

public class RangeFilterModel extends FilterModel
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
//			Iterator<String> iterator = filter.getSQLArguments().keySet().iterator();
			queryString += filter.getQueryBaseString() + " BETWEEN ";
			queryString += filter.getSQLArguments().get("min" + filter.getQueryBaseString()) + " AND ";
			queryString += filter.getSQLArguments().get("max" + filter.getQueryBaseString()) ;
//			while (iterator.hasNext())
//			{
//				String key = iterator.next();
//				queryString += filter.getSQLArguments().get(key);
//				if (iterator.hasNext()) queryString += " AND ";
//			}
//			if (filterIterator.hasNext()) queryString += ",";
			queryElements.add(queryString);
		}
//		System.out.println("RangeFilterModel: getSQLQueryString: query string is " + queryString);
//		return queryString;
		return queryElements;
	}

//	public HashMap<String, String> getSQLQueryElements()
//	{
//		HashMap<String, String> elements = new HashMap<String, String>();
//		Iterator filterIterator = getAllItems().iterator();
//		while (filterIterator.hasNext())
//		{
//			FilterType filter = (FilterType)filterIterator.next();
//			elements.put(filter.getQueryBaseString(), getSQLQueryString());
//		}
//
//		return elements;
//	}
}
