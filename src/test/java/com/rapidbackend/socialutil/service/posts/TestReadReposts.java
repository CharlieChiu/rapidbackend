package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

public class TestReadReposts extends SocialUtilTestBase{
    
    public TestReadReposts(String followableName){
        super(followableName);
    }
    
    @Before
    public void before() throws Exception{
        try {
            cleanDb();
            cleanRedisInstances();
            initDataContainers();
            socializedUserNum = 31;
            createUsersAndFollowables();
            
            
            createRelationships();
            logger.debug("TestPost before <===============");
        } catch (Exception e) {
            logger.error("TestPost before :" ,e);
            throw e;
        }
    }
    
    @Test
    public void testReadRepostsWithPaging() throws Exception{
        UserBase author = users.get(0);
        FeedContentBase feed = postFeed(author,author.getId(),false);// one feed posted
        Integer followableId = getFolloweableId(author);
        List<UserBase> followers = (List<UserBase>)getFollowers(followableId,0,socializedUserNum).getResult();
        
        for(UserBase follower:followers){
            Integer followableId_follower = getFolloweableId(follower);
            String content = "I am a repost";
            FeedContentBase reposted  = repostFeed(follower, follower.getId(), feed.getId(), content,true);
        }
        
        int numOfReposts = socializedUserNum -1;
        
        Integer pageSize = 10;
        Integer pageNum = 1;
        Integer start = 0;
        
        while(pageNum <= numOfReposts/pageSize){
            CommandResult<?> commandresult=readFeedReposts(feed.getId(), start, pageSize);
            
            assertEquals(commandresult.getPageSize(), pageSize);
            assertEquals(commandresult.getNextStart().intValue(), numOfReposts-pageNum*pageSize+2);
            start = commandresult.getNextStart();
            pageNum++;
        }
        
        CommandResult<?> commandresult=readFeedReposts(feed.getId(), start, pageSize);
        assertEquals(commandresult.getPageSize().intValue(), 0);
        assertEquals(commandresult.getNextStart().intValue(), -1);
    }
    
}
