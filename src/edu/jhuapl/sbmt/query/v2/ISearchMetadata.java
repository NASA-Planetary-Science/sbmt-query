package edu.jhuapl.sbmt.query.v2;

import crucible.crust.metadata.impl.FixedMetadata;

public interface ISearchMetadata
{
    public FixedMetadata getMetadata();
    
    public String getSearchString();
    
    public void setSearchString(String searchString);
}
