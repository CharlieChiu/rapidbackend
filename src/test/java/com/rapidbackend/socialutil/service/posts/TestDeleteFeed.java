package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

public class TestDeleteFeed extends SocialUtilTestBase{
    
    Logger logger = LoggerFactory.getLogger(getClass());
    
    public TestDeleteFeed(String followableName){
        super(followableName);
    }
    
    
    @Before
    public void before() throws Exception{
        cleanDb();
        
        cleanRedisInstances();
        
        initDataContainers();
        
        createUsersAndFollowables();
        
        
        createRelationships();
        
        logger.debug("TestDeleteFeed before <===============");
    }
    
    @Test
    public void testDeleteFeed() throws Exception{
        UserBase author = users.get(0);
        
        int postNum = 1;
        postMultipleFeedByOneFollowable(author, postNum,false);
        Integer followableId = getFolloweableId(author);
        FeedContentBase toDelete = cachedPosts.get(followableId).get(0);
        
        FeedContentBase deletedFeed = deleteFeed(author,toDelete);
        
        assertNotNull(deletedFeed);
        assertNotNull(deletedFeed.getId());
        
        List<FeedContext> readFeeds = readFeeds(new int[]{toDelete.getId()});
        logger.info(""+readFeeds.size());
        assertTrue(readFeeds.get(0).isFeedNotFound());
        
    }
    
        
    @Test(expected = AssertionError.class)
    public void testDeleteByWrongUser() throws Exception{
        UserBase author = users.get(0);
        
        int postNum = 1;
        postMultipleFeedByOneFollowable(author, postNum,false);
        Integer followableId = getFolloweableId(author);
        FeedContentBase toDelete = cachedPosts.get(followableId).get(0);
        
        UserBase requester = users.get(1);
        
        deleteFeed(requester,toDelete);
        
    }
}
