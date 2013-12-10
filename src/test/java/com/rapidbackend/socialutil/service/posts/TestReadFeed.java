package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;
/**
 * Test read a single feed by id
 * @author chiqiu
 *
 */
public class TestReadFeed extends SocialUtilTestBase{
    
    public TestReadFeed(String followableName){
        super(followableName);
    }
    
    @Before
    public void before() throws Exception{
        cleanDb();
        
        cleanRedisInstances();
        
        initDataContainers();
        
        createUsersAndFollowables();
        
        createRelationships();
        
        logger.debug("TestReadFeed before <===============");
    }
    @Test
    public void testReadSingleFeed() throws Exception{
        
        UserBase author = users.get(0);
        
        int postNum = 150;
        postMultipleFeedByOneFollowable(author,postNum);
        
        Integer followableId = getFolloweableId(author);
        FeedContentBase feed = cachedPosts.get(followableId).get(0);
        int [] ids = new int[1];
        ids[0] = feed.getId();
        
        List<FeedContext> feeds =  readFeeds(ids);
        FeedContext feedContext = feeds.get(0);
        FeedContentBase feedRead = (FeedContentBase)feedContext.getFeed();
        assertTrue(feedRead.getId().intValue() == feedRead.getId());
    }
    //TODO
    public void testReadUserFromContext() throws Exception{
        
    }
}
