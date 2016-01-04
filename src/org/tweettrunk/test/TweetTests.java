/**
 * TweetTests.java
 * Tests the Tweet class
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
import org.tweettrunk.util.Tweet;

@RunWith(JUnit4.class)
public class TweetTests {

	private Tweet t; 
	private ArrayList<String> s;
	private ArrayList<Double> m;
	private ArrayList<String> hashTags;
	private ArrayList<Double> geoLocation;
	private long id = 1;
	private String text= "This is Twitter";
	private String dateTime= "10:00";
	private Random generator;

	/**
	 * Sets up variables to be used throughout this whole class
	 */
	@Before
	public void setUp(){


		hashTags = new ArrayList<String>(
				Arrays.asList("Hello", "Bye", "Bonjour"));
		geoLocation = new ArrayList<Double>(
				Arrays.asList(155423457.7, 785856785.7));

		s =new ArrayList<String>(
				Arrays.asList("Hi", "By","Bon"));
		m =new ArrayList<Double>(
				Arrays.asList(8765.9, 876.8));

	}
	/**
	 * Tests this class itself
	 */
	@Test

	public void SelfInstantiationTest() {
		org.junit.Assert.assertNotNull(new TweetTests());
	}

	/**
	 * Tests the Tweet class itself
	 */
	@Test
	public void tweetConstructorTest(){

		org.junit.Assert.assertNotNull(new Tweet());
	}
	/**
	 * Tests the setId() method of Tweet class
	 */
	@Test
	public void setIdTest (){
		generator = new Random();
		long expected;
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		for (int i = 0; i < 50; i++) 
		{
			expected = generator.nextLong();
			t.setId(expected);
			assertEquals("Fail: not equal", t.getId(), expected);
		}
	}	
	/**
	 *  Tests the setHashTags() method of Tweet class
	 */
	@Test
	public void setHashTagsTest(){
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		t.setHashTags(s);
		ArrayList<String> expectedID = new ArrayList<String>(
				Arrays.asList("Hi", "By","Bon"));
		ArrayList<String> actualID = t.getHashTags();     
		assertEquals(expectedID,actualID);
	}
	/**
	 *  Tests the null instance of setGeoLocation() method of Tweet class
	 */
	@Test
	public void setGeoLocationNullTest(){
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		String s = null;
		t.setGeoLocation(s);
		t.setGeoLocation("");
	        ArrayList<Double> expectedID = null;
		ArrayList<Double> actualID = t.getGeoLocation();     
		assertEquals(expectedID,actualID);
	}
	/**
	 *  Tests the String instance of setGeoLocation() method of Tweet class
	 */
	@Test
	public void setGeoLocationStringTest(){
		String x = "8765.9, 876.8";
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		t.setGeoLocation(x);
		ArrayList<Double> expectedID2 = new ArrayList<Double>(
				Arrays.asList(8765.9, 876.8));
		ArrayList<Double> actualID2 = t.getGeoLocation();     
		assertEquals(expectedID2,actualID2);
	}
	/**
	 *  Tests the one cordinate String setGeoLocation() method of Tweet class
	 */
	@Test
	public void setGeoLocationOneCoordinateTest(){
		String xx = "8765.9";
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		t.setGeoLocation(xx);
		ArrayList<Double> expectedID4 = new ArrayList<Double>(
				Arrays.asList(1.554234577E8, 7.858567857E8));
		ArrayList<Double> actualID4 = t.getGeoLocation();     
		assertEquals(expectedID4,actualID4);


	}
	/**
	 *  Tests the arraylist instance of the setGeoLocation() method of Tweet class
	 */
	@Test
	public void setGeoLocationArrayListTest(){
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		t.setGeoLocation(m);
		ArrayList<Double> expectedID = new ArrayList<Double>(
				Arrays.asList(8765.9, 876.8));
		ArrayList<Double> actualID = t.getGeoLocation();     
		assertEquals(expectedID,actualID);
	}
	/**
	 *  Tests the setText() method of Tweet class
	 */
	@Test
	public void setTextTest(){
		String expectedT = "New";
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		t.setText(expectedT);
		String actualT = t.getText();
		assertEquals(expectedT,actualT);

	}
	/**
	 *  Tests the setDateTime() method of Tweet class
	 */
	@Test
	public void setDateTimeTest(){
		String expectedD = "11:00";
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		t.setDateTime(expectedD);
		String actualD = t.getDateTime();
		assertEquals(expectedD,actualD);
	}
	/**
	 *  Tests the setId() method of Tweet class
	 */
	@Test
	public void getIdTest(){
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		long expectedID = 1;
		long actualID = t.getId();     
		assertEquals(expectedID,actualID);

	}
	/**
	 *  Tests the setHashTags() method of Tweet class
	 */
	@Test
	public void getHashTagsTest(){
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);

		ArrayList<String> expectedID =new ArrayList<String>(
				Arrays.asList("Hello", "Bye","Bonjour"));
		ArrayList<String> actualID = t.getHashTags();     
		assertEquals(expectedID,actualID);

	}
	/**
	 *  Tests the getGeoLocation() method of Tweet class
	 */
	@Test
	public void getGeoLocationTest(){
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);

		ArrayList<Double> expectedID = new ArrayList<Double>(
				Arrays.asList(155423457.7, 785856785.7));
		ArrayList<Double> actualID = t.getGeoLocation();     
		assertEquals(expectedID,actualID);

	}
	/**
	 *  Tests the getText() method of Tweet class
	 */
	@Test
	public void getTextTest(){
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		String expectedID = "This is Twitter";
		String actualID = t.getText();     
		assertEquals(expectedID,actualID);

	}
	/**
	 *  Tests the getDateTime() method of Tweet class
	 */
	@Test
	public void getDateTimeTest(){
		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		String expectedID = "10:00";
		String actualID = t.getDateTime();     
		assertEquals(expectedID,actualID);

	}
	/**
	 *  Tests the empty instance of the getStringifiedGeo() method of Tweet class
	 */
	@Test
	public void getStringifiedGeoEmptyTest(){
		ArrayList<Double> geo = new ArrayList<Double>();
		t = new Tweet(id, hashTags, geo, text, dateTime);
		String actualS2= t.getStringifiedGeo();
		String expectedS2 = "";
		assertEquals(expectedS2,actualS2);
	}
	/**
	 *  Tests the two coordinate instance of the getStringified() method of Tweet class
	 */
	@Test
	public void getStringifiedGeoTwoCoordTest(){

		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		//String expectedS = "155423457.7,785856785.7";
		String expectedS = "1.554234577E8,7.858567857E8";
		String actualS = t.getStringifiedGeo();     
		assertEquals(expectedS,actualS);


	}
	/**
	 * Tests the null instance of the getStringified() method of Tweet class
	 */
	@Test
	public void getStringifiedGeoNullTest(){
		ArrayList<Double> geo = null;
		String x = "";
		t = new Tweet(id, hashTags, geo, text, dateTime);
		String actualS = t.getStringifiedGeo();     
		assertEquals(x,actualS);


	}

	/**
	 *  Tests thenull instance of the hasGeo() method of Tweet class
	 */
	@Test
	public void hasGeoNullTest(){

		ArrayList<Double> geo = null;
		t = new Tweet(id, hashTags,geo,text, dateTime);
		Boolean expectedID2 = false;
		Boolean actualID2 = t.hasGeo();     
		assertEquals(expectedID2,actualID2);
	}
	/**
	 *  Tests the correct number of geolocations instance of the hasGeo() method of Tweet class
	 */
	@Test
	public void hasGeoCorrectGeoTest(){

		t = new Tweet(id, hashTags, geoLocation, text, dateTime);
		Boolean expectedID = true;
		Boolean actualID = t.hasGeo();     
		assertEquals(expectedID,actualID);

	}
	/**
	 *  Tests the empty instance of the hasGeo() method of Tweet class
	 */
	@Test
	public void hasGeoEmptyTest(){
		ArrayList<Double> expectedID4 = new ArrayList<Double>();
		t = new Tweet(id, hashTags, expectedID4, text, dateTime);
		Boolean expectedID3 = false;
		Boolean actualID3 = t.hasGeo();     
		assertEquals(expectedID3,actualID3);

	}
	/**
	 *  Tests the setHashTags() method of Tweet class
	 */
	@Test
	public void toStringTest(){
		t = new Tweet(id, hashTags,geoLocation,text, dateTime);
	
		String expectedID = "ID: 1\nHashtags: [Hello, Bye, Bonjour]\nGeolocation: [1.554234577E8, 7.858567857E8]\nDate/Time: 10:00\nStatus: This is Twitter\n";
		String actualID = t.toString();     
		assertEquals(expectedID,actualID);
	}
}

