/**
 * TweetTrunkCLI.java
 *
 * This is the command line interface to TweetTrunk. 
 * This program runs in correlation with JCommander  in order to allow 
 * the user to authenticate themselves with Twitter, bring in an archive, 
 * and then run analysis tools on their tweets.    
 *
 * @author Eric Weyant
 */
package org.tweettrunk.ui;
import com.beust.jcommander.*;
import java.sql.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileReader;
import java.io.IOException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.exception.ZipException;

import org.tweettrunk.core.*;
import org.tweettrunk.util.*;


@SuppressWarnings("unused")
public class TweetTrunkCLI
{
	public static void main(String args[]) throws ZipException
	{
		//Create objects needed
		Parameters param = new Parameters();
		JCommander j = new JCommander(param, args);

		// Load the user's account authentication if it's stored.
		boolean userIsAuthenticated = AccountManager.loadStoredAuthentication();

        if(args.length == 0)
        {
            j.usage();
            return;
        }

		//check if user has been authenticated
		if (param.doAuthenticate()) {
			AccountManager.authenticateUser();
			return;
		}


		if (param.doAddArchive()) {
			if (param.getArchiveFilePath() != null) {
				//set up database and then add tweets from archive
				DBManager.reinitTable();       
				AccountManager.addTweetsFromArchive(param.getArchiveFilePath());
			}
			else {
				System.out.println("You must provide a path to the archive.");
			}

			return;
		}

		//if user tries to run analysis before authentication or adding archive
		if(!userIsAuthenticated) {
			System.out.println("You must permit TweetTrunk to access your Twitter account. Run TweetTrunk with -authenticate to proceed.");
			return;
		}

		        if(param.doDelete()) {
			System.out.println("Deleting imported Tweets and account details.");
			AccountManager.deleteAccount();
			return;
		}

		if (!AccountManager.hasImportedArchive()) {
			System.out.println("Now that you've authenticated your Twitter account, start out by importing your Twitter archive with -addArchive. For more information, visit https://blog.twitter.com/2012/your-twitter-archive.");
			return;
		}

		if(param.doReset()) {
			System.out.println("Removing imported Tweets.");
			AccountManager.resetAccount();
			return;
		}


		if(param.doFetchNewTweets()) {
			System.out.println("Fetched " + AccountManager.fetchNewTweets() + " new tweets.");
		}
		else if(AccountManager.newTweetsAvailable()) // print only if the user hasn't just fetched new tweets
		{
			System.out.println("You have new tweets available for download. Fetch them by running -fetchNewTweets.");
		}

		//analysis tools
		if (param.doAnalysis())
		{
			//switch statement to determine which tool to run
			String analysis = param.getAnalysis();
			switch(analysis)
			{
				case "match": 
					System.out.println(TweetsAnalyzer.subStringMatching(param.getStartDate(), param.getEndDate(), param.getPattern()));
					break;
				case "geolocations": 
					System.out.println(TweetsAnalyzer.geoLocationAnalysis(param.getStartDate(), param.getEndDate()));
					break;
				case "hashtagCloud": 
					TweetsAnalyzer.hashtagCloud(param.getStartDate(), param.getEndDate());
					break;
				case "dateRange": 
					System.out.println(TweetsAnalyzer.tweetsInDays(param.getStartDate(), param.getEndDate()));
					break;
				case "hourlyTweetRate": 
					System.out.println(TweetsAnalyzer.hourlyTweetRate());
					break;
				default:
					j.usage();
					break;
			}
		}


	}


}

