/**
 * ParametersTests.java
 * Tests the Parameters class
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
import org.tweettrunk.ui.Parameters;

@RunWith(JUnit4.class)
    public class ParametersTests {


    /**
     * Tests the doAuthenticate() method
     */


    @Test
    public void doAuthenticateTest(){
 	 Parameters jct = new Parameters();
        String[] argv = {"-authenticate"};
        new JCommander(jct, argv);
       Boolean actual = jct.doAuthenticate();
        Boolean expected = true;
       assertEquals(expected, actual);
    }
   /**
     * Tests the doAddArchive() method
     */
    @Test
    public void doAddArchiveTest2(){
	Parameters jct = new Parameters();
      String[] argv = {};
        new JCommander(jct, argv);
       Boolean actual2= jct.doAddArchive();
        Boolean expected = false;
       assertEquals(expected, actual2);
    }

   /**
     * Tests the doAddArchive() method
     */
    @Test
    public void doAddArchiveTest(){
	Parameters jct = new Parameters();
      String[] argv = {"-addArchive","/kingk2/"};
        new JCommander(jct, argv);
       Boolean actual2= jct.doAddArchive();
        Boolean expected = true;
       assertEquals(expected, actual2);
    }
   /**
     *Tests the getArchiveFilePath() method
     */
    @Test
    public void getArchiveFilePathTest(){
   Parameters jct = new Parameters();
        String[] argv = {"-addArchive","/kingk2/"};
        new JCommander(jct, argv);
       String actual = jct.getArchiveFilePath();
        String expected = "/kingk2/";
       assertEquals(expected, actual);
    } 

 /**
     *Tests the getAnalysis() method
     */
   
   @Test
    public void getAnalysisTest(){
           Parameters jct = new Parameters();
        String[] argv = {"-analysis","pieChart"};
        new JCommander(jct, argv);
       String actual = jct.getAnalysis();
        String expected = "pieChart";
       assertEquals(expected, actual);
    }

 /**
     *Tests the getStartDate() method
     */
 
     @Test
    public void getStartDateTest(){
        Parameters jct = new Parameters();
        String[] argv = {"-startDate","2013-12-12 00:00:00"};
        new JCommander(jct, argv);
       String actual = jct.getStartDate();
        String expected = "2013-12-12 00:00:00";
       assertEquals(expected, actual);
    }

 /**
     *Tests the getEndDate() method
     */
   
    @Test
    public void getEndDateTest(){
     Parameters jct = new Parameters();
        String[] argv = {"-endDate","2013-12-12 00:00:00"};
        new JCommander(jct, argv);
       String actual = jct.getEndDate();
        String expected = "2013-12-12 00:00:00";
       assertEquals(expected, actual);

    }	
 /**
     *Tests the getPattern() method
     */
  @Test
    public void getPatternTest(){


        Parameters jct = new Parameters();
        String[] argv = {"-pattern","a"};
        new JCommander(jct, argv);
       String actual = jct.getPattern();
        String expected = "a";
       assertEquals(expected, actual);
    } 

/**
     *Tests the doFetchNewTweets() method
     */
    @Test
    public void doFetchNewTweetsTest() {
       Parameters jct = new Parameters();
        String[] argv = {"-fetchNewTweets"};
        new JCommander(jct, argv);
       Boolean actual = jct.doFetchNewTweets();
        Boolean expected = true;
       assertEquals(expected, actual);
    }	
    }

