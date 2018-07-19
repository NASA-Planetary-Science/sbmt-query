package edu.jhuapl.sbmt.query.database;

import edu.jhuapl.sbmt.query.QueryBase;

/**
 * This class represents a database storing information about all the
 * data. It also provides functions for querying the database.
 */
public abstract class DatabaseQueryBase extends QueryBase
{
    protected String rootPath;
    protected final String galleryPath;
    protected Boolean galleryExists;

    protected DatabaseQueryBase(String galleryPath)
    {
        super(galleryPath);
        this.galleryPath = galleryPath;
        this.galleryExists = null;
        this.rootPath = null;
    }

    public String getRootPath()
    {
        return rootPath;
    }

    @Override
    public DatabaseQueryBase clone()
    {
//        try
//        {
            return (DatabaseQueryBase) super.clone();
//        }
//        catch (CloneNotSupportedException e)
//        {
//            // Can't happen.
//            throw new AssertionError(e);
//        }
    }

//    /**
//     * Run a query and return an array containing the results. The returned array
//     * is a list of list of strings where each a list in the containing list
//     * contains 2 elements. The first element is the path on the server to the
//     * FIT image file. The second is the start time of the image.
//     *
//     * @param datatype
//     * @param startDate
//     * @param stopDate
//     * @param filters
//     * @param userDefined1
//     * @param userDefined2
//     * @param startDistance
//     * @param stopDistance
//     * @param startResolution
//     * @param stopResolution
//     * @param searchString
//     * @param polygonTypes
//     * @param fromIncidence
//     * @param toIncidence
//     * @param fromEmission
//     * @param toEmission
//     * @param fromPhase
//     * @param toPhase
//     * @param cubeList
//     * @param imageSource
//     * @param limbType
//     * @return
//     */
//    public abstract List<List<String>> runQuery(
//            String type,
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
//            List<Integer> polygonTypes,
//            double fromIncidence,
//            double toIncidence,
//            double fromEmission,
//            double toEmission,
//            double fromPhase,
//            double toPhase,
//            TreeSet<Integer> cubeList,
//            ImageSource imageSource,
//            int limbType);

}
