
/**
 * DBManager.java
 *
 * This class handles all interactions with the database.
 *
 * @author Adam Wechter
 * @author Nathaniel Blake
 *
 * TODO: Cache and reuse a database connection?
 */

package org.tweettrunk.util;

import java.sql.*;
import java.util.ArrayList;
import org.tweettrunk.util.Tweet;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import org.tweettrunk.core.AccountManager;

public class DBManager {
	static Connection c = null;
	static Statement stmt = null;
	static ResultSet rs = null;
	static PreparedStatement prepstmt = null;

	/**
	 * Return the full path to the Tweets database.
	 *
	 * @return Full path to tweets.db.
	 */
	private static String tweetsDBLocation() {
		return AccountManager.tweetTrunkDirectory() + "tweets.db";
	}

	/**
	 * initializes the database and schema into the location given by AccountManager
	 * namely that of a hidden ".tweettrunk" directory off of the user's home directory
	 * with the schema of a primary ID integer, hashtag string, geolocation, in form of string,
	 * content in form of string, and a DateTime datetime.  This only happens if the database
	 * has not yet been created initialy
	 *
	 * author: Adam Wechter
	 * author: Nathaniel Blake
	 */
	public static void reinitTable() {
		try {
			AccountManager.createTweetTrunkDirectory();
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + tweetsDBLocation());
			stmt = c.createStatement();
			String sql = "Create Table IF NOT EXISTS Tweets (" +
				"ID INTEGER PRIMARY KEY,"+
				"HASHTAGS VARCHAR(180)," +
				"GEO_LOC VARCHAR(30)," +
				"CONTENT VARCHAR(140)," +
				"DATE_TIME DATETIME)";
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("-------Problems Creating Table-------");
		}
	}

	/**
	 * addTweet is the basic update or inital add of tweets to the table of tweets from
	 * an arraylist recieved from the parser of a JSON file.  If entries already exist
	 * i.e. a previously entered tweet is trying to be readded, the tweet is ignored and 
	 * the method moves on to the next one.  Boolean returns wether or not the entry worked
	 * or if errors happened within the insert.
	 *
	 * author: Adam Wechter
	 *
	 * @param tweets  An array list of parsed tweets from the JSON file 
	 * @return worked boolean variable that returns true if the insert was done correctly
	 */
	public static boolean addTweets(ArrayList<Tweet> tweets) {
		boolean worked = false;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + tweetsDBLocation());
			c.setAutoCommit(false);
			for(Tweet tweet : tweets) {
				String hashtags = "";
				if (tweet.getHashTags() != null && !tweet.getHashTags().isEmpty()) {
					for (String hashtag : tweet.getHashTags())
						hashtags += hashtag + ",";
					hashtags = hashtags.substring(0, hashtags.length()-1); // remove trailing comma
				}
				String sql = "INSERT OR IGNORE INTO Tweets (ID, HASHTAGS, CONTENT, GEO_LOC, DATE_TIME) "+  //may need to change way DATE TIME is read in
					"VALUES (?, ?, ?, ?, datetime(?));";
				prepstmt = c.prepareStatement(sql); //using prepared statement
				prepstmt.setLong(1, tweet.getId());
				prepstmt.setString(2, hashtags);
				prepstmt.setString(3, tweet.getText());
				prepstmt.setString(4, tweet.getStringifiedGeo());
				prepstmt.setString(5, tweet.getDateTime());
				prepstmt.executeUpdate();

			}
			prepstmt.close();
			c.commit();
			c.close();
			worked = true;
		} catch(Exception e) {
			System.out.println("\n\nProblem Updating Tweet Entries \n");
			e.printStackTrace();
		}
		return worked;
	}

	/**
	 * fetchTweet method takes in two dates and a search string dictated by the user input or controlling
	 * method in the the core package.  It returns an array of tweet objects with all associated objects
	 * for the method it is passing it off to to handle and choose what it wants.  The substring is searched 
	 * for as as substring and things can be before or after for find to be valid.
	 *
	 * author: Adam Wechter
	 *
	 * @param dateStart the starting date in form of string to be recast to date object, of ending of tweets
	 * @param dateEnd the ending date in form of string to be recast to date object, of ending of tweets
	 * @param searchString the specific search string you are looking for to be surrounded by "like" in query
	 * @return tweets An array list of tweets returned by the query given
	 */
	public static ArrayList<Tweet> fetchTweets(String dateStart, String dateEnd, String searchString) {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + tweetsDBLocation());
			c.setAutoCommit(false);
			String query = "SELECT * from TWEETS where CONTENT LIKE ? and DATE_TIME between datetime(?) and datetime(?);";
			prepstmt = c.prepareStatement(query);
			prepstmt.setString(1, "%" + searchString + "%");
			prepstmt.setString(2, dateStart);
			prepstmt.setString(3, dateEnd);
			rs = prepstmt.executeQuery();
			while(rs.next()){
				Tweet tweet = new Tweet();
				tweet.setId(rs.getLong("ID"));
				String hashtags = rs.getString("HASHTAGS");
				tweet.setHashTags(new ArrayList<String>(Arrays.asList(hashtags.split(","))));
				tweet.setText(rs.getString("Content"));
				tweet.setGeoLocation(rs.getString("GEO_LOC"));
				tweet.setDateTime(rs.getString("DATE_TIME"));
				tweets.add(tweet);
			}

			//check within times here
			prepstmt.close();
			rs.close();
			c.close();
		} catch(Exception e) {
			System.out.println("\n\nQuery Execution Problem\n");
			e.printStackTrace();
		}
		return tweets;
	}  

	/**
	 * Returns a list of all tweets from the database
	 * @return tweets An ArrayList of all Tweets available in the database.
	 * author: Adam Wechter
	 */      
	public static ArrayList<Tweet> grabAll() {
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		try {
			// Establish database connection.
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + tweetsDBLocation());
			c.setAutoCommit(false);
			
			// Execute a select all query.
			String query = "SELECT * from TWEETS;";
			prepstmt = c.prepareStatement(query);
			rs = prepstmt.executeQuery();

			// Create a list of Tweets from the result set.
			while(rs.next()) {
				Tweet tweet = new Tweet();
				tweet.setId(rs.getLong("ID"));
				String hashtags = rs.getString("HASHTAGS");
				tweet.setHashTags(new ArrayList<String>(Arrays.asList(hashtags.split(","))));
				tweet.setText(rs.getString("Content"));
				tweet.setGeoLocation(rs.getString("GEO_LOC"));
				tweet.setDateTime(rs.getString("DATE_TIME"));
				tweets.add(tweet);
			}

			// Close the database connection.
			prepstmt.close();
			rs.close();
			c.close();
		} catch(Exception e) {
			System.out.println("Error retrieving all Tweets from the database.");
			e.printStackTrace();
		}
		return tweets;
	}

	/**
	 * Fetches the newest Tweet that exists in the database.
	 *
	 * author: Nathaniel Blake
	 *
	 * @return The most recently posted Tweet in the database.
	 */
	public static Tweet fetchNewestTweet() {
		try {
			// Establish database connection.
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + tweetsDBLocation());
			c.setAutoCommit(false);
			stmt = c.createStatement();

			// Create and execute the query.
			String query = "select * from Tweets order by DATE_TIME desc limit 1;";
			prepstmt = c.prepareStatement(query);
			rs = prepstmt.executeQuery();

			// Generate a Tweet from the result.
			Tweet tweet = new Tweet();
			if (rs.next()) {
				tweet.setId(rs.getLong("ID"));
				String hashtags = rs.getString("HASHTAGS");
				tweet.setHashTags(new ArrayList<String>(Arrays.asList(hashtags.split(","))));
				tweet.setText(rs.getString("Content"));
				tweet.setGeoLocation(rs.getString("GEO_LOC"));
				tweet.setDateTime(rs.getString("DATE_TIME"));
			}

			// Close the database connection.
			prepstmt.close();
			rs.close();
			c.close();

			return tweet;
		} catch(Exception e) {
			System.out.println("Error fetching the newest Tweet in the database.");
			e.printStackTrace();
		}
		// If we're here, it's an error; return null.
		return null;
	}

	/**
	 * Calculate the number of Tweets (from all time)
	 * that are posted within a span of a single hour of the day.
	 *
	 * Returns -1 as an error condition.
	 *
	 * author: Adam Wechter
	 * author: Nathaniel Blake
	 *
	 * @param hour The hour of day for which to calculate the metric.
	 * Note that the calculation is from hour to hour + 1. This
	 * value must be between 0 and 23.
	 * @return The number of Tweets from all time posted within this hour.
	 */
	public static int getHourlyCount(String hour) {
		try {
			// Establish database connection.
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + tweetsDBLocation());
			c.setAutoCommit(false);

			// Prepare the database query.
			String query = "SELECT COUNT(*) AS hourlycount from TWEETS where TIME(DATE_TIME) between TIME(?) and TIME(?, ?);";
			prepstmt = c.prepareStatement(query);
			prepstmt.setString(1, hour);
			prepstmt.setString(2, hour);
			prepstmt.setString(3, "001 hours");

			// Execute the query and extract the resulting count.
			rs = prepstmt.executeQuery();
			int count = rs.getInt("hourlycount");

			// Close the database connection.
			prepstmt.close();
			rs.close();
			c.close();

			return count;
		} catch(Exception e) {
			System.out.println("Error calculating the number of Tweets within the hour " + hour + ".");
			e.printStackTrace(); 
		}

		// It's an error if we've gotten here.
		return -1;
	}
	/**
	 * Drops the table of tweets. The main use of this method is to clear between
	 * test during testing
	 *
	 * author: Adam Wechter
	 *
	 * @return droppedSucc  if dropped successfully, it will return true, otherwise there was a problem
	 */
	public static boolean dropTable() {
		boolean droppedSucc = false;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + tweetsDBLocation());
			c.setAutoCommit(true);
			stmt = c.createStatement();
			String drop = "DROP TABLE IF EXISTS Tweets";
			stmt.executeUpdate(drop);
			stmt.close();
			c.close();
			droppedSucc = true;
		} catch(Exception e) {
			System.out.println("\n\n Problems dropping table\n");
			e.printStackTrace();
		}
		return droppedSucc;
	}
}
