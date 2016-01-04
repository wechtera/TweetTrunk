/**
 * AccountManager.java
 *
 * This class provides the core account-related
 * functionality for TweetTrunk, including authentication
 * management, archive importing, and resetting the 
 * stored account information.
 *
 * @author Nathaniel Blake
 */
package org.tweettrunk.core;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.ResponseList;
import twitter4j.Paging;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.conf.Configuration;
import twitter4j.User;
import twitter4j.HashtagEntity;
import twitter4j.GeoLocation;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.StringBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import org.tweettrunk.util.Tweet;
import org.tweettrunk.core.TweetsAnalyzer;
import org.tweettrunk.util.ArchiveManager;
import org.tweettrunk.util.DBManager;


public class AccountManager {
	/** Hold on to a single authenticated Twitter instance. */
	private static Twitter authTwitter;

	/**
	 * Get the .tweettrunk directory in the user's home directory.
	 *
	 * @return The full path to the .tweettrunk directory.
	 */
	public static String tweetTrunkDirectory() {
		return System.getProperty("user.home") + "/.tweettrunk/";
	}

	/**
	 * Get the location of the stored access tokens file 
	 * (used to retrieve authentication details).
	 * 
	 * @return The location of the stored access tokens file.
	 */
	public static String accessTokensFileName() {
		return tweetTrunkDirectory() + "t4jAccessTokens";
	}

	/**
	 * Get the location of the archive-imported marker file,
	 * which indicates whether the user has yet imported 
	 * at least one archive of Tweets.
	 *
	 * @return The location of the archive-imported marker file.
	 */
	private static String archiveImportedMarkerFile() {
		return tweetTrunkDirectory() + ".archive-imported";
	}

	/**
	 * Create the .tweettrunk directory in the user's home directory.
	 */
	public static void createTweetTrunkDirectory() throws IOException {
		File homeDir = new File(System.getProperty("user.home"));
		File ttDir = new File(homeDir, ".tweettrunk");
		if (!ttDir.exists() && !ttDir.mkdir()) {
			throw new IOException("Error creating directory " + ttDir.getAbsolutePath());
		}
	}

	/**
	 * Build a configuration for TwitterFactory. This includes
	 * stored access tokens if they're available; otherwise,
	 * it's a default configuration containing only consumer keys.
	 *
	 * @return A Configuration for use with TwitterFactory.
	 */
	private static Configuration buildTwitterConfiguration() {
		ConfigurationBuilder cb = new ConfigurationBuilder();

		// Set the consumer keys for TweetTrunk-java
		cb.setDebugEnabled(false)
			.setOAuthConsumerKey("Wj8P0kKMCC6dnXbcep3Cg")
			.setOAuthConsumerSecret("A8eW7JDO1F3IrIhnbkmrrGHGtcusXSJaKR2jci748");

		// Check whether the authentication details exist already.
		File accessTokensFile = new File(accessTokensFileName());
		if (accessTokensFile.exists()) {
			try {
				// If so, load in the access token and secret
				Scanner scan = new Scanner(accessTokensFile);
				if (scan.hasNextLine())
					cb.setOAuthAccessToken(scan.nextLine());
				if (scan.hasNextLine())
					cb.setOAuthAccessTokenSecret(scan.nextLine());
				scan.close();
			} catch (FileNotFoundException e) {
				// This shouldn't be thrown since we're checking file existence first, but...
				e.printStackTrace();
			}
		}

		return cb.build();
	}

	public static boolean loadStoredAuthentication() {
		// If we already have an authenticated Twitter instance,
		// there's no work to do.
		if (userIsAuthenticated())
			return true;

		// Initialize a Twitter instance.
		Configuration conf = buildTwitterConfiguration();
		TwitterFactory tf = new TwitterFactory(conf);
		Twitter twitter = tf.getInstance();
		
		// If the authentication tokens are set, check that they're valid.
		if (twitter.getAuthorization().isEnabled()) {
			try {
				twitter.verifyCredentials();
				authTwitter = twitter;
				return true;
			} catch (TwitterException e) {
				// Status code 401 means invalid credentials; in that case,
				// silently move on in the code. Otherwise, print the error.
				if (e.getStatusCode() != 401)
					e.printStackTrace();
				return false; // authentication details aren't correct
			}
		}
		else { // We don't have stored keys; return false.
			return false;
		}
	}

	/**
	 * Get authentication for a Twitter user and store the 
	 * keys in the ~/.tweettrunk directory.
	 *
	 * Based on the TwitterCreateProperties class from an
	 * old CS380 lab.
	 *
	 * author: Nathaniel Blake
	 * author: Kara King
	 */
	public static void authenticateUser() {
		// If we already have an authenticated Twitter instance,
		// there's no work to do.
		if (userIsAuthenticated())
			return;

		// Initialize a Twitter instance.
		Configuration conf = buildTwitterConfiguration();
		TwitterFactory tf = new TwitterFactory(conf);
		Twitter twitter = tf.getInstance();

		try {
			// If the authentication tokens are set, check that they're valid.
			if (twitter.getAuthorization().isEnabled()) {
				try {
					twitter.verifyCredentials();
					authTwitter = twitter;
					return;
				} catch (TwitterException e) {
					// Status code 401 means invalid credentials; in that case,
					// silently move on in the code. Otherwise, print the error.
					if (e.getStatusCode() != 401)
						e.printStackTrace();
				}
			}

			// In the case that the tokens aren't set, or if they're set but invalid,
			// continue through the authentication process.
			// First, get the request token.
			RequestToken requestToken = twitter.getOAuthRequestToken(); 
			AccessToken accessToken = null;

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			while (accessToken == null) {
				// Open the Twitter authentication prompt in the default web browser (if possible);
				// otherwise, provide the URL for the user at the command line.
				if(Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("Open the following URL and grant access to your account:");
					System.out.println(requestToken.getAuthorizationURL());
				}

				// Get the authentication PIN from the user. Start with a prompt.
				System.out.print("\nEnter the PIN and hit enter after you granted access. [PIN]:");

				// Suppress output as the browser opens.
				PrintStream out = System.out;
				ByteArrayOutputStream output = new ByteArrayOutputStream(1000);
				System.setOut(new PrintStream(output));

				// Read in the PIN
				String pin = br.readLine();

				// After we've gotten the PIN from the user, it should be okay to stop
				// suppressing output (the browser will have opened).
				System.setOut(out);

				// Attempt to get the access tokens with this PIN.
				try {
					if (pin.length() > 0) {
						accessToken = twitter.getOAuthAccessToken(requestToken, pin);
					} else {
						accessToken = twitter.getOAuthAccessToken(requestToken);
					}
				} catch (TwitterException te) {
					if (401 == te.getStatusCode()) {
						System.out.println("Unable to get the access token.");
					} else {
						te.printStackTrace();
					}
				}
			}

			// We've received authentication; hold onto this Twitter instance.
			authTwitter = twitter;

			// Write the access tokens to a file.
			try {
				BufferedWriter out = 
					new BufferedWriter(new FileWriter(accessTokensFileName()));
				out.write(accessToken.getToken() + "\n");
				out.write(accessToken.getTokenSecret());
				out.close();
			} catch (IOException e) {
				System.out.println("Error storing the authentication details.");
				e.printStackTrace();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the authenticated user's screen name.
	 *
	 * @return The authenticated user's screen name.
	 */
	public static String authenticatedScreenName() {
		// First, authenticate the user if necessary.
		// TODO: We shouldn't initiate authentication here; throw an exception, maybe?
		if (!userIsAuthenticated())
			authenticateUser();

		try {
			return authTwitter.getScreenName();
		} catch (TwitterException e) {
			System.out.println("Error getting the authenticated user's screen name.");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Import Tweets from a Twitter archive file and store them in the database.
	 *
	 * @param zipFilePath The path to the Twitter archive.
	 */
	public static void addTweetsFromArchive(String zipFilePath) {
		// First, authenticate the user if necessary.
		// TODO: We shouldn't initiate authentication here; throw an exception, maybe?
		if (!userIsAuthenticated())
			authenticateUser();

		// Unzip the archive to a temporary directory.
		String unzipLoc = tweetTrunkDirectory() + ".temp/";
		String[] tweetFilePaths = ArchiveManager.extractArchive(zipFilePath, unzipLoc);

		// Check that the user in data/js/user_details.js matches
		// the authenticated user. If not, exit.
		try {
			String authenticatedScreenName = authTwitter.getScreenName();
			String archiveScreenName = 
				ArchiveManager.parseUserDetails(unzipLoc + ArchiveManager.userDetailsFilePath());
			if (!archiveScreenName.equals(authenticatedScreenName)) {
				System.out.println("The screen name (" + archiveScreenName + ")"
						+ " associated with this archive does not match"
						+ " the authenticated user (" + authenticatedScreenName + ").");
				return;
			}
		} catch (TwitterException e) { // We shouldn't get here since authTwitter is valid.
			System.out.println("Problem connecting to Twitter.");
			e.printStackTrace();
		}

		// Iterate through the list of Tweet JSON files and parse each file,
		// adding all Tweets to one list.
		ArrayList<Tweet> parsedTweets = new ArrayList<Tweet>();
		for (String filePath : tweetFilePaths)
			parsedTweets.addAll(ArchiveManager.parseTweetsJSON(filePath));

		// Add the Tweets in this list to the DB (DBManager will
		// handle preventing the insertion of duplicates).
		DBManager.addTweets(parsedTweets);

		// Remove the extraction directory.
		recursiveDelete(new File(unzipLoc));

		// Place a marker that an archive has been imported.
		try {
			new File(archiveImportedMarkerFile()).createNewFile();
		} catch (IOException e) {
			System.out.println("Error creating imported-archive marker.");
			e.printStackTrace();
		}
	}

	/**
	 * Method to recursively delete a directory or file.
	 * Used to remove the archive extraction directory.
	 *
	 * Based on http://stackoverflow.com/a/779529.
	 *
	 * @return The success of the deletion.
	 */ private static void recursiveDelete(File file) {
		 if (file.isDirectory()) {
			 for (File child : file.listFiles())
				 recursiveDelete(child);
		 }

		 file.delete();
	 }

	 /**
	  * Check whether there are new Tweets on the Twitter server that
	  * aren't already in the database.
	  *
	  * @return Whether there are new Tweets online that aren't in the database.
	  */
	 public static boolean newTweetsAvailable() {
		 // First, authenticate the user if necessary.
		 // TODO: We shouldn't initiate authentication here; throw an exception, maybe?
		 if (!userIsAuthenticated())
			 authenticateUser();

		 // Get the newest entry in the database.
		 long sinceId = DBManager.fetchNewestTweet().getId();

		 if (sinceId == 0) // There are no Tweets in the DB.
		 	 return true;

		 try {
			 // Check Twitter for newer tweets.
			 Paging onlyNewTweets = new Paging(sinceId); // get Tweets newer than newest in DB
			 ResponseList<Status> tweets = authTwitter.getUserTimeline(onlyNewTweets);

			 return (tweets.size() > 0);
		 } catch (TwitterException e) {
			 e.printStackTrace();
		 }

		 // Return the result.
		 return false;
	 }

	 /**
	  * Fetch Tweets from Twitter that are newer than those in the database
	  * and add them to the database.
	  *
	  * @return The number of new Tweets that were fetched.
	  */
	 public static int fetchNewTweets() {
		 // First, authenticate the user if necessary.
		 // TODO: We shouldn't initiate authentication here; throw an exception, maybe?
		 if (!userIsAuthenticated())
			 authenticateUser();

		 // Get the newest entry in the database.
		 long sinceId = DBManager.fetchNewestTweet().getId();

		 // TODO: throw an exception?
		 if (sinceId == 0) // the DB is empty
		 	 return 0;

		 try {
			 // Check Twitter for newer tweets.
			 Paging onlyNewTweets = new Paging(sinceId); // get Tweets newer than newest in DB
			 ResponseList<Status> response = authTwitter.getUserTimeline(onlyNewTweets);

			 // Convert these to Tweet objects.
			 // TODO: make conversion from T4J ResultSet<Status> to ArrayList<Tweet> a method.
			 ArrayList<Tweet> tweets = new ArrayList<Tweet>();
			 for (Status status : response) {
				 // Get the ID.
				 long id = status.getId();

				 // Extract the hashtags.
				 HashtagEntity[] hashtagEntities = status.getHashtagEntities();
				 ArrayList<String> hashtags = new ArrayList<String>();
				 for (HashtagEntity hashtagEntity : hashtagEntities)
					 hashtags.add(hashtagEntity.getText());

				 // Extract the geolocation.
				 GeoLocation geo = status.getGeoLocation();
				 ArrayList<Double> location = new ArrayList<Double>(2);
				 if (geo != null) {
					 location.add(geo.getLatitude());
					 location.add(geo.getLongitude());
				 }

				 // Get the status text.
				 String text = status.getText();

				 // Get the date/time and convert it to the standard format.
				 Date date = status.getCreatedAt();
				 String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);

				 tweets.add(new Tweet(id, hashtags, location, text, dateString));
			 }

			 DBManager.addTweets(tweets);
			 return tweets.size();
		 } catch (TwitterException e) {
			 e.printStackTrace();
		 }

		 return 0;
	 }

	 /**
	  * Check whether the user is currently authenticated on Twitter.
	  *
	  * @return Whether the user is authenticated on Twitter.
	  */
	 public static boolean userIsAuthenticated() {
		 // If we don't have an authorized Twitter instance, then no.
		 if (authTwitter == null)
			 return false;

		 // Otherwise, return whether its authentication is valid.
		 try {
			 authTwitter.verifyCredentials();
			 return true;
		 } catch (TwitterException e) { // when verifyCredentials() fails
			 // Status code 401 means invalid credentials; in that case,
			 // go ahead and return false. Otherwise, print the error first.
			 if (e.getStatusCode() != 401)
				 e.printStackTrace();

			 return false;
		 }
	 }

	 /**
	  * Check whether the user has imported at least one Twitter archive.
	  *
	  * @return Whether the user has imported at least one Twitter archive.
	  */
	 public static boolean hasImportedArchive() {
		 File marker = new File(archiveImportedMarkerFile());

		 return marker.exists();
	 }

	 /**
	  * Clears all previously imported Tweets from the database, 
	  * and removes the marker that the user has imported an archive.
	  */
	 public static void resetAccount() {
		 // Reset the database, clearing all entries and reinitializing.
		 DBManager.dropTable();
		 DBManager.reinitTable();

		 // Delete the marker that an archive has been imported, if it exists.
		 File marker = new File(archiveImportedMarkerFile());
		 marker.delete();
	 }

	 /**
	  * Removes all imported Tweets from the database and deletes the saved
	  * user authentication details.
	  */
	 public static void deleteAccount() {
		 // Empty the database and clear the imported-archive marker, as in resetAccount().
		 resetAccount();

		 // Delete the stored authentication details for this user.
		 File accessTokensFile = new File(accessTokensFileName());
		 accessTokensFile.delete();
	 }
}

