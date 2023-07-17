package edu.jhuapl.sbmt.query.v2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import crucible.crust.metadata.api.Key;
import crucible.crust.metadata.api.Version;
import crucible.crust.metadata.impl.InstanceGetter;
import crucible.crust.metadata.impl.SettableMetadata;
import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.sbmt.query.v2.QueryException.QueryExceptionReason;
import edu.jhuapl.sbmt.query.v2.QueryException.Severity;

/**
 * Class that provides base functionality to other classes that wish to perform database queries
 * 
 * 
 * For more details, including origin history of some of this code, please see the package README.  
 * 
 * @author steelrj1
 *
 *
 */
public class DatabaseDataQuery implements IDataQuery
{
	/**
	 * FixedListDataQuery object that acts as a fallover mechanism in case an exception is thrown here
	 */
	private FixedListDataQuery fixedListQuery;
	
	/**
	 * Object that contains data source information such as the root path, data path, and so on. 
	 */
	protected DataQuerySourcesMetadata dataSourceMetadata;
	
	protected String rootPath;
	protected String galleryPath;
	protected String dataPath;
	
	/**
	 * Constructor
	 * @param searchMetadata	metadata object describing the data source information
	 */
	public DatabaseDataQuery(DataQuerySourcesMetadata searchMetadata)
	{
		this.dataSourceMetadata = searchMetadata;
		fixedListQuery = new FixedListDataQuery(searchMetadata);
	}
	
	/**
	 * Helper method that establishes a URL connection to the given URL, sends the list of arguments
	 * (in "GET" format) and establishes a BufferedReader via an InputStreamReader to get the results
	 * of the call from the server
	 * 
	 * Example Usage:
	 * 		String args = key1=val1&key2=val2;
	 *  	URL url = new URL("http://sbmt.jhuapl.edu");
	 * 		BufferedReader reader = DatabaseDataQuery.getBufferedReaderForConnectionAtURL(url, args);
	 * 
	 * @param u					The URL to connect to
	 * @param args				The arguments to the URL call in GET format (key1=val1&key2=val2&....)
	 * @return					The BufferedReader that reads the response from the server
	 * @throws IOException		Exception thrown if there are problems connecting to the server
	 */
	private static BufferedReader getBufferedReaderForConnectionAtURL(URL u, String args) throws IOException
	{
		URLConnection conn = u.openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0");
//        conn.setReadTimeout(10000);

        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(args);
        wr.flush();

        InputStreamReader isr = new InputStreamReader(conn.getInputStream());
        BufferedReader in = new BufferedReader(isr);
        return in;
	}
	
    /**
     * Helper method that checks for the existence of a database table on the server
     * 
     * Example Usage:
     * 		boolean tableExists = DatabaseDataQuery.checkForDatabaseTable("67pcubes_gaskell");
     * 
     * @param tableName
     * @return
     * @throws IOException
     */
    public static boolean checkForDatabaseTable(String tableName) throws QueryException
    {
    	String line = "";
    	try 
    	{
	        URL u = new URL(Configuration.getQueryRootURL() + "/" + "tableexists.php");
	        BufferedReader in = getBufferedReaderForConnectionAtURL(u, "tableName=" + tableName);
	
	        line = in.readLine();
	        in.close();
	
	        if (line == null)
	        {
	            throw new IOException("No database available");
	        }
	        else if (!line.equalsIgnoreCase("true") && !line.equalsIgnoreCase("false"))
	        {
	            throw new IOException(line);
	        }
    	}
    	catch (IOException ioe)
    	{
    		throw new QueryException("Error: Problem determining if database table"
    				+ "exists", Severity.ERROR, QueryExceptionReason.DB_TABLE_NOT_FOUND, ioe);
    	}
        return line.equalsIgnoreCase("true");
    }

    /**
     * Sends the database query to the remote server.  In this case, a call is sent to a <pre>phpScript</pre>
     * on the remote server with the given <pre>args</pre>.  Results are encapsulated in a FetchedResults
     * object to send back to the caller.
     * 
     * By the time this method is called, arguments should already be checked for validity (e.g. table exists,
     * values within acceptable ranges)
     * 
     * Example Usage:
     * 		HashMap<String, String> argMap = new HashMap<String, String>();
	 *		argMap.put("Key1", "Value1");
	 *		argMap.put("Key2", "Value2");
     * 		String args = constructUrlArguments(argMap);
     * 		FetchedResults results = doQuery("searchimages.php", args);
     * 
     * @param phpScript			The php script to call on the server to perform the query
     * @param args				The args string in GET format (key1=val1&key2=val2&....)
     * @return					FetchedResults object that holds the list of results
     * @throws QueryException	Exception thrown when there is a problem performing the query
     * @throws IOException		Exception thrown when there is a problem with the server connection
     */
    protected FetchedResults doQuery(String phpScript, String args) throws QueryException//, IOException
    {
        List<List<String>> results = new ArrayList<>();

        if (!fixedListQuery.getCachedDataQuery().checkAuthorizedAccess())
        {
        	//TODO should this fall back to the fixed list here? and then possibly the cache?
//            return new FetchedResults("", FetchedResultsType.DATABASE, results);
            throw new QueryException("Error: You Are Not Authorized to Access This Data.",
        			 Severity.ERROR, QueryExceptionReason.FIXED_LIST_NOT_AUTHORIZED);
        }
        System.out.println("DatabaseDataQuery: doQuery: phpScript is " + phpScript);
        try 
        {
	        URL u = new URL(Configuration.getQueryRootURL() + "/" + phpScript);
	        System.out.println("DatabaseDataQuery: doQuery: url is " + u);
	        BufferedReader in = getBufferedReaderForConnectionAtURL(u, args);
	        System.out.println("DatabaseDataQuery: doQuery: args " + args);
	        String line;
	
	        while ((line = in.readLine()) != null)
	        {
	            line = line.trim();
	            if (line.length() == 0)
	                continue;
	
	            String[] tokens = line.split("\\s+");
	            List<String> words = new ArrayList<>();
	            for (String word : tokens)
	                words.add(word);
	            results.add(words);
	        }
	
	        in.close();
	        for (List<String> res : results)
	        {
	        	changeDataPathToFullPath(res, 0);
	        }
	
	        fixedListQuery.getCachedDataQuery().updateDataInventory(results);
        }
        catch (IOException ioe)
        {
        	throw new QueryException("Error: Could not retrieve results from "
        			+ "database", Severity.ERROR, QueryExceptionReason.DB_CONNECTION, ioe);
        }
        System.out.println("DatabaseDataQuery: doQuery: results size " + results.size());
        return new FetchedResults("", FetchedResultsType.DATABASE, results);
    }
	
    /**
     * Builds a HTTP GET style argument string based on the passed in set of key-value pairs in the
     * <pre>args</pre> argument.  
     * 
     * Example Usage:
     * 		HashMap<String, String> argMap = new HashMap<String, String>();
	 *		argMap.put("Key1", "Value1");
	 *		argMap.put("Key2", "Value2");
	 *		argMap.put("Key3", "Value3");
     * 		String args = constructUrlArguments(argMap);
     * 		//result is "Key1=Value1&Key2=Value2&Key3=Value3"
     * 
     * @param args
     * @return
     */
    protected String constructUrlArguments(HashMap<String, String> args)
    {
        String str = "";

        boolean firstKey = true;
        List<String> keysSorted = args.keySet().stream().collect(Collectors.toList());
        Collections.sort(keysSorted, (o1, o2) -> o1.compareTo(o2));
        for (String key : keysSorted)
        {
            if (firstKey == true)
                firstKey = false;
            else
                str += "&";

            str += key + "=" + args.get(key);
        }

        return str;
    }
    
    /**
     * Convert the index-th element of the result (the path to the data) with the full path, 
     * but only if the result does not already have a full path.
     * 
     * Example Usage:
     * 		
     * 
     * @param result
     * @param index
     */
    protected void changeDataPathToFullPath(List<String> result, int index)
    {
        String fullPath = result.get(index);
        if (!fullPath.contains("/"))
        {
            result.set(index, getDataPath() + "/" + fullPath);
        }
    }

	/**
	 * Returns the fixed list query for this database query
	 * 
	 * @return
	 */
	public FixedListDataQuery getFixedListQuery()
	{
		return fixedListQuery;
	}
	
	@Override
	public IDataQuery clone() throws CloneNotSupportedException
	{
		// TODO Auto-generated method stub
		return (IDataQuery)super.clone();
	}
	
	@Override
	public String getDataPath()
	{
		return fixedListQuery.getDataPath();
	}

	public FetchedResults fallbackQuery(ISearchMetadata queryMetadata) throws QueryException 
	{
		System.out.println("DatabaseDataQuery: fallbackQuery: falling back to fixed list");
		return fixedListQuery.runQuery(queryMetadata);
	}
	
	@Override
	public String getRootPath()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public DataQuerySourcesMetadata getDataSourceMetadata()
	{
		return dataSourceMetadata;
	}

	private static final Key<DatabaseDataQuery> DATABASEDATAQUERY_KEY = Key.of("databaseDataQuery");
    private static final Key<DataQuerySourcesMetadata> METADATA_KEY = Key.of("dataSourceMetadata");
	
	/**
	 * Registers this class with the metadata system
	 */
	public static void initializeSerializationProxy()
	{
    	InstanceGetter.defaultInstanceGetter().register(DATABASEDATAQUERY_KEY, (metadata) -> {

    		DataQuerySourcesMetadata metadataBundle = metadata.get(METADATA_KEY);
	        DatabaseDataQuery query = new DatabaseDataQuery(metadataBundle);
    		return query;

    	}, DatabaseDataQuery.class, query -> {

    		SettableMetadata result = SettableMetadata.of(Version.of(1, 0));
    		result.put(METADATA_KEY, query.getDataSourceMetadata());
    		return result;
    	});

	}

	@Override
	public FetchedResults runQuery(ISearchMetadata queryMetadata) throws QueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGalleryPath()
	{
		// TODO Auto-generated method stub
		return null;
	}
}

