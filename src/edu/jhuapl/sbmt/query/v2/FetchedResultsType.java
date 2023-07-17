/**
 * 
 */
package edu.jhuapl.sbmt.query.v2;

/**
 * Enum to describe where fetched results originate from
 * @author steelrj1
 *
 */
public enum FetchedResultsType
{
	/**
	 * Denotes this set of results was a result of querying the remote database
	 */
	DATABASE("Database"),
	
	/**
	 * Denotes this set of results was a result of retrieving a fixed list from the server
	 */
	FIXEDLIST("Fixed List"),
	
	/**
	 * Denotes this set of results was a result of querying the cache for information
	 */
	CACHE("Local Cache");
	
	private String name;
	
	private FetchedResultsType(String name)
	{
		this.name = name;
	}

	protected String getName()
	{
		return name;
	}
}
