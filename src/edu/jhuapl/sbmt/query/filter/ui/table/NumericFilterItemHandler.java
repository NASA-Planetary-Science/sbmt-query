package edu.jhuapl.sbmt.query.filter.ui.table;

import edu.jhuapl.sbmt.query.filter.model.FilterModel;
import edu.jhuapl.sbmt.query.filter.model.FilterType;

import glum.gui.panel.itemList.BasicItemHandler;
import glum.gui.panel.itemList.query.QueryComposer;

public class NumericFilterItemHandler extends BasicItemHandler<FilterType, FilterColumnLookup>
{
	private final FilterModel filterModel;

	public NumericFilterItemHandler(FilterModel aManager, QueryComposer<FilterColumnLookup> aComposer)
	{
		super(aComposer);

		filterModel = aManager;
	}

	@Override
	public Object getColumnValue(FilterType type, FilterColumnLookup aEnum)
	{
		//TODO: Switch to using an index so the get all items doesn't take so long to look up
		switch (aEnum)
		{
			case FILTER_NAME:
				return type.name();
			case FILTER_TYPE:
				return type.getType().getSimpleName();
			case FILTER_LOW:
				return type.getRange().get(0);
			case FILTER_HIGH:
				return type.getRange().get(type.getRange().size()-1);
			case FILTER_RANGE:
				return type.getSelectedRangeValue();
			case FILTER_UNITS:
				return type.getUnit().name();
			default:
				break;
		}

		throw new UnsupportedOperationException("Column is not supported. Enum: " + aEnum);
	}

	@Override
	public void setColumnValue(FilterType type, FilterColumnLookup aEnum, Object aValue)
	{
		if (aEnum == FilterColumnLookup.FILTER_LOW)
		{
			type.getRange().set(0, aValue);
		}
		else if (aEnum == FilterColumnLookup.FILTER_HIGH)
		{
			type.getRange().set(1, aValue);
		}
		else if (aEnum == FilterColumnLookup.FILTER_RANGE)
		{
			type.setSelectedRangeValue(aValue);
		}
		else
			throw new UnsupportedOperationException("Column is not supported. Enum: " + aEnum);
	}
}