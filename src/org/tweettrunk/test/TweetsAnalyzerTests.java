/**
 * TweetsAnalyzerTests.java
 * Tests the TweetAnalyzer class
 * 
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
import com.beust.jcommander.*;
import org.tweettrunk.core.TweetsAnalyzer;
import org.tweettrunk.util.Tweet;
import org.tweettrunk.util.DBManager;
@RunWith(JUnit4.class)
public class TweetsAnalyzerTests {

	public static Tweet makeTestTweet() {

		ArrayList<Double> locations = new ArrayList<Double>();
		locations.add(45.256);
		locations.add(31.2198);
		ArrayList<String> hashtags = new ArrayList<String>();
		hashtags.add("YOLO");
		hashtags.add("SWAG");
		Tweet tweet = new Tweet(1228251902, hashtags, locations, "Hello World #YOLO#SWAG", "2013-03-14 16:51:20");
		return tweet;

	}


	/**
	 * Tests the TweetsAnalyzer constructor
	 */

	@Test
	public void tweetsAnalyzerConstructorTest()
	{

		org.junit.Assert.assertNotNull(new TweetsAnalyzer());
	}

	/**
	 * Tests the instance where the dates are not valid of the subStringMatching() method
	 */
	@Test
	public void subStringMatchingNotValidParamTest()
	{
		String dateStart = "12-13-04 00:00:00";
		String dateEnd= "11-13-04 00:00:00";
		String search = "boss";
		String actual = TweetsAnalyzer.subStringMatching(dateStart,dateEnd,search);
		String expected = "Invalid Parameter Arguments";
		assertEquals(expected,actual);
	}
	/**
	 * Tests the subStringMatching() method
	 */
	@Test
	public void subStringMatchingTest()
	{
		DBManager.reinitTable();
		Tweet tweet = makeTestTweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);  //expects the only tweet in table

		DBManager.addTweets(expected);
		ArrayList<Tweet> actual = DBManager.grabAll();
		String actualS = TweetsAnalyzer.subStringMatching("2003-03-14 16:51:20","2014-03-14 16:51:20","i");
		String expectedS = "No Matches Found\n";
		assertEquals(expectedS, actualS);
		DBManager.dropTable();
	}
	/**
	 * Tests the subStringMatching() method
	 */
	@Test
	public void subStringMatchingTest2()
	{
		DBManager.reinitTable();
		Tweet tweet = makeTestTweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);  //expects the only tweet in table

		DBManager.addTweets(expected);
		ArrayList<Tweet> actual = DBManager.grabAll();
		String actualS = TweetsAnalyzer.subStringMatching("2003-03-14 16:51:20","2014-03-14 16:51:20","a");
		String expectedS = "Results for the phrase:  a\nTweet Date:\t Tweet Text:\n-----------------------------------\n2013-03-14 16:51:20\tHello World #YOLO#SWAG\n";
		assertEquals(expectedS, actualS);
		DBManager.dropTable();
	}

	/**
	 * Tests the invalid instance of the geoLocationAnalysis() method
	 */

	@Test
	public void  geoLocationAnalysisInValidTest()
	{
		String dateStart = "12-13-04 00:00:00";
		String dateEnd= "11-13-04 00:00:00";
		String actual = TweetsAnalyzer.geoLocationAnalysis(dateStart,dateEnd);
		String expected = "Invalid Parameter Arguements";
		assertEquals(expected,actual);
	}

	/**
	 * Tests the geoLocationAnalysis() method
	 */

	@Test
	public void geoLocationAnalysisTest()
	{

		DBManager.reinitTable();
		Tweet tweet = makeTestTweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);  //expects the only tweet in table

		DBManager.addTweets(expected);
		ArrayList<Tweet> actual = DBManager.grabAll();
		String actualS = TweetsAnalyzer.geoLocationAnalysis("2003-03-14 16:51:20","2014-03-14 16:51:20");
		String expectedS = "Coords of Tweets between: 2003-03-14 16:51:20 - 2014-03-14 16:51:20\n--------------------------------------------------------\n45.256,31.2198\n";
		assertEquals(expectedS, actualS);
		DBManager.dropTable();

	}
	/**
	 * Tests the geoLocationAnalysis() method
	 */

	@Test
	public void geoLocationAnalysisTest2()
	{
	}



	/**
	 * Tests the invalid parameter instance of tweetsInDays() method
	 */

	@Test
	public void tweetsInDaysInValidParamTest()
	{
		String dateStart = "12-13-04 00:00:00";
		String dateEnd= "11-13-04 00:00:00";
		String search = "boss";
		String actual = TweetsAnalyzer.tweetsInDays(dateStart,dateEnd);
		String expected = "Invalid Parameter Arguements";
		assertEquals(expected,actual);
	}

	/**
	 * Tests the tweetsInDays() method
	 */

	@Test
	public void tweetsInDaysTest()
	{

		DBManager.reinitTable();
		Tweet tweet = makeTestTweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);  //expects the only tweet in table

		DBManager.addTweets(expected);
		ArrayList<Tweet> actual = DBManager.grabAll();
		String actualS = TweetsAnalyzer.tweetsInDays("2003-03-14 16:51:20","2014-03-14 16:51:20");
		String expectedS = "The tweets between the dates of: 2003-03-14 16:51:20 and 2014-03-14 16:51:20\n-------------------------------------------------------------------\nHello World #YOLO#SWAG\n";
		assertEquals(expectedS, actualS);
		DBManager.dropTable();

	}

	/**
	 * Tests the invalid instance of the isValidParam() method
	 */

	@Test
	public void isValidParamInValidTest()
	{
		String dateStart = "12-13-04 00:00:00";
		String dateEnd= "11-13-04 00:00:00";
		Boolean actual = TweetsAnalyzer.isValidParam(dateStart,dateEnd);
		Boolean expected = false;
		assertEquals(expected,actual);


	}
	/**
	 * Tests the valid instance of the isValidParam() method
	 */

	@Test
	public void isValidParamValidTest()
	{
		String dateStart = "11-13-04 00:00:00";
		String dateEnd= "12-13-04 00:00:00";
		Boolean actual = TweetsAnalyzer.isValidParam(dateStart,dateEnd);
		Boolean expected = true;
		assertEquals(expected,actual);


	}

	/**
	 * Tests the same instance of the isValidParam() method
	 */

	@Test
	public void isValidParamSameTest()
	{
		String dateStart = "11-13-04 00:00:00";
		String dateEnd= "11-13-04 00:00:00";
		Boolean actual = TweetsAnalyzer.isValidParam(dateStart,dateEnd);
		Boolean expected = true;
		assertEquals(expected,actual);


	}
	/**
	 * Tests the subStringMatching() method
	 */
	@Test
	public void hourlyTweetRateTest()
	{
		DBManager.reinitTable();
		Tweet tweet = makeTestTweet();
		ArrayList<Tweet> expected = new ArrayList<Tweet>();
		expected.add(tweet);  //expects the only tweet in table

		DBManager.addTweets(expected);
		ArrayList<Tweet> actual = DBManager.grabAll();
		String actualS = TweetsAnalyzer.hourlyTweetRate();
		String expectedS = "Time\tTweets\n7 p.m.\t0\n8 p.m.\t0\n9 p.m.\t0\n10 p.m.\t0\n11 p.m.\t0\n12 a.m.\t0\n1 a.m.\t0\n2 a.m.\t0\n3 a.m.\t0\n4 a.m.\t0\n5 a.m.\t0\n6 a.m.\t0\n7 a.m.\t0\n8 a.m.\t0\n9 a.m.\t0\n10 a.m.\t0\n11 a.m.\t1\n12 p.m.\t0\n1 p.m.\t0\n2 p.m.\t0\n3 p.m.\t0\n4 p.m.\t0\n5 p.m.\t0\n6 p.m.\t0\n";
		assertEquals(expectedS, actualS);
		DBManager.dropTable();
	}

}
