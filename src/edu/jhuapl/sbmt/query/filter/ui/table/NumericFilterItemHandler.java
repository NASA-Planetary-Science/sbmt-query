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
		switch (aEnum)
		{
			case FILTER_ENABLED:
				return type.isEnabled();
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
		if (aEnum == FilterColumnLookup.FILTER_ENABLED)
		{
			type.setEnabled((Boolean)aValue);
		}
		else if (aEnum == FilterColumnLookup.FILTER_LOW)
		{
			if (type.getType() == Double.class)
				type.getRange().set(0, Double.parseDouble(""+aValue));
			else
				type.getRange().set(0, aValue);
		}
		else if (aEnum == FilterColumnLookup.FILTER_HIGH)
		{
			if (type.getType() == Double.class)
				type.getRange().set(1, Double.parseDouble(""+aValue));
			else
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