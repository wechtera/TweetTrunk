/**
 * TweetsAnalyzer.java
 *
 * Provides the core analysis functionality of
 * TweetTrunk in the form of a variety of analytics
 * that work with various subsets of a user's Tweets.
 *
 * @author Adam Wechter
 * @author Nathaniel Blake
 */
package org.tweettrunk.core;

import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import org.tweettrunk.util.Tweet;
import org.tweettrunk.util.DBManager;
import org.mcavallo.opencloud.Cloud.Case;
import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;
import org.mcavallo.opencloud.formatters.HTMLFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.font.TextAttribute;
import javax.swing.JLabel;
import java.awt.Color;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class TweetsAnalyzer {
	/**
	 * This method takes strings of starting and end dates, and a search string/substring, as paramaters
	 * and queries the database for any tweets with the matching substring within the tweet text between
	 * the two specified dates.  It then returns the tweet information and prints the date the tweet was
	 * tweeted along with the text of the tweet containing the substring in the form of a String built for
	 * CLI formatting.
	 *
	 * author: Adam Wechter
	 *
	 * @param dateStart String of the start date queried between using "YYYY-MM-DD HH:MM:SS"
	 * @param dateEnd String of the end date queried between form of "YYYY-MM-DD HH:MM:SS"
	 * @param search String of the string or substring that tweets are queried if they contain
	 * @return out  String that will contain either a formatted string of tweets and ddates, or invalid arguements or no matches found
	 */
	public static String subStringMatching(String dateStart, String dateEnd, String search) {
		String out = "";
		if(!isValidParam(dateStart, dateEnd)) {
			out += "Invalid Parameter Arguments";
			return out;
		}
		ArrayList<Tweet> tweetsWithSub = DBManager.fetchTweets(dateStart, dateEnd, search);
		if(tweetsWithSub.isEmpty()) {
			out += "No Matches Found\n";
			return out;
		}
		else {  //using string builder because it saves memory space for larger number of results (less objects)
			StringBuilder sb = new StringBuilder(out);
			sb.append("Results for the phrase:  " + search +"\n"); 
			sb.append("Tweet Date:\t Tweet Text:\n");
			sb.append("-----------------------------------\n");
			for(Tweet tweet : tweetsWithSub) {
				sb.append(tweet.getDateTime()).append("\t").append(tweet.getText() + "\n");
			}
			out = sb.toString();
			return out;
		}
	}
	/**
	 * This method takes two strings of starting and end dates to query the database between.  It will
	 * then look at all of the tweets to see if the tweet has a geographic location assocaited with the
	 * tweet and if it does, it will format and return the final formatted string after all the tweets are
	 * examined.  The string is primed for CLI execution and return.
	 *
	 * author: Adam Wechter
	 * 
	 * @param dateStart String of the start date queried between using "YYYY-MM-DD HH:MM:SS"
	 * @param dateEnd String of the end date queried between form of "YYYY-MM-DD HH:MM:SS"
	 * @return out String of formatted geographic locations of all the tweets sent, otherwise it will print invalid arguments
	 */
	public static String geoLocationAnalysis(String dateStart, String dateEnd) {  //if they want to specify dates
		String out = "";
		if(!isValidParam(dateStart, dateEnd)) {
			out += "Invalid Parameter Arguements";
			return out;
		}
		ArrayList<Tweet> tweets = DBManager.fetchTweets(dateStart, dateEnd, "");
		StringBuilder sb = new StringBuilder(out);
		sb.append("Coords of Tweets between: "+ dateStart +" - "+ dateEnd+"\n");
		sb.append("--------------------------------------------------------\n");
		if(tweets.isEmpty())
			sb.append("No Tweets have geographic location, please enable in settings");
		else{
			for(Tweet tweet : tweets) {
				if(tweet.hasGeo())
					sb.append(tweet.getStringifiedGeo()+"\n");
			}
		}
		out = sb.toString();
		return out;
	}

	/**
	 * Opens a window with clickable hashtags in the form of a word cloud.
	 *
	 * author: Nathaniel Blake
	 *
	 * @param dateStart The earliest post date of Tweets to return.
	 * @param dateEnd The latest post date of Tweets to return.
	 */
	@SuppressWarnings("unchecked")
	public static void hashtagCloud(String dateStart, String dateEnd) {
		// TODO: Make a class in the UI package for displaying a Cloud generated here;
		// there shouldn't be Swing code in the core package.
		Cloud hashtagCloud = new Cloud(Cloud.Case.PRESERVE_CASE);

		ArrayList<Tweet> tweets = DBManager.fetchTweets(dateStart, dateEnd, "");

		for (Tweet tweet : tweets)
			for (String hashtag : tweet.getHashTags()) 
				hashtagCloud.addTag(hashtag, "https://twitter.com/search?q=%23" + hashtag);

		// Represent cloud in a Swing window. Based on
		// http://stackoverflow.com/a/11482625.
		JFrame frame = new JFrame("Hashtag Cloud for " + AccountManager.authenticatedScreenName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		for (Tag tag : hashtagCloud.tags()) {
			final JLabel label = new JLabel(tag.getName());
			final Tag thisTag = tag; // final to allow access within MouseListener
			label.setOpaque(false);
			label.setFont(label.getFont().deriveFont((float) tag.getWeight() * 10 + 10));
			label.setCursor(new Cursor(Cursor.HAND_CURSOR));
			label.setForeground(Color.darkGray);
			// Add click to open Twitter hashtag search functionality.
			// TODO: Move this into a non-private class in the ui package?
			label.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent event) {
					try {
						Desktop.getDesktop().browse(new URI(thisTag.getLink()));
					} catch (URISyntaxException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				public void mouseEntered(MouseEvent event) {
					// Underline the text on hover.
					JLabel label = (JLabel) event.getComponent();
					Font font = label.getFont();
					Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>)font.getAttributes();
					attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
					label.setFont(font.deriveFont(attributes));
				}
				public void mouseExited(MouseEvent event) {
					// Remove underline from text on end of hover.
					JLabel label = (JLabel) event.getComponent();
					Font font = label.getFont();
					Map attributes = font.getAttributes();
					attributes.put(TextAttribute.UNDERLINE, -1); // underline off (no constant)
					label.setFont(font.deriveFont(attributes));
				}

				public void mousePressed(MouseEvent event) {
					// Color text blue on mouse down.
					JLabel label = (JLabel) event.getComponent();
					label.setForeground(Color.orange);
				}
				public void mouseReleased(MouseEvent event) {
					// Return the text to its original color after click release.
					JLabel label = (JLabel) event.getComponent();
					label.setForeground(Color.darkGray);
				}
			});
			panel.add(label);
		}
		frame.add(panel);
		frame.setMinimumSize(new Dimension(600,400));
		frame.setVisible(true);
	}
	/**
	 * This method will get any tweets that the user has made between two dates and return them 
	 * in a formatted string for CLI input and output.  If dates are switched or any improper 
	 * arguments are entered, the formatted string will inform the user of their error.  
	 *
	 * author: Adam Wechter
	 *
	 * @param dateStart String of the start date queried between using "YYYY-MM-DD HH:MM:SS"
	 * @param dateEnd String of the end date queried between form of "YYYY-MM-DD HH:MM:SS"
	 * @return out String of the tweets formatted for CLI input/output
	 */

	public static String tweetsInDays(String dateStart, String dateEnd) {
		String out = "";
		if(!isValidParam(dateStart, dateEnd)) {
			out += "Invalid Parameter Arguements";
			return out;
		}

		ArrayList<Tweet> tweets = DBManager.fetchTweets(dateStart, dateEnd, "");

		StringBuilder sb = new StringBuilder(out);
		sb.append("The tweets between the dates of: "+dateStart+" and "+dateEnd+"\n");
		sb.append("-------------------------------------------------------------------\n");

		for(Tweet tweet : tweets) {
			sb.append(tweet.getText() + "\n");
		}
		out = sb.toString();
		return out;
	}

	/**
	 * Output a list of the number of tweets by time of day.
	 *
	 * TODO: Sort the output by most common to least common
	 * post time.
	 *
	 * author: Nathaniel Blake
	 * author: Adam Wechter
	 *
	 * @return A string output of the hours of the day and
	 * the total number of Tweets each hour.
	 */
	public static String hourlyTweetRate() {
		StringBuilder sb = new StringBuilder();
		sb.append("Time\tTweets\n");

		// Get the timezone offset for the current date.
		Calendar cal = new GregorianCalendar();
		int timeZoneOffset = (cal.get(Calendar.ZONE_OFFSET) + 
				cal.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000); // ms to hr

		// For each hour.
		for(int i = 0; i <= 23; i++) {
			StringBuilder param = new StringBuilder();
			if(i < 10) 
				param.append("0");
			param.append(i + ":00:00");
			int count = DBManager.getHourlyCount(param.toString());

			// Convert i to twelve hour format, and adjust for time zone.
			int adjustedHour = (i + timeZoneOffset) % 24;
			if (adjustedHour< 0)
				adjustedHour = adjustedHour + 24;
			int twelveHour = (adjustedHour % 12 == 0 ? 12 : adjustedHour % 12);
			sb.append(twelveHour);
			if (adjustedHour >= 12)
				sb.append(" p.m.");
			else
				sb.append(" a.m.");

			sb.append("\t" + count + "\n");
		}

		return sb.toString();
	}
	/**
	 * This method is there to check all user inputs such that they are in the 
	 * correct order before the method makes calls to the database.  It tests
	 * specifically that the start date is before the end date in arguements.
	 * Done by a quick compare and is boolean in nature.
	 *
	 * author: Adam Wechter
	 *
	 * @param dateStart the assumed starting date the user gives to be tested
	 * @param dateEnd the assumed ending date the user gives to be tested
	 * @return Returns true if the two dates are in proper order, false otherwise
	 */
	//checks to make sure that the dates are in proper order
	public static boolean isValidParam(String dateStart, String dateEnd) {
		if(dateStart.compareTo(dateEnd)<0) //start is smaller than end
			return true;
		else if(dateStart.compareTo(dateEnd)==0)//same exact time
			return true;
		else
			return false;
	}


}
