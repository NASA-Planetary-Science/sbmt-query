package edu.jhuapl.sbmt.query.v2;

public interface IDataQuery
{
	/**
	 * Run a query and return a FetchedResults object holding the results. 
	 */
	FetchedResults runQuery(ISearchMetadata queryMetadata) throws QueryException;
	
	/**
	 * Clone method 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public IDataQuery clone() throws CloneNotSupportedException;
	
	/**
	 * Returns the data path for this query type
	 * @return
	 */
	public String getDataPath();
	
	public FetchedResults fallbackQuery(ISearchMetadata queryMetadata) throws QueryException;
	
    String getRootPath();
    
    String getGalleryPath();
	
}
