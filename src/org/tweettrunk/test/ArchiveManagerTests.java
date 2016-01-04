/**
 *
 * ArchiveManagerTests.java
 * Tests the ArchiveManager class
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
import org.tweettrunk.util.ArchiveManager;


import java.util.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.tweettrunk.util.Tweet;

@RunWith(JUnit4.class)
public class ArchiveManagerTests {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Tests the constructor of ArchiveManager class
	*@Author Kara King
	 */
	@Test
	public void archiveManagerConstructorTest()
	{

		org.junit.Assert.assertNotNull(new ArchiveManager());
	}


	/**
	 * Tests extractArchive() method
	*@Author Kara King
	 */
	@Test
	public void extractArchive(){
		ArchiveManager.extractArchive("testdata/tweets.zip","testdata/");
	}


	/** 
	*Tests exception for extractArchive() method
	 *@Author Kara King
	 */
	@Test
	public void extractArchiveExceptionTest(){
		PrintStream err = System.out;
		ByteArrayOutputStream errput = new ByteArrayOutputStream(1000);
		System.setErr(new PrintStream(errput));

		ArchiveManager.extractArchive("testdata/tweetsS.csv","testdata/");

		assertTrue(errput.toString().contains("ZipException"));
		System.setErr(err);
	}

	/**
	 *Test for parseTweetsJSON method
	*@Author Kara King
	 */
	@Test
	public void parseJSONHashTagTest(){
		ArrayList<Tweet> k = ArchiveManager.parseTweetsJSON("testdata/2013_12.js");
		ArrayList<Tweet> kk = new ArrayList<Tweet>();
		kk.add(k.get(0));
		ArrayList<String> hashTagss = new ArrayList<String>();
		hashTagss.add("TheChristmasMiracle");
		ArrayList<Double> geoLocationn = new ArrayList<Double>();
		long ff = (Long) k.get(0).getId();
		Tweet tt = new Tweet(ff, hashTagss, geoLocationn, "#TheChristmasMiracle", "2013-12-04 08:52:47");

		ArrayList<Tweet> f= new ArrayList<Tweet>();
		f.add(tt);
		assertEquals(kk.get(0).getId(),f.get(0).getId());
		assertEquals(kk.get(0).getDateTime(),f.get(0).getDateTime());
		assertEquals(kk.get(0).getText(),f.get(0).getText());
		assertEquals(kk.get(0).getHashTags(),f.get(0).getHashTags());

	}
	/**
	 *Test for parseTweetsJSON method for file not found exceptions
	*@Author Kara King
	 */
	@Test
	public void parseJSONExceptionTest(){
		PrintStream err = System.out;
		ByteArrayOutputStream errput = new ByteArrayOutputStream(1000);
		System.setErr(new PrintStream(errput));

		ArrayList<Tweet> k = ArchiveManager.parseTweetsJSON("testdata/111_19.js");

		assertTrue(errput.toString().contains("FileNotFoundException"));
		System.setErr(err);

	}
	/**
	 *Test for parseTweetsJSON method for parse exceptions
	*@Author Kara King
	 */
	@Test
	public void parseJSONException2Test(){
		PrintStream err = System.out;
		ByteArrayOutputStream errput = new ByteArrayOutputStream(1000);
		System.setErr(new PrintStream(errput));

		ArrayList<Tweet> k = ArchiveManager.parseTweetsJSON("testdata/2013_13.js");

		assertFalse(errput.toString().contains("ParseException"));
		System.setErr(err);
	}
/**
	 *Test for tweetsDirectoryPath method
	*@Author Kara King
	 */
@Test
public void tweetsDirectoryPathTest() {

String actual = ArchiveManager.tweetsDirectoryPath();

		String expected = "data/js/tweets/";


assertEquals(expected, actual);

	}
/**
	 *Test for userDetailsFilePath method
	*@Author Kara King
	 */
@Test
public void userDetailsFilePathTest() {

String actual = ArchiveManager.userDetailsFilePath();

		String expected = "data/js/user_details.js";


assertEquals(expected, actual);

	}
/**
	 *Test for parseUserDetails method
	*@Author Kara King
	 */
@Test
public void parseUserDetailsTest(){
String actual= ArchiveManager.parseUserDetails("testdata/data/js/user_details.js");
String expected= "Luvdahurricanes";
assertEquals(expected, actual);
}


/**
	 *Test for parseUserDetails File not found exception method
	*@Author Kara King
	 */
@Test
public void parseUserDetailsExTest(){
		PrintStream err = System.out;
		ByteArrayOutputStream errput = new ByteArrayOutputStream(1000);
		System.setErr(new PrintStream(errput));
		String actual = ArchiveManager.parseUserDetails("testdata/data/js/userdetails.js");
		assertTrue(errput.toString().contains("FileNotFoundException"));
		System.setErr(err);
}
/**
	 *Test for parseUserDetails File not found exception method
	*@Author Kara King
	 */
@Test
public void parseUserDetailsEx2Test(){
		PrintStream err = System.out;
		ByteArrayOutputStream errput = new ByteArrayOutputStream(1000);
		System.setErr(new PrintStream(errput));
		String actual = ArchiveManager.parseUserDetails("testdata/2013_12.js");
		assertFalse(errput.toString().contains("ParseException"));
		System.setErr(err);
}

}

