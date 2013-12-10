package com.rapidbackend.socialutil.service.posts;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

public class TestUpdateFeed extends SocialUtilTestBase{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    public TestUpdateFeed(String followableName){
        super(followableName);
    }
    
    @Before
    public void before() throws Exception{
        cleanDb();
        
        cleanRedisInstances();
        
        initDataContainers();
        
        createUsersAndFollowables();
        
        
        createRelationships();
        
        logger.debug("TestUpdateFeed before <===============");
    }
    @Test
    public void testUpdateFeed() throws Exception{
        UserBase author = users.get(0);
        Integer followableId = getFolloweableId(author);
        FeedContentBase feed = postFeed(author, followableId, false);
        String updatedContent = "I am updated";
        CommandResult<FeedContentBase> commandResult = updateFeed(feed, author,updatedContent);
        FeedContentBase updatedFeed = (FeedContentBase)commandResult.getResult();
        String content = updatedFeed.getContent();
        assertEquals(updatedContent, content);
        sleep4Subscription();
    }
    @Test(expected=AssertionError.class)
    public void testUpdateFeedWithWrongUser() throws Exception{
        UserBase author = users.get(0);
        UserBase user = users.get(1);
        Integer followableId = getFolloweableId(author);
        FeedContentBase feed = postFeed(author, followableId, false);
        String updatedContent = "I am updated";
        CommandResult<FeedContentBase> commandResult = updateFeed(feed, user,updatedContent);
        FeedContentBase updatedFeed = (FeedContentBase)commandResult.getResult();
        String content = updatedFeed.getContent();
        assertEquals(updatedContent, content);
        sleep4Subscription();
    }
}
