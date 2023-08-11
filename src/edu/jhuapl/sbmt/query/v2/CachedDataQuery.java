package edu.jhuapl.sbmt.query.v2;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Preconditions;

import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.SafeURLPaths;
import edu.jhuapl.saavtk.util.UnauthorizedAccessException;
import edu.jhuapl.sbmt.query.v2.QueryException.QueryExceptionReason;
import edu.jhuapl.sbmt.query.v2.QueryException.Severity;

/**
 * CachedDataQuery class. Handles the querying of the local file cache.  Other classes like 
 * FixedListDataQuery use this as a fallback mechanism in case an exception is thrown in those
 * classes when trying to make a query. 
 * 
 * For more details, including origin history of some of this code, please see the package README.  
 * 
 * @author steelrj1
 *
 */
public class CachedDataQuery
{
	/**
	 * The data path for this cache query lookup
	 */
	private String dataPath;
	
	/**
	 * The DataInventory object containing the list of all files ever discovered for this data path
	 */
	private DataInventory dataInventory;
	
	/**
	 * Constructor.  In addition to setting the data path, also initialized a data inventory object
	 * 
	 * Example usage: 
	 *    CachedDataQuery query = new CachedDataQuery("/GASKELL/CERES/FC/images");
	 *    
	 * Here, the argument is the path or URL segment defining the directory to search on relative to the
	 * cache directory as defined in Configuration.getCacheDir().  This is also the path relative to the
	 * remote server's main data path (e.g. http://sbmt.jhuapl.edu/sbmt/prod/data/GASKELL/CERES/FC/images/)
	 * 
	 * @param dataPath	The data path for this query (e.g. "/GASKELL/CERES/FC/images")
	 */
	public CachedDataQuery(String dataPath)
	{
		Preconditions.checkNotNull(dataPath);
		this.dataPath = dataPath;
		this.dataInventory = new DataInventory();
	}
	
	/**
     * Return the list of cached results from previous data searches stored in the
     * data inventory file. This particular implementation assumes the first element in 
     * each result is the name of a file, and checks this against a list of files that 
     * actually exist in the user's cache.
     * 
     * Example usage:
     * 		query.getCachedResults("*1474*");	//Looks for files with 1474 in the name
     * 		query.getCachedResults("");			//empty search string
     * 		query.getCachedResults(null);		//identical to the second case
     * 
     * @param searchString 		the additional string to search for in the file name
     * @return 					the FetchedResults objects containing the results 
     */
    public FetchedResults getCachedResults(String searchString) throws QueryException
    {
    	//grab a list of all the cached files
        final List<File> fileList = getCachedFiles();
        final Map<String, File> filesFound = new TreeMap<>();
        for (File file: fileList)
        {
            // Strip off the local cache part of the prefix.
            String path = file.getPath().substring(Configuration.getCacheDir().length());
            filesFound.put(path, file);
        }

        //Grab the data inventory file, and return only the objects that currently exist in the cache
        final List<List<String>> results = new ArrayList<>();
        SortedMap<String, List<String>> inventory = getDataInventory();
        for (Entry<String, List<String>> each: inventory.entrySet())
        {
            if (filesFound.containsKey(each.getKey()))
                results.add(each.getValue());
        }
        //If a search string is specified, search for that pattern in the filenames
        if (searchString != null && !searchString.isEmpty())
        {
            searchString = wildcardToPathRegex(searchString);
            List<List<String>> unfilteredResults = results;
            List<List<String>> filteredResults = new ArrayList<>();
            for (List<String> result : unfilteredResults)
            {
                String name = result.get(0);
                if (!name.matches(searchString)) continue;                
                filteredResults.add(result);
            }
            return new FetchedResults("Filtered Cached Results for search string '" + searchString + "'", FetchedResultsType.CACHE, filteredResults);
        }
        return new FetchedResults("Cached Results", FetchedResultsType.CACHE, results);
    }

    /**
     * Return a list of files that actually exist on disk in the user's cache.  Walks no more than 10 levels
     * down the file tree and returns a list of files starting at the pathToDataFolder argument in the cache.
     * 
     * Example Usage:
     * 		List<File> cachedFiles = query.getCachedFiles()
     * 
     * This is a private class, so use methods like getCachedResults to confirm correctness
     * 
     * @return the map
     */
    private List<File> getCachedFiles() throws QueryException
    {
        final List<File> filesFound = new ArrayList<>();
        
        try
        {
            final int maxDepth = 10;
            // Find actual files present.
            Path start = Paths.get(Configuration.getCacheDir(), dataPath);
            Files.walkFileTree(start, EnumSet.allOf(FileVisitOption.class), maxDepth, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs)
                {
                    FileVisitResult result = FileVisitResult.CONTINUE;
                    try
                    {
                        result = super.visitFile(path, attrs);
                        File file = path.toFile();
                        if (file.isFile())
                            filesFound.add(file);
                    }
                    catch (@SuppressWarnings("unused") IOException e)
                    {
                        // Ignore problems that occur while traversing the file tree.
                        // e.printStackTrace();
                    }
                    return result;
                }

            });
        }
        catch (IOException e)
        {
            // Report this for debugging purposes, but no need for a pop-up.
//            e.printStackTrace();
        	throw new QueryException("Problem getting cached files.", Severity.ERROR, 
        			QueryExceptionReason.CACHE_FILE_LIST_ERROR, e);
        }
        
        return filesFound;
    }
    
    /**
     * Return the full path name of the inventory file.
     * 
     * Example Usage:
     * 		String dataInventoryFile = getDataInventoryFileName();
     * 
     * This method is package private solely for the purpose of running JUnit tests.  
     * 
     * @return the inventory file name
     */
    String getDataInventoryFileName()
    {
        if (getDataPath() == null) return null;
        return SafeURLPaths.instance().getString(Configuration.getCacheDir(), "dataInventory.txt");
    }

    /**
     * Return the current content of the data inventory file. Note this inventory
     * should be a superset of the data files that are locally cached.
     * 
     * The Key of the map is the first entry of the List<String>, the value.  
     * 
     * See {@link edu.jhuapl.sbmt.query.v2.DataInventory}
     * 
     * Example Usage:
     * 		SortedMap<String, List<String>> dataInventory = getDataInventory()
     * 
     * Testing is handled in the DataInventory class
     * 
     * @return the data inventory
     */
    private SortedMap<String, List<String>> getDataInventory() throws QueryException
    {
    	return dataInventory.getDataInventory(getDataInventoryFileName());
    }
    
    /**
     * Add the supplied search results to the data inventory.
     * 
     * New results (dates) supersede previous results for the same file.
     * Following a call to this method, the image inventory file will thus contain a union of all the search
     * results ever made. Note that this inventory includes all files that were found in a search, whether
     * or not those files have every actually been displayed and cached.
     * 
     * Example Usage:
     * 		List<String> results = .....
     * 		query.updateDataInventory(results);
     *
     * See {@link edu.jhuapl.sbmt.query.v2.DataInventory}
     * 
     * This method is package private for use by the FixedListDataQuery class.  Testing is handled
     * in the DataInventory class
     * 
     * @param newResults the results to add
     */
    void updateDataInventory(List<List<String>> newResults) throws QueryException
    {
    	dataInventory.updateDataInventory(newResults, getDataInventoryFileName());
    }
    
    /**
     * Converts typical wildcard patterns used by users (?, *, etc) to the proper Java pattern 
     * matching syntax
     * 
     * Example Usage:
     * 		NOTE: The first / is escaped in the comment below so it will not cause the comment block
     * 			to end early.
     * 		String pathRegex = query.wildcardToPathRegex("*1474*"); //pathRegex is "^.*\/.*1474.*$"
     * 
     * @param wildcard 	the user entered wildcard pattern
     * @return			the wildcard pattern converted - with escapes where needed - to Java regex syntax
     */
    protected String wildcardToPathRegex(String wildcard)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("^.*/");
        for (char c : wildcard.toCharArray())
        {
            switch(c) {
            case '*':
                builder.append(".*");
                break;
            case '?':
                builder.append(".");
                break;
                // escape special regexp-characters
            case '(': case ')': case '[': case ']': case '$':
            case '^': case '.': case '{': case '}': case '|':
            case '\\':
                builder.append("\\");
                builder.append(c);
                break;
            default:
                builder.append(c);
                break;
            }
        }
        builder.append('$');
        return builder.toString();
    }
    
    /**
     * Checks to see if the data path is accessible
     * 
     * Example Usage:
     * 		boolean authorized = query.checkAuthorizedAccess();
     * 
     * @return	boolean describing whether the data path is accessible on server
     * @throws UnauthorizedAccessException
     */
    protected boolean checkAuthorizedAccess() throws UnauthorizedAccessException
    {
    	return FileCache.isFileGettable(getDataPath());
    }
    
	/**
	 * Returns the data path for this cache data query
	 * 
	 * Example Usage:
	 * 		String dataPath = query.getDataPath();
	 * 
	 * @return
	 */
	protected String getDataPath()
	{
		return dataPath;
	}
}
