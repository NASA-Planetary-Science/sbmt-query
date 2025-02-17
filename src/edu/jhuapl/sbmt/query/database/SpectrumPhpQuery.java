package edu.jhuapl.sbmt.query.database;

import edu.jhuapl.sbmt.query.fixedlist.FixedListQuery;

import edu.jhuapl.ses.jsqrl.api.Key;
import edu.jhuapl.ses.jsqrl.api.Metadata;
import edu.jhuapl.ses.jsqrl.api.MetadataManager;
import edu.jhuapl.ses.jsqrl.api.Version;
import edu.jhuapl.ses.jsqrl.impl.SettableMetadata;

public abstract class SpectrumPhpQuery extends DatabaseQueryBase implements MetadataManager
{

    protected String spectraTableName;
    protected String cubeTableName;
    private FixedListQuery fixedListQuery;

    public SpectrumPhpQuery()
    {
        this("", "", null);
    }

//    public SpectrumPhpQuery(String rootPath, String tablePrefix)
//    {
//        this(rootPath, tablePrefix, null);
//    }

    public SpectrumPhpQuery(String rootPath, String spectrumTable, String cubeTable)
    {
        super(rootPath, "");
        this.spectraTableName = spectrumTable.toLowerCase();
        this.cubeTableName = cubeTable.toLowerCase();
        fixedListQuery = new FixedListQuery(rootPath);
    }

    @Override
    public String getDataPath()
    {
        return rootPath + "/spectra";
    }

//    public SpectrumPhpQuery(String rootPath, String tablePrefixSpc, String tablePrefixSpice, String galleryPath)
//    {
//        super(galleryPath);
//        this.rootPath = rootPath;
//        this.tablePrefixSpc = tablePrefixSpc.toLowerCase();
//        this.tablePrefixSpice = tablePrefixSpice.toLowerCase();
//    }


//    @Override
//    public SearchResultsMetadata runQuery(SearchMetadata queryMetadata)
//    {
//        FixedMetadata metadata = queryMetadata.getMetadata();
//        double fromIncidence = metadata.get(DatabaseSearchMetadata.FROM_INCIDENCE);
//        double toIncidence = metadata.get(DatabaseSearchMetadata.TO_INCIDENCE);
//        double fromEmission = metadata.get(DatabaseSearchMetadata.FROM_EMISSION);
//        double toEmission = metadata.get(DatabaseSearchMetadata.TO_EMISSION);
//        double fromPhase = metadata.get(DatabaseSearchMetadata.FROM_PHASE);
//        double toPhase = metadata.get(DatabaseSearchMetadata.TO_PHASE);
//        String searchString = metadata.get(DatabaseSearchMetadata.SEARCH_STRING);
//        double startDistance = metadata.get(DatabaseSearchMetadata.FROM_DISTANCE);
//        double stopDistance = metadata.get(DatabaseSearchMetadata.TO_DISTANCE);
//        ImageSource imageSource = ImageSource.valueOf(metadata.get(ImageDatabaseSearchMetadata.IMAGE_SOURCE));
//        double startResolution = metadata.get(ImageDatabaseSearchMetadata.FROM_RESOLUTION);
//        double stopResolution = metadata.get(ImageDatabaseSearchMetadata.TO_RESOLUTION);
//        boolean sumOfProductsSearch = metadata.get(ImageDatabaseSearchMetadata.SUM_OF_PRODUCTS);
//        TreeSet<Integer> cubeList = metadata.get(ImageDatabaseSearchMetadata.CUBE_LIST);
//        List<Integer> camerasSelected = metadata.get(ImageDatabaseSearchMetadata.CAMERAS_SELECTED);
//        List<Integer> filtersSelected = metadata.get(ImageDatabaseSearchMetadata.FILTERS_SELECTED);
//        int limbType = metadata.get(ImageDatabaseSearchMetadata.HAS_LIMB);
//        DateTime startDate = new DateTime(metadata.get(DatabaseSearchMetadata.START_DATE));
//        DateTime stopDate = new DateTime(metadata.get(DatabaseSearchMetadata.STOP_DATE));
//
//
//        final String galleryPath = getGalleryPath();
//        if (imageSource == ImageSource.CORRECTED)
//        {
//            List<List<String>> resultsFromFileListOnServer = getResultsFromFileListOnServer(rootPath + "/sumfiles-corrected/imagelist.txt",
//                    rootPath + "/images/", galleryPath, searchString);
//            return SearchResultsMetadata.of("", resultsFromFileListOnServer);   //"" should really be a query name here, if applicable
//        }
//        else if (imageSource == ImageSource.CORRECTED_SPICE)
//        {
//            List<List<String>> resultsFromFileListOnServer = getResultsFromFileListOnServer(rootPath + "/infofiles-corrected/imagelist.txt",
//                    rootPath + "/images/", galleryPath, searchString);
//            return SearchResultsMetadata.of("", resultsFromFileListOnServer);   //"" should really be a query name here, if applicable
//        }
//        /*else if (imageSource == ImageSource.GASKELL_UPDATED)
//        {
//            return getResultsFromFileListOnServer(rootPath + "/sumfiles_to_be_delivered/imagelist.txt",
//                    rootPath + "/images/", galleryPath);
//        }*/
//
//        List<List<String>> results = new ArrayList<>();
//
//        double minIncidence = Math.min(fromIncidence, toIncidence);
//        double maxIncidence = Math.max(fromIncidence, toIncidence);
//        double minEmission = Math.min(fromEmission, toEmission);
//        double maxEmission = Math.max(fromEmission, toEmission);
//        double minPhase = Math.min(fromPhase, toPhase);
//        double maxPhase = Math.max(fromPhase, toPhase);
//
//        // Get table name.  Examples: erosimages_gaskell, amicacubes_pds_beta
////        String imagesDatabase = getTablePrefix(imageSource) + "images_" + imageSource.getDatabaseTableName();
////        String cubesDatabase = getTablePrefix(imageSource) + "cubes_" + imageSource.getDatabaseTableName();
//        if(SmallBodyViewConfig.betaMode)
//        {
//            imagesDatabase += "_beta";
//            cubesDatabase += "_beta";
//        }
//        else
//        {
//            imagesDatabase += Configuration.getDatabaseSuffix();
//            cubesDatabase += Configuration.getDatabaseSuffix();
//        }
//
//        try
//        {
//        	boolean tableExists = QueryBase.checkForDatabaseTable(imagesDatabase);
//            if (!tableExists) throw new RuntimeException("Database table " + imagesDatabase + " is not available now.");
//
//            if (searchString != null)
//            {
//                HashMap<String, String> args = new HashMap<>();
//                args.put("imagesDatabase", imagesDatabase);
//                args.put("searchString", searchString);
//
//                results = doQuery("searchimages.php", constructUrlArguments(args));
//
//                return SearchResultsMetadata.of("", results);   //"" should really be a query name here, if applicable
//            }
//
//            double minScDistance = Math.min(startDistance, stopDistance);
//            double maxScDistance = Math.max(startDistance, stopDistance);
//            double minResolution = Math.min(startResolution, stopResolution) / 1000.0;
//            double maxResolution = Math.max(startResolution, stopResolution) / 1000.0;
//
//            HashMap<String, String> args = new HashMap<>();
//            args.put("imagesDatabase", imagesDatabase);
//            args.put("cubesDatabase", cubesDatabase);
//            args.put("minResolution", String.valueOf(minResolution));
//            args.put("maxResolution", String.valueOf(maxResolution));
//            args.put("minScDistance", String.valueOf(minScDistance));
//            args.put("maxScDistance", String.valueOf(maxScDistance));
//            args.put("startDate", String.valueOf(startDate.getMillis()));
//            args.put("stopDate", String.valueOf(stopDate.getMillis()));
//            args.put("minIncidence", String.valueOf(minIncidence));
//            args.put("maxIncidence", String.valueOf(maxIncidence));
//            args.put("minEmission", String.valueOf(minEmission));
//            args.put("maxEmission", String.valueOf(maxEmission));
//            args.put("minPhase", String.valueOf(minPhase));
//            args.put("maxPhase", String.valueOf(maxPhase));
//            args.put("limbType", String.valueOf(limbType));
//
//            // Populate args for camera and filter search
//            if(sumOfProductsSearch)
//            {
//                // Sum of products (hierarchical) search: (CAMERA 1 AND FILTER 1) OR ... OR (CAMERA N AND FILTER N)
//                args.put("sumOfProductsSearch", "1");
//                Integer[] camerasSelectedArray = camerasSelected.toArray(new Integer[0]);
//                Integer[] filtersSelectedArray = filtersSelected.toArray(new Integer[0]);
//                int numProducts = camerasSelectedArray.length;
//
//                // Populate search parameters
//                args.put("numProducts", new Integer(numProducts).toString());
//                for(int i=0; i<numProducts; i++)
//                {
//                    args.put("cameraType"+i, new Integer(camerasSelectedArray[i]+1).toString());
//                    args.put("filterType"+i, new Integer(filtersSelectedArray[i]+1).toString());
//                }
//            }
//            else
//            {
//                // Product of sums (legacy) search: (CAMERA 1 OR ... OR CAMERA N) AND (FILTER 1 OR ... FILTER M)
//                args.put("sumOfProductsSearch", "0");
//
//                // Populate search parameters
//                for(Integer c : camerasSelected)
//                {
//                    args.put("cameraType"+(c+1), "1");
//                }
//                for(Integer f : filtersSelected)
//                {
//                    args.put("filterType"+(f+1), "1");
//                }
//            }
//            if (cubeList != null && cubeList.size() > 0)
//            {
//                String cubes = "";
//                int size = cubeList.size();
//                int count = 0;
//                for (Integer i : cubeList)
//                {
//                    cubes += "" + i;
//                    if (count < size-1)
//                        cubes += ",";
//                    ++count;
//                }
//                args.put("cubes", cubes);
//            }
//
//            results = doQuery("searchimages.php", constructUrlArguments(args));
//
//        }
//        catch (RuntimeException e)
//        {
////            e.printStackTrace();
//            System.err.println("GenericPhpQuery: runQuery: falling back to image list");
//            String imageListName = "imagelist-info.txt";
//            if (imageSource.equals(ImageSource.GASKELL))
//            {
//            	imageListName = "imagelist-sum.txt";
//            	if (!FileCache.instance().isAccessible(rootPath + "/" + imageListName))
//            		imageListName = "imagelist.txt";
//            }
//            results = getResultsFromFileListOnServer(rootPath + "/" + imageListName, getDataPath(), getGalleryPath(), searchString);
//        }
//        catch (IOException e)
//        {
////            e.printStackTrace();
//            System.err.println("GenericPhpQuery: runQuery: Can't reach database server, or some other database access failure; falling back to cached results");
//            results = getCachedResults(getDataPath(), searchString);
//        }
//
//        return SearchResultsMetadata.of("", results);   //"" should really be a query name here, if applicable
//    }

    public String getSpectraTableName()
    {
        return spectraTableName;
    }

    public String getCubeTableName()
    {
        return cubeTableName;
    }

    Key<String> rootPathKey = Key.of("rootPath");
    Key<String> spectraTableNameKey = Key.of("spectraTableName");
    Key<String> cubeTableNameKey = Key.of("cubeTableName");
//    Key<String> galleryPathKey = Key.of("galleryPath");

    @Override
    public Metadata store()
    {
        SettableMetadata configMetadata = SettableMetadata.of(Version.of(1, 0));
        write(rootPathKey, rootPath, configMetadata);
        write(spectraTableNameKey, spectraTableName, configMetadata);
        write(cubeTableNameKey, cubeTableName, configMetadata);
        return configMetadata;
    }

    @Override
    public void retrieve(Metadata source)
    {
        rootPath = read(rootPathKey, source);
        spectraTableName = read(spectraTableNameKey, source);
        cubeTableName = read(cubeTableNameKey, source);
    }

	public FixedListQuery getFixedListQuery() {
		return fixedListQuery;
	}
}
