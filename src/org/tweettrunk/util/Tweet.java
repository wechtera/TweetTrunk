/**
 * Tweet.java
 *
 *Class for creating a Tweet
 *
 * @author Adam Wechter
 */

package org.tweettrunk.util;
import java.util.ArrayList;
import java.util.*;
public class Tweet {
	private long id;  //tweet id number
	private ArrayList<String> hashTags;  //hashtags
	private ArrayList<Double> geoLocation;  //gographical locations
	private String text;  //actual tweet
	private String dateTime;

	/**
	 *Parameter Constructor for Tweet Class 
	 * author: Adam Wechter
	 *
	 */
	public Tweet(long id, ArrayList<String> hashTags, ArrayList<Double> geoLocation, String text, String dateTime) {
		this.id = id;
		if (hashTags != null)
			this.hashTags = new ArrayList<String>(hashTags);
		if (geoLocation != null)
			this.geoLocation = new ArrayList<Double>(geoLocation);
		this.text = text;
		this.dateTime =	dateTime; 
	}
	/**
	 * No parameter Constructor for Tweet Class
	 *
	 * author: Adam Wechter
	 */
	public Tweet() {
		new Tweet(0, null, null, "", "");
	}
	/**
	 * Sets the user id in the tweet object
	 *
	 * author: Adam Wechter
	 *
	 */
	public void setId(long id) { this.id = id; }
	/**
	 * Sets the hashTag arraylist in the tweet object
	 *
	 * author: Adam Wechter
	 *
	 */
	public void setHashTags(ArrayList<String> hashTags) {
		this.hashTags = hashTags; 
	} 
	/**
	 *Sets the geoLocation arraylist in the tweet object
	 *
	 * author: Adam Wechter
	 *
	 */
	public void setGeoLocation(ArrayList<Double> geoLocation) {
		this.geoLocation = geoLocation; 
	} 
	/**
	 *Sets the geoLocation in the tweet object
	 *
	 * author: Adam Wechter
	 *
	 */
	public void setGeoLocation(String geoLocation) { 
		if (geoLocation == null)
			return;
		if(geoLocation.equals("")) 
			this.geoLocation = null; 
		else { 
			double firstCoord; 
			double secondCoord; 
			int length=0; 
			for(int i=0; i<geoLocation.length(); i++) {
				if(geoLocation.charAt(i) == ',') {
					length = i; break;
				}
			}
			String[] coords = geoLocation.split(",");
			if (coords.length == 2) {
				this.geoLocation = new ArrayList<Double>(2);
				this.geoLocation.add(new Double(coords[0]));
				this.geoLocation.add(new Double(coords[1]));
			}
		}
	}
	/**
	 * Sets the text (tweet) in the tweet object
	 *
	 * author: Adam Wechter
	 *
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * Sets the data and time in the tweet object
	 *
	 * author: Adam Wechter
	 * 
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	/**
	 *Fetchs the id of the tweet object
	 *
	 * author: Adam Wechter
	 *
	 * @return id of type long of the tweet
	 */
	public long getId() {
		return id;
	}
	/**
	 * Fetchs the hashTags of the tweet object
	 *
	 * author: Adam Wechter
	 *
	 * @return ArrayList of String that represent all the hashTags one tweeted
	 */
	public ArrayList<String> getHashTags() {
		return hashTags;
	}
	/**
	 * Fetchs the GeoLocation of the tweet object
	 *
	 * author: Adam Wechter
	 *
	 * @return ArrayList of Double that represent all the GeoLocations one has tweeted
	 */
	public ArrayList<Double> getGeoLocation() {
		return geoLocation;
	}
	/**
	 * Fetchs the text(tweet) of the tweet object
	 *
	 * author: Adam Wechter
	 *
	 * @return String representation of a tweet
	 */
	public String getText() {
		return text;
	}
	/**
	 * Fetchs the date and time if the tweet object
	 *
	 * author: Adam Wechter
	 *
	 * @return String representation of the data and time of a tweet
	 */
	public String getDateTime() {
		return dateTime;
	}
	/**
	 * Fetch the String version of the GeoLocation of the tweet object
	 *
	 * author: Adam Wechter
	 *
	 * @return String representation of the GeoLocation
	 */
	public String getStringifiedGeo() {
		String geoString = "";
		if(geoLocation == null || geoLocation.isEmpty())
			return geoString;
		geoString += Double.toString(geoLocation.get(0));
		geoString += ",";  //comma separated string
		geoString += Double.toString(geoLocation.get(1));
		return geoString;
	}
	/**
	* Checks to see if the tweet has a GeoLocation
	 * author: Adam Wechter
	 *
	 * @return Boolean of whether the tweet object has a GeoLocation
	 */
	public boolean hasGeo() {
		if (geoLocation == null)
			return false;
		return !geoLocation.isEmpty();
	}
	/**
	 * Converts a tweet object into a String representation
	 *
	 * author: Adam Wechter
	 *
	 * @return String representation of a tweet object
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("ID: " + id + "\n");
		strBuilder.append("Hashtags: " + hashTags + "\n");
		strBuilder.append("Geolocation: " + geoLocation + "\n");
		strBuilder.append("Date/Time: " + dateTime + "\n");
		strBuilder.append("Status: " + text + "\n");

		return strBuilder.toString();
	}
}
