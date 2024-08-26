package edu.jhuapl.sbmt.query.v2;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.common.collect.Lists;

import edu.jhuapl.ses.jsqrl.api.Key;
import edu.jhuapl.ses.jsqrl.api.Version;
import edu.jhuapl.ses.jsqrl.impl.FixedMetadata;
import edu.jhuapl.ses.jsqrl.impl.InstanceGetter;
import edu.jhuapl.ses.jsqrl.impl.SettableMetadata;
import edu.jhuapl.saavtk.util.DownloadableFileState;
import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.FileUtil;
import edu.jhuapl.saavtk.util.SafeURLPaths;
import edu.jhuapl.saavtk.util.UnauthorizedAccessException;
import edu.jhuapl.sbmt.core.pointing.PointingSource;
import edu.jhuapl.sbmt.query.fixedlist.FixedListSearchMetadata;
import edu.jhuapl.sbmt.query.v2.QueryException.QueryExceptionReason;
import edu.jhuapl.sbmt.query.v2.QueryException.Severity;

/**
 * FixedListDataQuery class. Handles the querying of the a file list (as opposed to a database
 * or a list of files in the cache.  Other classes like DatabaseDataQuery use this as a fallback 
 * mechanism in case an exception is thrown in those classes when trying to make a query. 
 * 
 * For more details, including origin history of some of this code, please see the package README.  
 * 
 * @author steelrj1
 */
public class FixedListDataQuery implements IDataQuery
{
	private CachedDataQuery cachedDataQuery;
	private String rootPath;
	private String dataPath;
	private String dataFileList;
	private ISearchMetadata dataSourceMetadata;
	private boolean showFixedListPrompt = true;
	
    private static final Key<FixedListDataQuery> FIXEDLISTDATAQUERY_KEY = Key.of("fixedListDataQuery");
    private static final Key<ISearchMetadata> METADATA_KEY = Key.of("dataSourceMetadata");
	
    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss");
    private static final DateTimeFormatter YYYY_MMM_DD = DateTimeFormatter.ofPattern("yyyy MMM dd HH:mm:ss");
    private static final DateTimeFormatter OUTPUT_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Constructor.  
	 * 
	 * @param searchMetadata
	 */
	public FixedListDataQuery(ISearchMetadata searchMetadata)
	{
		this.dataSourceMetadata = searchMetadata;
		this.dataPath = searchMetadata.getMetadata().get(DataQuerySourcesMetadata.DATA_PATH);
		this.cachedDataQuery = new CachedDataQuery(dataPath);
		this.rootPath = searchMetadata.getMetadata().get(DataQuerySourcesMetadata.DATA_ROOT_PATH);
		this.dataFileList = searchMetadata.getMetadata().get(DataQuerySourcesMetadata.FILE_LIST);
	}
		
	/**
	 * Method to return results from a data file list on the server/in the cache, while also providing an
	 * optional search string to further narrow the results. 
	 *
     * Example Usage:
     * 
     * 		FetchedResults results = getResultsFromFileListOnServer("imagelist.txt", "*1452*");
	 * 
	 * See {@link edu.jhuapl.sbmt.query.v2.FixedListDataQuery.getResultsFromFileListOnServer(String)}
	 * 
	 * @param dataFileList		the file containing the list of file for this dataset.  Should be located relative to 
     * 							the root directory specified in the ISearchMetadata objects passed into the query
	 * @param searchString		the search string the can be used to further narrow results from the returned set
	 * @return					the FetchedResults objects containing the list of results
	 * @throws QueryException	exception thrown if something in the query goes wrong (updating local cache, converting 
     * 							times, etc)
	 */
	public FetchedResults getResultsFromFileListOnServerWithSearch(String searchString) throws QueryException
    {
		return getResultsFromFileListOnServerWithSearch(dataFileList, searchString);
    }
	
	public FetchedResults getResultsFromFileListOnServerWithSearch(String dataFileList, String searchString) throws QueryException
	{
		FetchedResults fetchedResults = getResultsFromFileListOnServer(dataFileList);

        List<List<String>> results = new ArrayList<>();
        if (searchString != null && !searchString.isEmpty())
        {
            searchString = cachedDataQuery.wildcardToPathRegex(searchString);
            for (List<String> result : fetchedResults.getFetchedData())
            {
                if (!result.get(0).matches(searchString)) continue;
                results.add(result);
            }
        }
        else
        	results.addAll(fetchedResults.getFetchedData());
        return new FetchedResults("", FetchedResultsType.FIXEDLIST, results);
	}
	
	public FetchedResults getResultsFromFileListOnServer() throws QueryException
    {
		return getResultsFromFileListOnServer(dataFileList);
    }

    /**
     * Method to return results from a data file list on the server/in the cache
     * 
     * Example Usage:
     * 
     * 		FetchedResults results = getResultsFromFileListOnServer("imagelist.txt");
     * 
     * @param dataFileList		the file containing the list of file for this dataset.  Should be located relative to 
     * 							the root directory specified in the ISearchMetadata objects passed into the query
     * @return					the FetchedResults objects containing the list of results
     * @throws QueryException	exception thrown if something in the query goes wrong (updating local cache, converting 
     * 							times, etc)
     */
    public FetchedResults getResultsFromFileListOnServer(String dataFileList) throws QueryException
    {
    	String dataListPath = rootPath + "/" + dataFileList;
    	DownloadableFileState state = FileCache.refreshStateInfo(dataListPath);
    	if (!state.isAccessible())
    	{
        	throw new QueryException("Search returned no results because SBMT is unable to access the "
        			+ "fixed list of files from the server.", Severity.WARNING, 
        			QueryExceptionReason.FIXED_LIST_NOT_ACCESSIBLE);
    	}

        if (!dataPath.endsWith("/"))
            dataPath += "/";

        List<List<String>> results = new ArrayList<>();

        if (!cachedDataQuery.checkAuthorizedAccess())
        {
        	throw new QueryException("Search returned no results because the user is not "
        			+ "authorized to read this fixed list of files.", Severity.WARNING,
        			QueryExceptionReason.FIXED_LIST_NOT_AUTHORIZED);
        }

        File file = FileCache.getFileFromServer(dataListPath);
        if (!file.exists()) return new FetchedResults("Empty Result Set (data file list does not exist", 
        												FetchedResultsType.FIXEDLIST, results);

        // Let user know that search uses fixed list and ignores search parameters
        //TODO This needs to move somewhere else
//        if (!Boolean.parseBoolean(System.getProperty("java.awt.headless")) && showFixedListPrompt)
//        	JOptionPane.showMessageDialog(null,
//                "Search uses a fixed list and ignores all but file name search parameters.",
//                "Notification",
//                JOptionPane.INFORMATION_MESSAGE);
        
        try
        {
            List<String> lines = FileUtil.getFileLinesAsStringList(file.getAbsolutePath());
            for (String line : lines)
            {
                List<String> vals = Lists.newArrayList(line.trim().split("\\s+"));
                String timeString = interpretTimeSubStrings(vals.subList(1, vals.size()));
                List<String> res = new ArrayList<>();
                res.add(dataPath + vals.get(0));
                res.add("" + new DateTime(timeString, DateTimeZone.UTC).getMillis());
                results.add(res);
            }
            cachedDataQuery.updateDataInventory(results);
        }
        catch (IOException e)
        {
        	throw new QueryException("Could not parse the fixed list; please contact "
        			+ "SBMT Support for assistance", Severity.ERROR, 
        			QueryExceptionReason.FIXED_LIST_PARSE_ERROR, e);
        }
        System.out.println("FixedListDataQuery: getResultsFromFileListOnServer: ");
        return new FetchedResults("", FetchedResultsType.FIXEDLIST, results);
    }

	/**
	 * Getter to return the cached data query object
	 * 
	 * @return
	 */
	public CachedDataQuery getCachedDataQuery()
	{
		return cachedDataQuery;
	}
	
	/**
	 * Returns the root path for the query
	 * 
	 * @return
	 */
	public String getRootPath()
	{
		return rootPath;
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
		return cachedDataQuery.getDataPath();
	}
	
	/**
	 * Returns whether the show fixed prompt list boolean is true or not
	 * 
	 * @return
	 */
	protected boolean isShowFixedListPrompt()
	{
		return showFixedListPrompt;
	}

	/**
	 * Sets the show fixed list prompt
	 * 
	 * @param showFixedListPrompt
	 */
	public void setShowFixedListPrompt(boolean showFixedListPrompt)
	{
		this.showFixedListPrompt = showFixedListPrompt;
	}

	/**
	 * Gets the data source metadata object
	 * 
	 * @return the data source metadata for this query
	 */
	protected ISearchMetadata getDataSourceMetadata()
	{
		return dataSourceMetadata;
	}
	
	@Override
	public FetchedResults runQuery(ISearchMetadata queryMetadata) throws QueryException
	{
		FixedMetadata metadata = queryMetadata.getMetadata();
        String fileListRoot = metadata.get(DataQuerySourcesMetadata.FILE_LIST);
//        String searchString = metadata.get(DatabaseSearchMetadata.SEARCH_STRING);
        String searchString = queryMetadata.getSearchString();
//        rootPath = metadata.get(DataQuerySourcesMetadata.DATA_ROOT_PATH);
        rootPath = metadata.get(FixedListSearchMetadata.ROOT_PATH);

        String fileListSuffix = "";
        PointingSource imageSource = PointingSource.valueFor(metadata.get(DataQuerySourcesMetadata.POINTING_SOURCE));
        if (imageSource != null)
	        switch (imageSource)
	        {
	            case GASKELL:
	            case CORRECTED:
	                fileListSuffix = "sum";
	                break;
	            case SPICE:
	            case CORRECTED_SPICE:
	                fileListSuffix = "info";
	                break;
	            default:
	                // No pointing-specific suffix, use a blank.
	                fileListSuffix = "";
	                break;
	        }
        String fileList = getFileList(fileListRoot, fileListSuffix);
		return getResultsFromFileListOnServerWithSearch(fileList, searchString);
    }

    /**
     * Returns the file list name with the given file suffix.  Checks for existence on server 
     * 
     * Example Usage:
     * 		String fileList = query.getFileList("imagelist.txt", "sum");
     * 
     * NOTE: this file is package private solely for the purpose of unit testing.  
     * 
     * @param fileList
     * @param fileListSuffix
     * @return
     */
    String getFileList(final String fileList, String fileListSuffix)
    {
        // -----------------------------------------------
        // This whole section is just for backward compatibility with early (indirect) callers of this method. Need to
        // accept the following variations:
        // fileList == "imgagelist-sum.txt", "imagelist-sum", "imagelist.txt"
        String fileListRoot = fileList;
        if (fileListRoot.endsWith(".txt"))
        {
            fileListRoot = fileListRoot.substring(0, fileListRoot.length() - ".txt".length());
        }
        
        if (fileListRoot.endsWith("-" + fileListSuffix))
        {
        	fileListRoot = FilenameUtils.getBaseName(fileListRoot);
        }
        else if (fileListRoot.lastIndexOf("-") != -1)
        {
        	throw new IllegalArgumentException("Mismatch between pointing type (" + fileListSuffix + ") and "
            		+ "name of file list: " + fileList);
        }
        int dashIndex = fileListRoot.lastIndexOf("-");
        if (dashIndex != -1) fileListRoot = fileListRoot.substring(0, dashIndex);
        	
        // End backward-compatibility section.
        // -----------------------------------------------

        // This is the "real" guts of this method.
        final String fileListWithoutSuffix = fileListRoot + ".txt";

        if (fileListSuffix.isEmpty())	return fileListWithoutSuffix;
        
        final String fileListWithSuffix = fileListRoot + "-" + fileListSuffix + ".txt";
        try
        {
            if (FileCache.instance().isAccessible(SafeURLPaths.instance().getString(rootPath, fileListWithSuffix)))
            {
                return fileListWithSuffix;
            }
            else
            {
                System.out.println("Could not find " + fileListWithSuffix + ". Trying " 
                							+ fileListWithoutSuffix + " instead");
                return fileListWithoutSuffix;
            }
        }
        catch (@SuppressWarnings("unused") UnauthorizedAccessException e)
        {
            System.out.println("Could not access " + fileListWithSuffix + ". Trying " 
            								+ fileListWithoutSuffix + " instead");
            return fileListWithoutSuffix;
        }
	}
    

	/**
	 * Registers this class with the metadata system
	 */
	public static void initializeSerializationProxy()
	{
    	InstanceGetter.defaultInstanceGetter().register(FIXEDLISTDATAQUERY_KEY, (metadata) -> {

    		ISearchMetadata metadataBundle = metadata.get(METADATA_KEY);
	        FixedListDataQuery query = new FixedListDataQuery(metadataBundle);
    		return query;

    	}, FixedListDataQuery.class, query -> {

    		SettableMetadata result = SettableMetadata.of(Version.of(1, 0));
    		result.put(METADATA_KEY, query.getDataSourceMetadata());
    		return result;
    	});

	}

    /**
     * Converts non-standard time strings into UTC acceptable format
     * 
     * Example Usage:
     * 		List<String> vals = new ArrayList<String>();
	 *		vals.add("Sample Event");
	 *		vals.add("2020");
	 *		vals.add("05");
	 *		vals.add("19");
	 *		vals.add("00:10:20");
	 *		String interpretedTimeString = query.interpretTimeSubStrings(vals);
	 *		//returned value is "2020-05-19T00:10:20"
     * 
     * @param vals
     * @return
     * @throws QueryException
     */
    protected String interpretTimeSubStrings(List<String> vals) throws QueryException
    {
        if (vals.isEmpty()) return null;
        if (vals.size() == 1) return vals.get(0);
        String timeString = String.join (" ", vals.subList(1, vals.size()));
        String toReturn = null;
        //Try both time formats: one with MM, the other with MMM
        try
        {
            LocalDateTime date = LocalDateTime.parse(timeString, YYYY_MMM_DD);
            toReturn = date.format(OUTPUT_TIME_FORMAT).replace(" ", "T");
        }
        catch (DateTimeParseException e)
        {
        	try
            {
                LocalDateTime date = LocalDateTime.parse(timeString, YYYY_MM_DD);
                toReturn = date.format(OUTPUT_TIME_FORMAT).replace(" ", "T");
            }
            catch (DateTimeParseException e2)
            {
            	throw new QueryException("Unable to replace space in time string with 'T'", Severity.ERROR, 
            			QueryExceptionReason.TIME_PARSING_ERROR, e2);
            }
        }
        return toReturn;
    }
    
    @Override
    public FetchedResults fallbackQuery(ISearchMetadata queryMetadata) throws QueryException
    {
    	return cachedDataQuery.getCachedResults(null);
    }

	@Override
	public String getGalleryPath()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
