/**
 * AllTests.java
 * Test suite of all the test classes
 * 
 * @author	Kara King
 * @since	Dec 4, 2013
 */


package org.tweettrunk.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({

   
        TweetTests.class,ParametersTests.class,DBManagerTest.class,TweetsAnalyzerTests.class,ArchiveManagerTests.class,

})

public class AllTests {
}
