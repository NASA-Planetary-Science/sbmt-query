package edu.jhuapl.sbmt.query.v2;

import javax.swing.JOptionPane;

/**
 * Custom exception class for dealing with queries.  Includes a Severity enum that can be used to pass information to
 * GUI so the proper type of JOptionDialog (or other popup) can be presented to the user.  
 * @author steelrj1
 *
 */
public class QueryException extends Exception
{
	/**
	 * Severity enum, describing levels of severity that can be passed to the user interface to suggest the types
	 * of alerts to show.  
	 * @author steelrj1
	 *
	 */
	public enum Severity 
	{
		ERROR("Error", JOptionPane.ERROR_MESSAGE),
		WARNING("Warning", JOptionPane.WARNING_MESSAGE);
		
		private String name;
		private int optionType;
		
		private Severity(String name, int optionType)
		{
			this.name = name;
			this.optionType = optionType;
		}

		public String getName()
		{
			return name;
		}

		public int getOptionType()
		{
			return optionType;
		}
	}
	
	public enum QueryExceptionReason
	{
		//DB Reasons
		DB_TABLE_NOT_FOUND("Database Table Not Found", "Could not determine if the specified "
				+ "database table exists on the server."),
		
		DB_CONNECTION("Problem with Database Connection", "There was a problem connecting to "
				+ "or getting data from the database connection."),
		//Fixed List Reasons
		FIXED_LIST_NOT_ACCESSIBLE("Fixed List Not Accessible", "The Fixed List for this query "
				+ "is not accessible."),
		
		FIXED_LIST_NOT_AUTHORIZED("Not Authorized to Read Fixed List", "You are not authorized "
				+ "to read the fixed list for this query."),
		
		FIXED_LIST_PARSE_ERROR("Problem Parsing Fixed List", "There was an error in parsing the "
				+ "fixed list for this query."),

		//Cached data reasons
		CACHE_FILE_LIST_ERROR("Problem Reading File Cache Listing", "There was an error in reading "
				+ "the cache's directory structure or file listing."),
		
		CACHE_INVENTORY_ERROR("Problem Reading Cache Data Inventory", "There was an error reading "
				+ " or writing the data inventory from the cache."),
		
		//Other (IOException, FileNotFoundException, etc)
		TIME_PARSING_ERROR("Problem Parsing Time String", "A time string could not be properly "
				+ "parsed or converted the necessary format.");
		
		private String name;
		private String description;
		
		private QueryExceptionReason(String name, String description)
		{
			this.name = name;
			this.description = description;
		}

		public String getName()
		{
			return name;
		}

		public String getDescription()
		{
			return description;
		}
		
	}
	
	
	private Severity severity;
	private QueryExceptionReason reason;
	
	/**
	 * Constructor
	 * @param message	Message to display in the exception
	 */
	public QueryException(String message)
	{
		super(message);
	}
	
	/**
	 * Constructor
	 * @param message	Message to display in the exception
	 * @param severity	Severity type of this QueryException
	 */
	public QueryException(String message, Severity severity, QueryExceptionReason reason)
	{
		super(message);
		this.severity = severity;
		this.reason = reason;
	}

	/**
	 * Constructor
	 * @param cause		The Throwable that caused this exception to be created
	 */
	public QueryException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Constructor
	 * @param message	Message to display in the exception
	 * @param cause		The Throwable that caused this exception to be created
	 */
	public QueryException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Constructor
	 * @param message	Message to display in the exception
	 * @param severity	Severity type of this QueryException
	 * @param cause		The Throwable that caused this exception to be created
	 */
	public QueryException(String message, Severity severity, QueryExceptionReason reason, Throwable cause)
	{
		super(message, cause);
		this.severity = severity;
		this.reason = reason;
	}
	
	/**
	 * Returns the severity
	 * @return
	 */
	public Severity getSeverity()
	{
		return severity;
	}
	
	/**
	 * Returns the reason why this exception was thrown
	 * 
	 * @return
	 */
	public QueryExceptionReason getReason()
	{
		return reason;
	}
}
