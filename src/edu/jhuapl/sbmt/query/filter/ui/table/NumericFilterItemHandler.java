package edu.jhuapl.sbmt.query.filter.ui.table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
			case FILTER_START_DATE:
				return ((LocalDateTime)type.getRangeMin()).toLocalDate();
			case FILTER_START_TIME:
				return ((LocalDateTime)type.getRangeMin()).toLocalTime();
			case FILTER_END_DATE:
				return ((LocalDateTime)type.getRangeMax()).toLocalDate();
			case FILTER_END_TIME:
				return ((LocalDateTime)type.getRangeMax()).toLocalTime();

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
		else if (aEnum == FilterColumnLookup.FILTER_START_DATE)
		{
			LocalDateTime startDate = (LocalDateTime)type.getRangeMin();
			var newDate = (LocalDate)aValue;
			startDate = startDate.with(newDate);
			type.setRangeMin(startDate);
		}
		else if (aEnum == FilterColumnLookup.FILTER_START_TIME)
		{
			LocalDateTime startDate = (LocalDateTime)type.getRangeMin();
			var newTime = (LocalTime)aValue;
			startDate = startDate.with(newTime);
			type.setRangeMin(startDate);
		}
		else if (aEnum == FilterColumnLookup.FILTER_END_DATE)
		{
			LocalDateTime endDate = (LocalDateTime)type.getRangeMax();
			var newDate = (LocalDate)aValue;
			endDate = endDate.with(newDate);
			type.setRangeMax(endDate);
		}
		else if (aEnum == FilterColumnLookup.FILTER_END_TIME)
		{
			LocalDateTime endDate = (LocalDateTime)type.getRangeMax();
			var newDate = (LocalTime)aValue;
			endDate = endDate.with(newDate);
			type.setRangeMax(endDate);
		}
		else
			throw new UnsupportedOperationException("Column is not supported. Enum: " + aEnum);
	}
}