package edu.jhuapl.sbmt.query.filter.ui.table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import edu.jhuapl.sbmt.query.filter.model.FilterModel;
import edu.jhuapl.sbmt.query.filter.model.FilterType;

import glum.gui.panel.itemList.BasicItemHandler;
import glum.gui.panel.itemList.query.QueryComposer;

public class TimeWindowFilterItemHandler extends BasicItemHandler<FilterType<LocalDateTime>, FilterColumnLookup>
{
	private final FilterModel<LocalDateTime> filterModel;

	public TimeWindowFilterItemHandler(FilterModel<LocalDateTime> aManager, QueryComposer<FilterColumnLookup> aComposer)
	{
		super(aComposer);

		filterModel = aManager;
	}

	@Override
	public Object getColumnValue(FilterType<LocalDateTime> type, FilterColumnLookup aEnum)
	{
		switch (aEnum)
		{
			case FILTER_ENABLED:
				return type.isEnabled();
			case FILTER_NAME:
				return type.name();
			case FILTER_TYPE:
				return type.getType().getSimpleName();
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
	public void setColumnValue(FilterType<LocalDateTime> type, FilterColumnLookup aEnum, Object aValue)
	{
		if (aEnum == FilterColumnLookup.FILTER_ENABLED)
		{
			type.setEnabled((Boolean)aValue);
		}
		else if (aEnum == FilterColumnLookup.FILTER_START_DATE)
		{
			LocalDateTime startDate = (LocalDateTime)type.getRangeMin();
			var newDate = (LocalDate)aValue;
			startDate = startDate.with(newDate);
			type.getRange().set(0, startDate);
			type.setRangeMin(startDate);
		}
		else if (aEnum == FilterColumnLookup.FILTER_START_TIME)
		{
			LocalDateTime startDate = (LocalDateTime)type.getRangeMin();
			var newTime = (LocalTime)aValue;
			startDate = startDate.with(newTime);
			type.getRange().set(0, startDate);
			type.setRangeMin(startDate);
		}
		else if (aEnum == FilterColumnLookup.FILTER_END_DATE)
		{
			LocalDateTime endDate = (LocalDateTime)type.getRangeMax();
			var newDate = (LocalDate)aValue;
			endDate = endDate.with(newDate);
			type.getRange().set(1, endDate);
			type.setRangeMax(endDate);
		}
		else if (aEnum == FilterColumnLookup.FILTER_END_TIME)
		{
			LocalDateTime endDate = (LocalDateTime)type.getRangeMax();
			var newDate = (LocalTime)aValue;
			endDate = endDate.with(newDate);
			type.getRange().set(1, endDate);
			type.setRangeMax(endDate);
		}
		else
			throw new UnsupportedOperationException("Column is not supported. Enum: " + aEnum);
	}
}