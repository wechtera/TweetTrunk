/**
 * Parameters.java
 *
 * JCommander parameters class. Specifies possible
 * command line arguments to TweetTrunkCLI.
 *
 * @author Nathaniel Blake
 */
package org.tweettrunk.ui;
import com.beust.jcommander.*;
import java.util.ArrayList;
import java.util.List;

public class Parameters 
{
    
	@Parameter(names = "-authenticate", description = "Authenticate TweetTrunk to access your Twitter account.")
	private boolean authenticate = false;

	@Parameter(names = "-addArchive", description = "Add Tweets from a Twitter zip archive in the specified location.")
	private String archiveFilePath;

    @Parameter(names = "-analysis", description = "Analyze the tweets stored in the database. Valid analyses:\n"
    		+ "\tmatch:\n\t\tReturn all Tweets matching a pattern provided by -pattern.\n"
    		+ "\tdateRange:\n\t\tReturn all Tweets between two dates (-startDate and -endDate).\n"
    		+ "\thashtagCloud:\n\t\tDisplay a word cloud of hashtags used in your Tweets.\n"
    		+ "\tgeolocations:\n\t\tDisplay an analysis of geolocations associated with your Tweets.\n"
    		+ "\thourlyTweetRate:\n\t\tDisplay information about what times you tweet most often.\n")
    private String analysis;
    
	@Parameter(names = "-startDate", description = "Provide only Tweets newer than a certain date (default: 0000-01-01).")
    private String startDate = new String("0000-01-01 00:00:00");

	@Parameter(names = "-endDate", description = "Provide only Tweets older than a certain date (default: now).")
    private String endDate  = new String("3000-01-01 00:00:00");
    
	@Parameter(names = "-pattern", description = "Provide only Tweets that match a given pattern (default: none).")
    private String pattern = new String("");

    @Parameter(names = "-fetchNewTweets", description = "Download new Tweets from online and add them to the database.")
	private boolean fetchTweets = false;

    @Parameter(names = "-resetAccount", description = "Removes any imported tweets.")
	private boolean reset = false;

	@Parameter(names = "-deleteAccount", description = "Clears database and removes authentication information.")
	private boolean delete = false;



	/**
	 * Get whether to authenticate.
	 *
	 * @return Whether to initiate authentication.
	 */
	public boolean doAuthenticate() {
		return authenticate;
	}

	
	/**
	 * Get whether to authenticate.
	 *
	 * @return Whether to initiate a refresh
	 */
	public boolean doReset() {
		return reset;
	}

    /**
	 * Get whether to authenticate.
	 *
	 * @return Whether to initiate deletion
	 */
	public boolean doDelete() {
		return delete;
	}


	/**
	 * Get whether to add Tweets from an archive.
	 *
	 * @return Whether to add Tweets from an archive.
	 */
	public boolean doAddArchive() {
		return (archiveFilePath != null);
	}

	/**
	 * Get the archive file path.
	 *
	 * @return File path of the archive file.
	 */
	public String getArchiveFilePath() {
		return archiveFilePath;
	}

	/**
	 * Get whether to do analysis.
	 *
	 * @return Whether to do analysis.
	 */
	public boolean doAnalysis() {
		return (analysis != null);
	}

	/**
	 * Get the analysis technique.
	 *
	 * @return The analysis technique to use.
	 */
	public String getAnalysis() {
		return analysis;
	}

	/**
	 * Get the start date for date range constraining.
	 *
	 * @return The start date for result Tweets.
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * Get the end date for date range constraining.
	 *
	 * @return The end date for result Tweets.
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * Get the pattern to match for substring matching.
	 *
	 * @return Pattern to match in status text.
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Return whether to fetch new Tweets from the Twitter server.
	 *
	 * @return Whether to fetch new Tweets from online.
	 */
	public boolean doFetchNewTweets() {
		return fetchTweets;
	}
}
