package query.database;
//package edu.jhuapl.sbmt.query.database;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import org.joda.time.DateTime;
//import org.junit.Assert;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//
//import com.google.common.collect.Ranges;
//
//import edu.jhuapl.saavtk.model.ShapeModelBody;
//import edu.jhuapl.saavtk.model.ShapeModelType;
//import edu.jhuapl.saavtk.util.Configuration;
//import edu.jhuapl.sbmt.client.SbmtMultiMissionTool;
//import edu.jhuapl.sbmt.client.SmallBodyViewConfig;
//import edu.jhuapl.sbmt.model.image.ImageSource;
//import edu.jhuapl.sbmt.query.QueryBase;
//import edu.jhuapl.sbmt.query.SearchResultsMetadata;
//import edu.jhuapl.sbmt.tools.Authenticator;
//
//
//@TestMethodOrder(Alphanumeric.class)
//class TestGenericPhpQuery
//{
//	private static GenericPhpQuery query = null;
//	private static String tableName = "bennu_altwgspcv20190828_mapcam";
//
//	@BeforeAll
//	static void setUpBeforeClass() throws Exception
//	{
//		Configuration.setAPLVersion(true);
//        SbmtMultiMissionTool.configureMission();
//        Authenticator.authenticate();
//        SmallBodyViewConfig.initialize();
//        SmallBodyViewConfig config = null;
//        String bodyName = "RQ36";
//        String authorName = "ALTWG-SPC-v20190828";
//        String versionString = null;
//        if (versionString != null)
//        	config = SmallBodyViewConfig.getSmallBodyConfig(ShapeModelBody.valueOf(bodyName), ShapeModelType.provide(authorName), versionString);
//        else
//        	config = SmallBodyViewConfig.getSmallBodyConfig(ShapeModelBody.valueOf(bodyName), ShapeModelType.provide(authorName));
//
//		query = new GenericPhpQuery(config.rootDirOnServer + "/mapcam", tableName, tableName, config.rootDirOnServer + "/mapcam/gallery");
//	}
//
//	@AfterAll
//	static void tearDownAfterClass() throws Exception
//	{
//	}
//
//	@Test
//	void testGetDataPath()
//	{
//		System.out.println("TestGenericPhpQuery: testGetDataPath: " + query.getDataPath());
//		Assert.assertEquals(query.getRootPath() + "/images", query.getDataPath());
//	}
//
//	@Test
//	void testRunQuerySearchMetadata()
//	{
//		ImageDatabaseSearchMetadata searchMetadata = ImageDatabaseSearchMetadata.of("", new DateTime(946684800000L), new DateTime(2524608000000L),
//                Ranges.closed(0.0, 1000.0),
//                null, new ArrayList<Integer>(),
//                Ranges.closed(0.0, 180.0),
//                Ranges.closed(0.0, 180.0),
//                Ranges.closed(0.0, 180.0),
//                false, new ArrayList<Integer>(), new ArrayList<Integer>(),
//                Ranges.closed(0.0, 1000.0),
//                null, ImageSource.GASKELL, 0);
//		SearchResultsMetadata searchResultsMetadata = query.runQuery(searchMetadata);
//		System.out.println("TestGenericPhpQuery: testRunQuerySearchMetadata: metadata is " + searchResultsMetadata);
//		System.out.println("TestGenericPhpQuery: testRunQuerySearchMetadata: num results " + searchResultsMetadata.getResultlist().size());
//		assertEquals(false, searchResultsMetadata.isLoadFailed());
//		assertEquals(1722, searchResultsMetadata.getResultlist().size());
//	}
//
//	@Test
//	void testGetTablePrefix()
//	{
//		ImageSource source = ImageSource.SPICE;
//		Assert.assertEquals(tableName, query.getTablePrefix(source));
//	}
//
//	@Test
//	void testGetTablePrefixSpc()
//	{
//		Assert.assertEquals(tableName, query.getTablePrefixSpc());
//	}
//
//	@Test
//	void testGetTablePrefixSpice()
//	{
//		Assert.assertEquals(tableName, query.getTablePrefixSpice());
//	}
//
//	@Test
//	void testRunQuerySearchMetadataFailover1()
//	{
//		tableName = "bennu_altwgspcv20190828_mapcam2";
//		query.tablePrefixSpc = tableName;
//		query.tablePrefixSpice = tableName;
//		ImageDatabaseSearchMetadata searchMetadata = ImageDatabaseSearchMetadata.of("", new DateTime(946684800000L), new DateTime(2524608000000L),
//                Ranges.closed(0.0, 1000.0),
//                null, new ArrayList<Integer>(),
//                Ranges.closed(0.0, 180.0),
//                Ranges.closed(0.0, 180.0),
//                Ranges.closed(0.0, 180.0),
//                false, new ArrayList<Integer>(), new ArrayList<Integer>(),
//                Ranges.closed(0.0, 1000.0),
//                null, ImageSource.GASKELL, 0);
//		SearchResultsMetadata searchResultsMetadata = query.runQuery(searchMetadata);
//		System.out.println("TestGenericPhpQuery: testRunQuerySearchMetadata: metadata is " + searchResultsMetadata);
//		System.out.println("TestGenericPhpQuery: testRunQuerySearchMetadata: num results " + searchResultsMetadata.getResultlist().size());
//		assertEquals(false, searchResultsMetadata.isLoadFailed());
//		assertEquals(1722, searchResultsMetadata.getResultlist().size());
//	}
//
//	@Test
//	void testRunQuerySearchMetadataFailover2()
//	{
//		Configuration.setRootURL("http://sbmt2.jhuapl.edu");
//		ImageDatabaseSearchMetadata searchMetadata = ImageDatabaseSearchMetadata.of("", new DateTime(946684800000L), new DateTime(2524608000000L),
//                Ranges.closed(0.0, 1000.0),
//                null, new ArrayList<Integer>(),
//                Ranges.closed(0.0, 180.0),
//                Ranges.closed(0.0, 180.0),
//                Ranges.closed(0.0, 180.0),
//                false, new ArrayList<Integer>(), new ArrayList<Integer>(),
//                Ranges.closed(0.0, 1000.0),
//                null, ImageSource.GASKELL, 0);
//		SearchResultsMetadata searchResultsMetadata = query.runQuery(searchMetadata);
//		System.out.println("TestGenericPhpQuery: testRunQuerySearchMetadata: metadata is " + searchResultsMetadata);
//		System.out.println("TestGenericPhpQuery: testRunQuerySearchMetadata: num results " + searchResultsMetadata.getResultlist().size());
//		assertEquals(false, searchResultsMetadata.isLoadFailed());
//		assertEquals(1722, searchResultsMetadata.getResultlist().size());
//	}
//
//
//
////	@Test
////	void testStore()
////	{
////		fail("Not yet implemented");
////	}
////
////	@Test
////	void testRetrieve()
////	{
////		fail("Not yet implemented");
////	}
//
//	@Test
//	void testCheckForDatabaseTable()
//	{
//		//In QueryBase
//		ImageSource imageSource = ImageSource.SPICE;
//        String imagesDatabase = query.getTablePrefix(imageSource) + "images_" + imageSource.getDatabaseTableName();
//        imagesDatabase += Configuration.getDatabaseSuffix();
//        System.out.println("TestGenericPhpQuery: testCheckForDatabaseTable: image db " + imagesDatabase);
//		boolean checkForDatabaseTable = false;
//		try
//		{
//			checkForDatabaseTable = QueryBase.checkForDatabaseTable(imagesDatabase);
//		} catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Assert.assertEquals(true, checkForDatabaseTable);
//	}
//
//	@Test
//	void testGetRootPath()
//	{
//		Assert.assertEquals("/bennu/altwg-spc-v20190828/mapcam", query.getRootPath());
//	}
//}
