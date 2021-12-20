package edu.jhuapl.sbmt.query.filter.model;

import java.util.ArrayList;

@FunctionalInterface
public interface DynamicFilterValues<C> {
	ArrayList<C> getCurrentValues();
}