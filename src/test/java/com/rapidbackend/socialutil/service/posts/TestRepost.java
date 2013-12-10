package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.socialutil.model.metadata.FeedMetaData;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

public class TestRepost extends SocialUtilTestBase{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    public TestRepost(String followableName){
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
    
    @Test
    public void testRepost() throws Exception{
        UserBase author = users.get(0);
        FeedContentBase feed = postFeed(author,author.getId(),false);// one feed posted
        Integer followableId = getFolloweableId(author);
        List<UserBase> followers = getFollowers(followableId);
        
        UserBase follower = followers.get(0);
        Integer followableId_follower = getFolloweableId(follower);
        String content = "I am a repost";
        FeedContentBase reposted  = repostFeed(follower, follower.getId(), feed.getId(), content,true);
                
        List<UserBase> followersFollowers = getFollowers(followableId_follower);
        
        for(UserBase user : followersFollowers){
            
            List<FeedContext> timeline = getTimeline(user);
            FeedContext feedContext = timeline.get(0);
            assertNotNull(feedContext);
            FeedContentBase receievedFeed = feedContext.feed();
            assertTrue(receievedFeed.getId()== reposted.getId());
            assertTrue(receievedFeed.getContent().equals(content));
            FeedContentBase seedFeed = feedContext.seedFeed();
            assertNotNull(seedFeed);
            assertTrue(seedFeed.getId() == feed.getId());
        }
        
                
        CommandResult<List<FeedMetaData>> metadatas = readMetaData(new int[]{feed.getId()});
        FeedMetaData metaData = metadatas.getResult().get(0);
        assertEquals(1,metaData.getRepostCount().intValue());
    }
    
    
}
