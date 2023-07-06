package edu.jhuapl.sbmt.query.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.joda.time.DateTime;

import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.saavtk.util.FileCache;
import edu.jhuapl.saavtk.util.SafeURLPaths;
import edu.jhuapl.sbmt.config.SmallBodyViewConfig;
import edu.jhuapl.sbmt.core.pointing.PointingSource;
import edu.jhuapl.sbmt.query.QueryBase;
import edu.jhuapl.sbmt.query.SearchMetadata;
import edu.jhuapl.sbmt.query.SearchResultsMetadata;

import crucible.crust.metadata.api.Key;
import crucible.crust.metadata.api.Metadata;
import crucible.crust.metadata.api.MetadataManager;
import crucible.crust.metadata.api.Version;
import crucible.crust.metadata.impl.FixedMetadata;
import crucible.crust.metadata.impl.SettableMetadata;

public class GenericPhpQuery extends DatabaseQueryBase implements MetadataManager
{

    private String dataPath;
    private String tablePrefixSpc;
    private String tablePrefixSpice;
    private boolean publicOnly = false;
    private String imageNameTable = null;

    public GenericPhpQuery()
    {
        this("", "", "", null, null);
    }

    public GenericPhpQuery(String rootPath, String tablePrefix)
    {
        this(rootPath, tablePrefix, "", null, null);
    }

    public GenericPhpQuery(String rootPath, String tablePrefixSpc, String galleryPath)
    {
        this(rootPath, tablePrefixSpc, tablePrefixSpc, galleryPath, null);
    }

    public GenericPhpQuery(String rootPath, String tablePrefixSpc, String tablePrefixSpice, String galleryPath)
    {
        this(rootPath, tablePrefixSpc, tablePrefixSpc, galleryPath, null);
    }

    public GenericPhpQuery(String rootPath, String tablePrefixSpc, String tablePrefixSpice, String galleryPath, String dataPath)
    {
        super(rootPath, galleryPath);
        this.dataPath = dataPath != null ? dataPath : SafeURLPaths.instance().getString(rootPath, "images");
        this.tablePrefixSpc = tablePrefixSpc.toLowerCase();
        this.tablePrefixSpice = tablePrefixSpice.toLowerCase();
    }

    @Override
    public GenericPhpQuery copy()
    {
        return new GenericPhpQuery(rootPath, tablePrefixSpc, tablePrefixSpice, galleryPath, dataPath);
    }

    public void setPublicOnly(boolean publicOnly)
    {
    	this.publicOnly = publicOnly;

    }

    public void setImageNameTable(String imageNameTable)
    {
    	this.imageNameTable = imageNameTable;
    }


    @Override
    public String getDataPath()
    {
        return dataPath;
    }

    @Override
    public SearchResultsMetadata runQuery(SearchMetadata queryMetadata)
    {
        FixedMetadata metadata = queryMetadata.getMetadata();
        double fromIncidence = metadata.get(DatabaseSearchMetadata.FROM_INCIDENCE);
        double toIncidence = metadata.get(DatabaseSearchMetadata.TO_INCIDENCE);
        double fromEmission = metadata.get(DatabaseSearchMetadata.FROM_EMISSION);
        double toEmission = metadata.get(DatabaseSearchMetadata.TO_EMISSION);
        double fromPhase = metadata.get(DatabaseSearchMetadata.FROM_PHASE);
        double toPhase = metadata.get(DatabaseSearchMetadata.TO_PHASE);
        String searchString = metadata.get(DatabaseSearchMetadata.SEARCH_STRING);
        double startDistance = metadata.get(DatabaseSearchMetadata.FROM_DISTANCE);
        double stopDistance = metadata.get(DatabaseSearchMetadata.TO_DISTANCE);
        PointingSource imageSource = PointingSource.valueOf(metadata.get(ImageDatabaseSearchMetadata.IMAGE_SOURCE));
        double startResolution = metadata.get(ImageDatabaseSearchMetadata.FROM_RESOLUTION);
        double stopResolution = metadata.get(ImageDatabaseSearchMetadata.TO_RESOLUTION);
        boolean sumOfProductsSearch = metadata.get(ImageDatabaseSearchMetadata.SUM_OF_PRODUCTS);
        TreeSet<Integer> cubeList = metadata.get(ImageDatabaseSearchMetadata.CUBE_LIST);
        List<Integer> camerasSelected = metadata.get(ImageDatabaseSearchMetadata.CAMERAS_SELECTED);
        List<Integer> filtersSelected = metadata.get(ImageDatabaseSearchMetadata.FILTERS_SELECTED);
        int limbType = metadata.get(ImageDatabaseSearchMetadata.HAS_LIMB);
        DateTime startDate = new DateTime(metadata.get(DatabaseSearchMetadata.START_DATE));
        DateTime stopDate = new DateTime(metadata.get(DatabaseSearchMetadata.STOP_DATE));
//        List<Integer> polygonTypes = metadata.get(DatabaseSearchMetadata.POLYGON_TYPES);


        final String galleryPath = getGalleryPath();
        if (imageSource == PointingSource.CORRECTED)
        {
            List<List<String>> resultsFromFileListOnServer = getResultsFromFileListOnServer(rootPath + "/sumfiles-corrected/imagelist.txt",
                    rootPath + "/images/", galleryPath, searchString);
            return SearchResultsMetadata.of("", resultsFromFileListOnServer);   //"" should really be a query name here, if applicable
        }
//        else if (imageSource == ImageSource.CORRECTED_SPICE)
//        {
//            List<List<String>> resultsFromFileListOnServer = getResultsFromFileListOnServer(rootPath + "/infofiles-corrected/imagelist.txt",
//                    rootPath + "/images/", galleryPath, searchString);
//            return SearchResultsMetadata.of("", resultsFromFileListOnServer);   //"" should really be a query name here, if applicable
//        }
        /*else if (imageSource == ImageSource.GASKELL_UPDATED)
        {
            return getResultsFromFileListOnServer(rootPath + "/sumfiles_to_be_delivered/imagelist.txt",
                    rootPath + "/images/", galleryPath);
        }*/

        List<List<String>> results = new ArrayList<>();

        double minIncidence = Math.min(fromIncidence, toIncidence);
        double maxIncidence = Math.max(fromIncidence, toIncidence);
        double minEmission = Math.min(fromEmission, toEmission);
        double maxEmission = Math.max(fromEmission, toEmission);
        double minPhase = Math.min(fromPhase, toPhase);
        double maxPhase = Math.max(fromPhase, toPhase);

        // Get table name.  Examples: erosimages_gaskell, amicacubes_pds_beta
        String imagesDatabase = getTablePrefix(imageSource) + "images_" + imageSource.getDatabaseTableName();
        String cubesDatabase = getTablePrefix(imageSource) + "cubes_" + imageSource.getDatabaseTableName();
        if(SmallBodyViewConfig.betaMode)
        {
            imagesDatabase += "_beta";
            cubesDatabase += "_beta";
        }
        else
        {
            imagesDatabase += Configuration.getDatabaseSuffix();
            cubesDatabase += Configuration.getDatabaseSuffix();
        }

        try
        {
        	boolean tableExists = QueryBase.checkForDatabaseTable(imagesDatabase);
            if (!tableExists) throw new RuntimeException("Database table " + imagesDatabase + " is not available now.");

            if (searchString != null)
            {
                HashMap<String, String> args = new HashMap<>();
                args.put("imagesDatabase", imagesDatabase);
                args.put("searchString", searchString);

                if (imageNameTable != null)
                {
                	String visibilityStr = publicOnly ? "public" : "public,private";
                	args.put("imageLocationDatabase", imagesDatabase);
                	args.put("visibilityStr", visibilityStr);
                	results = doQuery("searchimages2.php", constructUrlArguments(args));
                }
                else
                {
                	results = doQuery("searchimages.php", constructUrlArguments(args));
                }



                return SearchResultsMetadata.of("", results);   //"" should really be a query name here, if applicable
            }

            double minScDistance = Math.min(startDistance, stopDistance);
            double maxScDistance = Math.max(startDistance, stopDistance);
            double minResolution = Math.min(startResolution, stopResolution) / 1000.0;
            double maxResolution = Math.max(startResolution, stopResolution) / 1000.0;

            HashMap<String, String> args = new HashMap<>();
            args.put("imagesDatabase", imagesDatabase);
            args.put("cubesDatabase", cubesDatabase);
            args.put("minResolution", String.valueOf(minResolution));
            args.put("maxResolution", String.valueOf(maxResolution));
            args.put("minScDistance", String.valueOf(minScDistance));
            args.put("maxScDistance", String.valueOf(maxScDistance));
            args.put("startDate", String.valueOf(startDate.getMillis()));
            args.put("stopDate", String.valueOf(stopDate.getMillis()));
            args.put("minIncidence", String.valueOf(minIncidence));
            args.put("maxIncidence", String.valueOf(maxIncidence));
            args.put("minEmission", String.valueOf(minEmission));
            args.put("maxEmission", String.valueOf(maxEmission));
            args.put("minPhase", String.valueOf(minPhase));
            args.put("maxPhase", String.valueOf(maxPhase));
            args.put("limbType", String.valueOf(limbType));

            // Populate args for camera and filter search
            if(sumOfProductsSearch)
            {
                // Sum of products (hierarchical) search: (CAMERA 1 AND FILTER 1) OR ... OR (CAMERA N AND FILTER N)
                args.put("sumOfProductsSearch", "1");
                Integer[] camerasSelectedArray = camerasSelected.toArray(new Integer[0]);
                Integer[] filtersSelectedArray = filtersSelected.toArray(new Integer[0]);
                int numProducts = camerasSelectedArray.length;

                // Populate search parameters
                args.put("numProducts", new Integer(numProducts).toString());
                for(int i=0; i<numProducts; i++)
                {
                    args.put("cameraType"+i, new Integer(camerasSelectedArray[i]+1).toString());
                    args.put("filterType"+i, new Integer(filtersSelectedArray[i]+1).toString());
                }
            }
            else
            {
                // Product of sums (legacy) search: (CAMERA 1 OR ... OR CAMERA N) AND (FILTER 1 OR ... FILTER M)
                args.put("sumOfProductsSearch", "0");

                // Populate search parameters
                for(Integer c : camerasSelected)
                {
                    args.put("cameraType"+(c+1), "1");
                }
                for(Integer f : filtersSelected)
                {
                    args.put("filterType"+(f+1), "1");
                }
            }
            if (cubeList != null && cubeList.size() > 0)
            {
                String cubes = "";
                int size = cubeList.size();
                int count = 0;
                for (Integer i : cubeList)
                {
                    cubes += "" + i;
                    if (count < size-1)
                        cubes += ",";
                    ++count;
                }
                args.put("cubes", cubes);
            }

            if (imageNameTable != null)
            {
//            	System.out.println("GenericPhpQuery: runQuery: image name table " + imageNameTable);
            	String visibilityStr = publicOnly ? "public" : "public,private";
            	args.put("imageLocationDatabase", imageNameTable);
            	args.put("visibilityStr", visibilityStr);
            	results = doQuery("searchimages2.php", constructUrlArguments(args), true);
            }
            else
            {
            	results = doQuery("searchimages.php", constructUrlArguments(args));
            }
//            results = doQuery("searchimages.php", constructUrlArguments(args));

        }
        catch (RuntimeException e)
        {
//            e.printStackTrace();
            System.err.println("GenericPhpQuery: runQuery: falling back to image list");
            String imageListName = "imagelist-info.txt";
            if (imageSource.equals(PointingSource.GASKELL))
            {
            	imageListName = "imagelist-sum.txt";
            	if (!FileCache.instance().isAccessible(rootPath + "/" + imageListName))
            		imageListName = "imagelist.txt";
            }
            results = getResultsFromFileListOnServer(rootPath + "/" + imageListName, getDataPath(), getGalleryPath(), searchString);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("GenericPhpQuery: runQuery: Can't reach database server, or some other database access failure; falling back to cached results");
            results = getCachedResults(getDataPath(), searchString);
        }

        return SearchResultsMetadata.of("", results);   //"" should really be a query name here, if applicable
    }


//    @Override
//    public List<List<String>> runQuery(
//            @SuppressWarnings("unused") String type,
//            DateTime startDate,
//            DateTime stopDate,
//            boolean sumOfProductsSearch,
//            List<Integer> camerasSelected,
//            List<Integer> filtersSelected,
//            double startDistance,
//            double stopDistance,
//            double startResolution,
//            double stopResolution,
//            String searchString,
//            @SuppressWarnings("unused") List<Integer> polygonTypes,
//            double fromIncidence,
//            double toIncidence,
//            double fromEmission,
//            double toEmission,
//            double fromPhase,
//            double toPhase,
//            TreeSet<Integer> cubeList,
//            ImageSource imageSource,
//            int limbType)
//    {
//        final String galleryPath = getGalleryPath();
//        if (imageSource == ImageSource.CORRECTED)
//        {
//            return getResultsFromFileListOnServer(rootPath + "/sumfiles-corrected/imagelist.txt",
//                    rootPath + "/images/", galleryPath, searchString);
//        }
//        else if (imageSource == ImageSource.CORRECTED_SPICE)
//        {
//            return getResultsFromFileListOnServer(rootPath + "/infofiles-corrected/imagelist.txt",
//                    rootPath + "/images/", galleryPath, searchString);
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
//        String imagesDatabase = tablePrefix + "images_" + imageSource.getDatabaseTableName();
//        String cubesDatabase = tablePrefix + "cubes_" + imageSource.getDatabaseTableName();
//        if(SmallBodyViewConfig.betaMode)
//        {
//            imagesDatabase += "_beta";
//            cubesDatabase += "_beta";
//        }
//
//        try
//        {
//            if (searchString != null)
//            {
//                HashMap<String, String> args = new HashMap<>();
//                args.put("imagesDatabase", imagesDatabase);
//                args.put("searchString", searchString);
//
//                results = doQuery("searchimages.php", constructUrlArguments(args));
//
//                return results;
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
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            results = getResultsFromFileListOnServer(rootPath + "/imagelist.txt", getDataPath(), getGalleryPath(), searchString);
//        }
//
//        return results;
//    }

    public String getTablePrefix(PointingSource source)
    {
        return source == PointingSource.SPICE ? tablePrefixSpice : tablePrefixSpc;
    }

    public String getTablePrefixSpc()
    {
        return tablePrefixSpc;
    }

    public String getTablePrefixSpice()
    {
        return tablePrefixSpice;
    }

    private static final Key<String> rootPathKey = Key.of("rootPath");
    private static final Key<String> dataPathKey = Key.of("dataPath");
    private static final Key<String> tablePrefixSpcKey = Key.of("tablePrefixSpc");
    private static final Key<String> tablePrefixSpiceKey = Key.of("tablePrefixSpice");
    private static final Key<String> galleryPathKey = Key.of("galleryPath");
    private static final Key<String> imageNameTableKey = Key.of("imageNameTable");
    private static final Key<Boolean> publicOnlyKey = Key.of("publicOnly");

    @Override
    public Metadata store()
    {
        SettableMetadata configMetadata = SettableMetadata.of(Version.of(1, 1));
        write(rootPathKey, rootPath, configMetadata);
        write(dataPathKey, dataPath, configMetadata);
        write(tablePrefixSpcKey, tablePrefixSpc, configMetadata);
        write(tablePrefixSpiceKey, tablePrefixSpice, configMetadata);
        write(galleryPathKey, galleryPath, configMetadata);
        if (imageNameTable != null)
        	write(imageNameTableKey, imageNameTable, configMetadata);
        write(publicOnlyKey, publicOnly, configMetadata);
        return configMetadata;
    }

    @Override
    public void retrieve(Metadata source)
    {
        rootPath = read(rootPathKey, source);
        String dataPath = read(dataPathKey, source);
        this.dataPath = dataPath != null ? dataPath : SafeURLPaths.instance().getString(rootPath, "images");
        tablePrefixSpc = read(tablePrefixSpcKey, source);
        tablePrefixSpice = read(tablePrefixSpiceKey, source);
        if (source.hasKey(galleryPathKey)) galleryPath = read(galleryPathKey, source);
        if (source.hasKey(imageNameTableKey)) imageNameTable = read(imageNameTableKey, source);
        if (source.hasKey(publicOnlyKey)) publicOnly = read(publicOnlyKey, source);
    }


}
