package edu.jhuapl.sbmt.query.v2;

import com.google.common.base.Preconditions;

import crucible.crust.metadata.api.Key;
import crucible.crust.metadata.api.Metadata;
import crucible.crust.metadata.api.Version;
import crucible.crust.metadata.impl.FixedMetadata;
import crucible.crust.metadata.impl.InstanceGetter;
import crucible.crust.metadata.impl.SettableMetadata;
import edu.jhuapl.saavtk.util.SafeURLPaths;
import edu.jhuapl.sbmt.core.pointing.PointingSource;

public class DataQuerySourcesMetadata implements ISearchMetadata
{
	//Metadata keys
    private static final Version DATA_QUERY_SOURCES_DATA_VERSION = Version.of(1, 0);
	protected static final Key<String> DATA_ROOT_PATH = Key.of("Data Root Path");
	protected static final Key<String> DATA_PATH = Key.of("Data Path");
    protected static final Key<String> DB_SPICE_TABLE = Key.of("SPICE Database Prefix");
    protected static final Key<String> DB_SPC_TABLE = Key.of("SPC Database Prefix");
    protected static final Key<String> GALLERY_PATH = Key.of("Gallery Path");
    protected static final Key<String> FILE_LIST = Key.of("File list");
    protected static final Key<String> POINTING_SOURCE = Key.of("Pointing Source");
    private static final Key<DataQuerySourcesMetadata> DATAQUERYSOURCESMETADATA_KEY = Key.of("dataSourcesMetadata");
    private static final Key<Metadata> METADATA_KEY = Key.of("sourceMetadata");
	
    /**
     * FixedMetadata object that holds the various values passed into the DataQuerySourcesMetadata object
     */
    private FixedMetadata searchMetadata;
    private String searchString;

    /**
     * Helper method to builds a metadata object from the passed in values
     * 
     * @param rootPath		the root path for the data in this query
     * @param dataPath		the data path for the data in this query
     * @param spiceDB		the name of the SPICE database table to use
     * @param spcDB			the name of the SPC database table to use
     * @param galleryPath	the path to the image gallery, if applicable
     * @return
     */
    public static DataQuerySourcesMetadata of(String rootPath, String dataPath, String spcDB, String spiceDB, 
    											String galleryPath, PointingSource pointingSource, String dataFileList)
    {	
    	FixedMetadata metadata = 
    			FixedMetadata.of(createSettableMetadata(rootPath, dataPath, spiceDB, spcDB, galleryPath,
    														pointingSource, dataFileList));
    	return new DataQuerySourcesMetadata(metadata);
    }
    
    /**
     * Helper method to builds a metadata object from the passed in values
     * 
     * @param rootPath		the root path for the data in this query
     * @param dataPath		the data path for the data in this query
     * @param spiceDB		the name of the SPICE database table to use
     * @param spcDB			the name of the SPC database table to use
     * @param galleryPath	the path to the image gallery, if applicable
     * @return
     */
    public static DataQuerySourcesMetadata of(String rootPath, String dataPath, String spcDB, String spiceDB,
    											String galleryPath, PointingSource pointingSource)
    {	
    	return DataQuerySourcesMetadata.of(rootPath, dataPath, spcDB, spiceDB, galleryPath, pointingSource, "");
    }
    
    /**
     * Helper method to builds a metadata object from the passed in values
     * 
     * @param rootPath		the root path for the data in this query
     * @param dataPath		the data path for the data in this query
     * @param spiceDB		the name of the SPICE database table to use
     * @param spcDB			the name of the SPC database table to use
     * @param galleryPath	the path to the image gallery, if applicable
     * @return
     */
    public static DataQuerySourcesMetadata of(String rootPath, String dataPath, String spcDB, String spiceDB, String galleryPath)
    {	
    	if (dataPath.isEmpty()) dataPath = SafeURLPaths.instance().getString(rootPath, "images");;
    	return DataQuerySourcesMetadata.of(rootPath, dataPath, spcDB, spiceDB, galleryPath, null, "");
    }
    
    /**
     * Helper method to builds a metadata object from the passed in values
     * 
     * @param rootPath		the root path for the data in this query
     * @param dataPath		the data path for the data in this query
     * @param spiceDB		the name of the SPICE database table to use
     * @param galleryPath	the path to the image gallery, if applicable
     * @return
     */
    public static DataQuerySourcesMetadata of(String rootPath, String dataPath, String spiceDB, String galleryPath)
    {	
    	return DataQuerySourcesMetadata.of(rootPath, dataPath, spiceDB, spiceDB, galleryPath);
    }
    
    /**
     * Helper method to builds a metadata object from the passed in values
     * 
     * @param rootPath		the root path for the data in this query
     * @param dataPath		the data path for the data in this query
     * @param spiceDB		the name of the SPICE database table to use
     * @return
     */
    public static DataQuerySourcesMetadata of(String rootPath, String dataPath, String spiceDB)
    {	
    	return DataQuerySourcesMetadata.of(rootPath, dataPath, spiceDB, "", "");
    }
    
    /**
     * Helper method to builds a metadata object from the passed in values
     * 
     * @param rootPath		the root path for the data in this query
     * @param dataPath		the data path for the data in this query
     * @return
     */
    public static DataQuerySourcesMetadata of(String rootPath, String dataPath)
    {	
    	return DataQuerySourcesMetadata.of(rootPath, dataPath, "", "", "");
    }

	/**
	 * Helper method to builds a metadata object from the passed in values
	 * 
	 * @param rootPath		the root path for the data in this query
	 * @param dataPath		the data path for the data in this query
	 * @param spiceDB		the name of the SPICE database table to use
	 * @param spcDB			the name of the SPC database table to use
	 * @param galleryPath	the path to the image gallery, if applicable
	 * @return
	 */
	private static SettableMetadata createSettableMetadata(String rootPath, 
															 String dataPath, String spiceDB, 
															 String spcDB, String galleryPath,
															 PointingSource pointingSource, String dataFileList)
	{
		Preconditions.checkNotNull(rootPath);
		Preconditions.checkNotNull(dataPath);
		SettableMetadata metadata = SettableMetadata.of(DATA_QUERY_SOURCES_DATA_VERSION);
		metadata.put(DataQuerySourcesMetadata.DATA_ROOT_PATH, rootPath);
		metadata.put(DataQuerySourcesMetadata.DATA_PATH, dataPath);
		metadata.put(DataQuerySourcesMetadata.DB_SPICE_TABLE, spiceDB);
		metadata.put(DataQuerySourcesMetadata.DB_SPC_TABLE, spcDB);
		metadata.put(DataQuerySourcesMetadata.GALLERY_PATH, galleryPath);
		if (pointingSource != null)
			metadata.put(DataQuerySourcesMetadata.POINTING_SOURCE, pointingSource.toString());
		metadata.put(DataQuerySourcesMetadata.FILE_LIST, dataFileList);
		return metadata;
	}
    
	/**
	 * Constructor
	 * 
	 * @param metadata	Metadata object containing information on source locations for this query
	 */
	protected DataQuerySourcesMetadata(FixedMetadata metadata)
    {
        this.searchMetadata = metadata;
    }

	@Override
	public FixedMetadata getMetadata()
	{
		return searchMetadata;
	}
	
	/**
	 * Returns the root path for this query
	 * 
	 * @return	String representing the data path for this query
	 */
	public String getRootPath()
	{
		return searchMetadata.get(DATA_ROOT_PATH);
	}
	
	/**
	 * Returns the data path for this query
	 * 
	 * @return	String representing the data path for this query
	 */
	public String getDataPath()
	{
		return searchMetadata.get(DATA_PATH);
	}
	
	/**
	 * The SPICE Database table name for looking up pointing file names
	 * 
	 * @return	String representing the SPICE database table name
	 */
	public String getDBSpiceTable()
	{
		return searchMetadata.get(DB_SPICE_TABLE);
	}
	
	/**
	 * The SPC Database table name for looking up pointing file names
	 * 
	 * @return	String representing the SPC database table name
	 */
	public String getDBSPCTable()
	{
		return searchMetadata.get(DB_SPC_TABLE);
	}
	
	/**
	 * Returns the gallery path for this query
	 * 
	 * @return	String representing the gallery path for this query
	 */
	public String getGalleryPath()
	{
		return searchMetadata.get(GALLERY_PATH);
	}
	
	 /**
     * Returns the pointing source to use during this query
     * 
     * @return		PointingSource object for this query
     */
    public PointingSource getPointingSource()
    {
        String pointingSourceString = getMetadata().get(POINTING_SOURCE);
        return PointingSource.valueOf(pointingSourceString);
    }
    
    /**
     * Returns the file list to use in this query
     * 
     * @return	The string describing the file list for this query
     */
    public String getFilelist()
    {
        return getMetadata().get(FILE_LIST);
    }
	
	public String getSearchString()
	{
		return searchString;
	}

	public void setSearchString(String searchString)
	{
		this.searchString = searchString;
	}

	/**
	 * Registers this class with the metadata system
	 */
	public static void initializeSerializationProxy()
	{
    	InstanceGetter.defaultInstanceGetter().register(DATAQUERYSOURCESMETADATA_KEY, (metadata) -> {

    		String rootPath = metadata.get(DataQuerySourcesMetadata.DATA_ROOT_PATH);
			String dataPath = metadata.get(DataQuerySourcesMetadata.DATA_PATH);
			String spiceTable = metadata.get(DataQuerySourcesMetadata.DB_SPICE_TABLE);
			String spcTable = metadata.get(DataQuerySourcesMetadata.DB_SPC_TABLE);
			String galleryPath = metadata.get(DataQuerySourcesMetadata.GALLERY_PATH);
    		return DataQuerySourcesMetadata.of(rootPath, dataPath, spiceTable, spcTable, galleryPath);

    	}, DataQuerySourcesMetadata.class, dataSourceMetadata -> {

    		SettableMetadata result = SettableMetadata.of(Version.of(1, 0));
    		result.put(METADATA_KEY, dataSourceMetadata.getMetadata());
    		result.put(DATA_ROOT_PATH, dataSourceMetadata.getRootPath());
    		result.put(DATA_PATH, dataSourceMetadata.getDataPath());
    		result.put(DB_SPICE_TABLE, dataSourceMetadata.getDBSpiceTable());
    		result.put(DB_SPC_TABLE, dataSourceMetadata.getDBSPCTable());
    		result.put(GALLERY_PATH, dataSourceMetadata.getGalleryPath());
    		return result;
    	});

	}

}
