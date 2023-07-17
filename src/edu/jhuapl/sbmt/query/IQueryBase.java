package edu.jhuapl.sbmt.query;

import java.util.List;

import edu.jhuapl.sbmt.query.v2.ISearchMetadata;

/**
 * Interface representing queries for searching for data that match some
 * criteria in an implementation-specific way. Queries are typically in a
 * context defined by a combination of a PolyModel or other model, an
 * IImagingInstrument or other instrument, and a query method, such as a
 * database or a file-based query.
 */
public interface IQueryBase
{
    /**
     * Return the path that identifies the top level folder associated with the
     * MODEL, under which ancillary instrument data, such as file lists and
     * folders containing pointing files, would be located. Implementations
     * should not return null.
     *
     * @return the top-level model-specific ancillary data path
     */
    String getRootPath();

    /**
     * Return the path that identifies the top level folder associated with the
     * INSTRUMENT, under which primary data, such as image or spectra files are
     * located. Implementations should not return null.
     *
     * @return the top-level instrument data top path
     */
    String getDataPath();

    /**
     * Return the path that identifies a folder associated with the INSTRUMENT,
     * under which a gallery of summary data may be located. Galleries are not
     * always available. Implementations may return null to signify this.
     *
     * @return
     */
    String getGalleryPath();

    /**
     * Run a query and return an array containing the results. The returned
     * array is a list of Metadata objects that contain data specific to that
     * instrument's query request, which is also encapsulated in a metadata
     * bundle.
     *
     */
    ISearchResultsMetadata runQuery(ISearchMetadata queryMetadata);

    /**
     * This method should be rethought. Most of the arguments seem to be derivable from the results of interface methods.
     * @param pathToFileListOnServer
     * @param pathToImageFolderOnServer
     * @param pathToGalleryFolderOnServer
     * @param showFixedListPrompt
     * @return
     */
    List<List<String>> getResultsFromFileListOnServer(String pathToFileListOnServer, String pathToImageFolderOnServer, String pathToGalleryFolderOnServer, boolean showFixedListPrompt);

}