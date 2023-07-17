/**
 * 
 */
package edu.jhuapl.sbmt.query.v2;

import java.util.List;

import com.google.common.collect.Lists;

import crucible.crust.metadata.api.Key;
import crucible.crust.metadata.api.Version;
import crucible.crust.metadata.impl.InstanceGetter;
import crucible.crust.metadata.impl.SettableMetadata;

/**
 * Container class the holds the results from a query, which can come from a remote database, a fixed list, or
 * the local file cache.  The FetchedResult object may contain a name to help identify the set of result, and it also
 * also comes with a FetchedResultType identifying where it came from (Cache, FixedList, Database).
 * 
 * Also, it has an initializeSerializationProxy method to allow the object to be read/written from/to metadata. 
 *  
 * @author steelrj1
 *
 */
public class FetchedResults //implements MetadataManager
{
    private static final Key<FetchedResults> FETCHEDRESULTS_KEY = Key.of("fetchedResults");
	private static final Key<String> fetchedResultsNameKey = Key.of("Results Name");
	private static final Key<String> fetchedResultTypeKey = Key.of("Fetched Result Type");
	private static final Key<List<List<String>>> fetchedDataKey = Key.of("Fetched Data");
	
	private String name;
	private FetchedResultsType type;
	private List<List<String>> fetchedData = Lists.newArrayList();
	
	public FetchedResults() { }
	
	/**
	 * Public constructor
	 * @param name			The name of the FetchedResults dataset, may be an empty string
	 * @param type			The type of data, defined by a FetchedResultsType enum
	 * @param fetchedData	The fetched data, as a List of List of Strings.  
	 */
	public FetchedResults(String name, FetchedResultsType type, List<List<String>> fetchedData) 
	{
		this.name = name;
		this.type = type;
		this.fetchedData = fetchedData;
	}
	
	/**
	 * Returns the name
	 * @return	the name of the results set
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the name of the results set
	 * @param resultSetName
	 */
	public void setName(String resultSetName)
	{
		this.name = resultSetName;
	}

	/**
	 * Returns the type of the result set
	 * @return	the FetchedResultsType for this results set
	 */
	public FetchedResultsType getType()
	{
		return type;
	}

	/**
	 * Returns the result set
	 * @return	the List of List of Strings that defines this results set
	 */
	public List<List<String>> getFetchedData()
	{
		return fetchedData;
	}
	
	public int size()
	{
		return fetchedData.size();
	}
	
	public static void initializeSerializationProxy()
	{
    	InstanceGetter.defaultInstanceGetter().register(FETCHEDRESULTS_KEY, (metadata) -> {
    		String name = metadata.get(fetchedResultsNameKey);
    		FetchedResultsType type = FetchedResultsType.valueOf(metadata.get(fetchedResultTypeKey));
    		List<List<String>> results = metadata.get(fetchedDataKey);
    		FetchedResults result = new FetchedResults(name, type, results);
    		return result;

    	}, FetchedResults.class, spec -> {

    		SettableMetadata result = SettableMetadata.of(Version.of(1, 0));
    		result.put(fetchedResultsNameKey, spec.getName());
    		result.put(fetchedResultTypeKey, spec.getType().toString());
    		result.put(fetchedDataKey, spec.getFetchedData());

    		return result;
    	});

	}
}
