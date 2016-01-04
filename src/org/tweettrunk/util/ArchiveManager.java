/**
 * ArchiveManager.java
 *
 * Class for working with the Twitter archives, as described at
 * https://blog.twitter.com/2012/your-twitter-archive.
 *
 * @author Eric Weyant
 * @author Nathaniel Blake
 */

package org.tweettrunk.util;

import java.util.*;
import java.io.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.tweettrunk.util.Tweet;
import java.io.BufferedReader;

public class ArchiveManager 
{
	/**
	 * Get the standard location of the user details file
	 * relative to the base of the extracted archive.
	 *
	 * author: Nathaniel Blake
	 *
	 * @return A relative path to the user_details.js within
	 * the extracted Twitter archive.
	 */
	public static String userDetailsFilePath() {
		return "data/js/user_details.js";
	}

	/**
	 * Get the standard location of the user details file
	 * relative to the base of the extracted archive.
	 *
	 * author: Nathaniel Blake
	 *
	 * @return A relative path to the directory containing Tweet
	 * JSON files within the extracted Twitter archive.
	 */
	public static String tweetsDirectoryPath() {
		return "data/js/tweets/";
	}

	/**
	 * Unzips the Twitter archive to a specified directory and
	 * provides a list of all Tweet JSON files within.
	 *
	 * author: Eric Weyant
	 * author: Nathaniel Blake
	 *
	 * @param archiveFilePath Path to the Twitter archive (must be a .zip file).
	 * @param outputPath Directory to decompress files into (must end with '/').
	 * @return An array of path strings to the Tweet JSON files.
	 */
	public static String[] extractArchive(String archiveFilePath, String outputPath) {
		try {
			// Extract the archive.
			ZipFile zipFile = new ZipFile(archiveFilePath);
			zipFile.extractAll(outputPath);
			
			// Get a list of the Tweet JSON files.
			File tweetsDirectory = new File(outputPath + tweetsDirectoryPath());
			File[] tweetJSONFiles = tweetsDirectory.listFiles();

			// We first get the File references, but we want the full paths
			// as Strings; do this conversion.
			String[] tweetJSONFilePaths = new String[tweetJSONFiles.length];
			int i = 0;
			for (File tweetFile : tweetJSONFiles) {
				tweetJSONFilePaths[i] = tweetFile.getAbsolutePath();
				i++;
			}

			return tweetJSONFilePaths;
		} catch (ZipException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Parses a Tweets JSON file in the format of those in data/js/tweets in
	 * a Twitter archive.
	 *
	 * author: Eric Weyant
	 * author: Nathaniel Blake
	 *
	 * @param filePath Path to the Tweets JSON file in the extracted archive.
	 * @return An ArrayList of Tweets parsed from the JSON file.
	 */
	public static ArrayList<Tweet> parseTweetsJSON(String filePath) {
		JSONParser parser = new JSONParser();
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			reader.readLine(); // read past the first line, which has garbage data
			Object obj = parser.parse(reader);

			JSONArray jsonArray = (JSONArray) obj;

			for(int i = 0; i < jsonArray.size(); i++)
			{
				JSONObject tweetJSON = (JSONObject) jsonArray.get(i);

				// Get the ID.
				long id = (Long) tweetJSON.get("id");

				// Get the hashtags.
				JSONObject entities = (JSONObject) tweetJSON.get("entities");
				JSONArray hashtagsArray = (JSONArray) entities.get("hashtags");
				ArrayList<String> hashtags = null;
				if (hashtagsArray != null) {
					hashtags = new ArrayList<String>(hashtagsArray.size());
					for (int j = 0; j < hashtagsArray.size(); j++) {
						JSONObject hashtag = (JSONObject) hashtagsArray.get(j);
						hashtags.add((String)hashtag.get("text"));
					}
				}

				// Get the geo-location information.
				JSONObject geoObj = (JSONObject) tweetJSON.get("geo");
				ArrayList<Double> geo = new ArrayList<Double>(2);
				if (geoObj != null) {
					JSONArray coords = (JSONArray) geoObj.get("coordinates");
					if (coords != null) {
						geo.add((Double)coords.get(0)); 
						geo.add((Double)coords.get(1)); 
					}
				}

				// Get the status text.
				String text = (String) tweetJSON.get("text");

				// Get the date.
				String date = (String) tweetJSON.get("created_at");
				date = date.substring(0, date.length() - 6);

				// Create a new Tweet using the parsed data
				tweets.add(new Tweet(id, hashtags, geo, text, date));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return tweets;
	}

	/**
	 * Extract the screen_name property from the user_details.js 
	 * file as formatted in the Twitter archive.
	 *
	 * author: Nathaniel Blake
	 *
	 * @param userDetailsFilePath Path to user_details.js within the extracted
	 * Twitter archive.
	 * @return The value of the screen_name property within the provided file,
	 * or an empty string if there was a problem parsing the file.
	 */
	public static String parseUserDetails(String userDetailsFilePath) {
		JSONParser parser = new JSONParser();
		String screenName = "";

		try {
			BufferedReader reader = new BufferedReader(new FileReader(userDetailsFilePath));
			reader.skip(20); // skip "var user_details =  " at start of file; it messes up parsing
			Object obj = parser.parse(reader);

			JSONObject userDetails = (JSONObject) obj;

			screenName = (String) userDetails.get("screen_name");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return screenName;
	}
}
