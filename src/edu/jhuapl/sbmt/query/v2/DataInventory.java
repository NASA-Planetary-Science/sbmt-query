package edu.jhuapl.sbmt.query.v2;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.collect.Lists;

import edu.jhuapl.saavtk.util.FileUtil;
import edu.jhuapl.sbmt.query.v2.QueryException.QueryExceptionReason;
import edu.jhuapl.sbmt.query.v2.QueryException.Severity;

/**
 * @author steelrj1
 *
 */
public class DataInventory
{
	/**
	 * Map that holds the list of files in the inventory, keyed by the first entry in the file
	 */
	private SortedMap<String, List<String>> inventory;

	/**
	 * Constructor.
	 * 
	 * Initializes the inventory tree map.
	 */
	public DataInventory()
	{
		inventory = new TreeMap<>();
	}
	
    /**
     * Return the current content of the data inventory file. Note this inventory
     * should be a superset of the data files that are locally cached.
     * 
     * Example Usage:
     * 		String dataInventoryFile = SafeURLPaths.instance().getString(Configuration.getCacheDir(), "dataInventory.txt");
     * 		SortedMap<String, List<String>> inventory = getDataInventory(dataInventoryFile);
     * 
     * 
     * @return the data inventory map
     */
    protected SortedMap<String, List<String>> getDataInventory(String inventoryFileName) throws QueryException
    {
        if (inventoryFileName != null)
        {
            try
            {
                List<String> lines = FileUtil.getFileLinesAsStringList(inventoryFileName);
                for (String line: lines)
                {
                    String[] values = line.trim().split("\\s+");
                    inventory.put(values[0], Lists.newArrayList(values));
                }
            }
            catch (IOException e)
            {
            	throw new QueryException("Problem reading previous cache inventory", Severity.ERROR, 
            			QueryExceptionReason.CACHE_INVENTORY_ERROR, e);
                // Ignore any problems reading any previous inventories.
                // e.printStackTrace();
            }
        }

        return inventory;
    }
    
    /**
     * Add the supplied search results to the data inventory for this small body configuration/instrument.
     * New results (dates) supersede previous results for the same image file.
     * Following a call to this method, the image inventory file will thus contain a union of all the search
     * results ever made. Note that this inventory includes all files that were found in a search, whether
     * or not those files have every actually been displayed and cached.
     * 
     * Example Usage:
     * 		String dataInventoryFile = SafeURLPaths.instance().getString(Configuration.getCacheDir(), "dataInventory.txt");
     * 		List<String> testEntry = new ArrayList<String>();
	 *		testEntry.add("This is a test");
	 *		testEntry.add("And another one");
	 *		testArray.add(testEntry);
	 *		dataInventory.updateDataInventory(testArray, dataInventoryFile);
     * 
     * @param newResults the results to add
     */
    void updateDataInventory(List<List<String>> newResults, String inventoryFileName) throws QueryException
    {
    	if (inventoryFileName == null) return;
    	
        // Add the new results, overwriting any that were previously cached; always assume newer is "better".
        for (List<String> each: newResults)
        {
            inventory.put(each.get(0), each);
        }

        // Write the new inventory file.
        PrintWriter writer = null;
        File newInventoryFile = null;
        try
        {
            // Write as a temporary file first, then rename to keep things clean/atomic.
            String prefix = inventoryFileName.substring(inventoryFileName.lastIndexOf(File.separator) + File.separator.length(), inventoryFileName.lastIndexOf('.'));
            String suffix = inventoryFileName.substring(inventoryFileName.lastIndexOf('.'));
            File directory = new File(inventoryFileName.substring(0, inventoryFileName.lastIndexOf(File.separator)));
            if (!directory.exists())
                directory.mkdirs();
            newInventoryFile = File.createTempFile(prefix, suffix, directory);
            writer = new PrintWriter(newInventoryFile, "UTF-8");
            for (Entry<String, List<String>> each: inventory.entrySet())
            {
                writer.println(String.join(" ", each.getValue()));
            }
        }
        catch (IOException e)
        {
        	throw new QueryException("Problem writing the inventory file", Severity.ERROR, 
        			QueryExceptionReason.CACHE_INVENTORY_ERROR, e);
            // Ignore any problems writing this file.
            // e.printStackTrace();
        }
        finally
        {
            if (writer != null)
                writer.close();
            if (newInventoryFile != null)
            {
                newInventoryFile.renameTo(new File(inventoryFileName));
            }
        }
    }
}