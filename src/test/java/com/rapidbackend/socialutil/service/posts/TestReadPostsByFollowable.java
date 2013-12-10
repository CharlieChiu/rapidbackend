package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

/**
 * Test read posts by one 'Followable'
 * @author chiqiu
 *
 */
public class TestReadPostsByFollowable extends SocialUtilTestBase{
    static Logger logger = LoggerFactory.getLogger(TestReadPostsByFollowable.class);
    
    public TestReadPostsByFollowable(String followableName){
        super(followableName);
    }
    
    @Before
    public void before() throws Exception{
        try {
            cleanDb();
            cleanRedisInstances();
            initDataContainers();
            
            createUsersAndFollowables();
            
            createRelationships();
        } catch (Exception e) {
            logger.error("hit exception in before()",e);
            throw e;
        }
        
    }
    @AfterClass
    public static void afterClass() throws Exception{
        try {
            cleanDb();
            cleanRedisInstances();
        } catch (Exception e) {
            logger.error("hit exception in "+TestReadPostsByFollowable.class+" afterClass()",e);
            throw e;
        }
        
    }
    /**
     * test read posts by one 'followable'
     * @throws Exception
     */
    @Test
    public void testReadPostsByFollowable() throws Exception{
        UserBase user = users.get(0);
        Integer followableId = getFolloweableId(user);
        int numOfPosts = 100;
        postMultipleFeedByOneFollowable(user, numOfPosts);
        List<FeedContentBase> feeds = cachedPosts.get(followableId);
        int pageSize = 35;
        int start = 0;
        CommandResult<?> commandresult=readFeedsByFollowables(followableId, start, pageSize);
        List<FeedContext> results =  (List<FeedContext>)commandresult.getResult();
        
        assertTrue(results.size()==pageSize);
        int i =1;
        for(FeedContext feedContext : results){
            FeedContentBase feed = feedContext.feed();
            assertTrue(feed.getId().intValue() == feeds.get(numOfPosts - i).getId().intValue());
            i++;
        }
        
    }
    @Test
    public void testReadPostsByFollowablWithPaging() throws Exception{
        UserBase user = users.get(0);
        Integer numOfPosts = 200;
        postMultipleFeedByOneFollowable(user, numOfPosts);
        Integer followableId = getFolloweableId(user);
        
        Integer pageSize = 10;
        Integer pageNum = 1;
        Integer start = 0;
        
        while(pageNum <= numOfPosts/pageSize){
            CommandResult<?> commandresult=readFeedsByFollowables(followableId, start, pageSize);
            
            assertEquals(commandresult.getPageSize(), pageSize);
            assertEquals(commandresult.getNextStart().intValue(), numOfPosts-pageNum*pageSize+1);
            start = commandresult.getNextStart();
            pageNum++;
        }
        CommandResult<?> commandresult=readFeedsByFollowables(followableId, start, pageSize);
        assertEquals(commandresult.getPageSize().intValue(), 0);
        assertEquals(commandresult.getNextStart().intValue(), -1);
    }
    
    @Test
    public void testReadPostsByFollowableWithoutReturnValue() throws Exception{
        UserBase user = users.get(0);
        UserBase user1 = users.get(1);
        Integer followableId = getFolloweableId(user1);
        int numOfPosts = 100;
        postMultipleFeedByOneFollowable(user, numOfPosts);
        postMultipleFeedByOneFollowable(user1,numOfPosts);
        
        List<FeedContentBase> feeds = cachedPosts.get(followableId);
        int pageSize = 35;
        int start = 0;
        CommandResult<?> commandresult=readFeedsByFollowables(followableId, numOfPosts-1, pageSize);
        List<FeedContext> results =  (List<FeedContext>)commandresult.getResult();
        
        assertTrue(results == null);
        assertTrue(commandresult.getNextStart().intValue() == -1);
        assertTrue(commandresult.getPageSize().intValue() == 0);
    }
    
    
}
