package query;
//package edu.jhuapl.sbmt.query;
//
//import static org.junit.jupiter.api.Assertions.fail;
//
//import org.junit.Assert;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import edu.jhuapl.saavtk.model.ShapeModelBody;
//import edu.jhuapl.saavtk.model.ShapeModelType;
//import edu.jhuapl.saavtk.util.Configuration;
//import edu.jhuapl.sbmt.client.SbmtMultiMissionTool;
//import edu.jhuapl.sbmt.client.SmallBodyViewConfig;
//import edu.jhuapl.sbmt.tools.Authenticator;
//
//import crucible.crust.metadata.api.Metadata;
//
//class TestQueryBase
//{
//	private static QueryBase query = null;
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
//		query = new QueryBase(config.rootDirOnServer + "/mapcam/gallery")
//		{
//
//			@Override
//			public Metadata store()
//			{
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public void retrieve(Metadata arg0)
//			{
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public ISearchResultsMetadata runQuery(SearchMetadata queryMetadata)
//			{
//				// TODO Auto-generated method stub
//				return null;
//			}
//
//			@Override
//			public String getDataPath()
//			{
//				// TODO Auto-generated method stub
//				return null;
//			}
//		};
//	}
//
//	@AfterAll
//	static void tearDownAfterClass() throws Exception
//	{
//	}
//
//	@Test
//	void testDoQuery()
//	{
//		//In QueryBase
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testCheckAuthorizedAccess()
//	{
//		//in QueryBase
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testConstructUrlArguments()
//	{
//		//in QueryBase
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetResultsFromFileListOnServerStringStringStringString()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetResultsFromFileListOnServerStringStringString()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testWildcardToPathRegex()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testInterpretTimeSubStrings()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testUpdateDataInventory()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetCachedResults()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetDataInventoryFileName()
//	{
//		//in QueryBase
////		Assert.assertEquals(SafeURLPaths.instance().getString(Configuration.getCacheDir(), "dataInventory.txt"), query.getDataInventoryFilename());
//	}
//
//	@Test
//	void testGetDataInventory()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetCachedFiles()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//	@Test
//	void testGetGalleryPath()
//	{
//		//in QueryBase
//
//		Assert.assertEquals("/bennu/altwg-spc-v20190828/mapcam/gallery", query.getGalleryPath());
//	}
//
//	@Test
//	void testChangeDataPathToFullPath()
//	{
//		//in QueryBase
//
//		fail("Not yet implemented");
//	}
//
//}
