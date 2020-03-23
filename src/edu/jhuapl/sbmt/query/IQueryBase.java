package edu.jhuapl.sbmt.query;

import java.util.List;

import crucible.crust.metadata.api.MetadataManager;

public interface IQueryBase extends MetadataManager
{

	String getDataPath();

	/**
	 * Run a query and return an array containing the results. The returned array
	 * is a list of Metadata objects that contain data specific to that instrument's
	 * query request, which is also encapsulated in a metadata bundle.
	 *
	 */
	ISearchResultsMetadata runQuery(SearchMetadata queryMetadata);

	String getGalleryPath();

	public IQueryBase clone();

	public String getRootPath();

	public List<List<String>> getResultsFromFileListOnServer(
            String pathToFileListOnServer,
            String pathToImageFolderOnServer,
            String pathToGalleryFolderOnServer);

	public List<List<String>> getResultsFromFileListOnServer(
            String pathToFileListOnServer,
            String pathToImageFolderOnServer,
            String pathToGalleryFolderOnServer,
            boolean showFixedListPrompt);

}