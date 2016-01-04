/**
 * DBManagerTests.java
 * Tests the Tweet class
 * @author  Adam Wechter
 * @author	Kara King
 * @since	Dec 4, 2013
 */

package org.tweettrunk.test;
import static org.junit.Assert.fail;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.tweettrunk.util.Tweet;
import org.dbunit.*;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.tweettrunk.util.DBManager;
import java.io.IOException;
@RunWith(JUnit4.class)
public class DBManagerTest  {

	/**
	 * Tests the constructor of ArchiveManager class
	 */
 	public static Tweet makeTestTweet() {
 		
		ArrayList<Double> locations = new ArrayList<Double>();
		locations.add(45.256);
		locations.add(31.2198);
		ArrayList<String> hashtags = new ArrayList<String>();
		hashtags.add("YOLO");
		hashtags.add("SWAG");
		Tweet tweet = new Tweet(1228251902, hashtags, locations, "Hello World #YOLO#SWAG", "2103-03-14 16:51:20");
		return tweet;
		
	}
	/**
	 * Tests the constructor of ArchiveManager class
	 */
	@Test
	public void dBManagerConstructorTest() {
		DBManager dbmanager = new DBManager();
		assertNotNull(dbmanager);
	}


	/**
	 * Tests the constructor of ArchiveManager class
	 */
	@Test 
	public void grabAllTest() {
		DBManager.reinitTable();
		Tweet tweet = makeTestTweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);  //expects the only tweet in table
		
		DBManager.addTweets(expected);
		ArrayList<Tweet> actual = DBManager.grabAll();
		assertEquals(expected.get(0).getId(), actual.get(0).getId());
		DBManager.dropTable();
	} 
	/**
	 * Tests the constructor of ArchiveManager class
	 */
 	@Test 
	public void queryTest() { 
		DBManager.reinitTable();
		Tweet tweet = makeTestTweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);  //expects the only tweet in table
		
		DBManager.addTweets(expected);
		ArrayList<Tweet> actual = DBManager.fetchTweets("0000-01-01 00:00:00", "3000-01-01 00:00:00", "Hello");
		assertEquals(expected.get(0).getId(), actual.get(0).getId());
		DBManager.dropTable();
  	} 	
	/**
	 * Tests the constructor of ArchiveManager class
	 */
	@Test
	public void dropTableTest() {
		DBManager.reinitTable();
		boolean actual = false;
		actual= DBManager.dropTable();
		assertTrue(actual);
	}
	/**
	 * Tests the constructor of ArchiveManager class
	 */
	@Test
	public void fetchNewestTweetTest() {
		DBManager.reinitTable();
		Tweet tweet = makeTestTweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);  //expects the only tweet in table
		Tweet expected1 = tweet;
		DBManager.addTweets(expected);
		Tweet actual= DBManager.fetchNewestTweet();
		assertEquals(expected1.getId(), actual.getId());
		assertEquals(expected1.getHashTags(), actual.getHashTags());
		assertEquals(expected1.getText(), actual.getText());
		assertEquals(expected1.getGeoLocation(), actual.getGeoLocation());
		assertEquals(expected1.getDateTime(), actual.getDateTime());
		DBManager.dropTable();
	}
	
	/**
	 * Test if Branch in addTweets
	 * FIXME still not catching right
	 */
	@Test
	public void addTweetsGeoBranch() {   //STILL NOT CATCHING THE BRANCH IT SHOULD BE SFOR SOME REASON 
		DBManager.reinitTable();
		Tweet tweet = new Tweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);
		DBManager.addTweets(expected);
		ArrayList<Tweet> actual = DBManager.grabAll();
		assertNull(actual.get(0).getGeoLocation());
		DBManager.dropTable();
	}

}
