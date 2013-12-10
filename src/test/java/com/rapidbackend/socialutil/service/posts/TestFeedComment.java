package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.socialutil.model.reserved.CommentBase;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

public class TestFeedComment extends SocialUtilTestBase{
    
    Logger logger = LoggerFactory.getLogger(getClass());
    
    public TestFeedComment(String followableName){
        super(followableName);
    }
    
    @Before
    public void before() throws Exception{
        cleanDb();
        
        cleanRedisInstances();
        
        initDataContainers();
        socializedUserNum = 15;
        createUsersAndFollowables();
                
        createRelationships();
        
        logger.debug("TestFeedComment before <===============");
    }
    @Test
    public void testCommentFeed() throws Exception{
        UserBase author = users.get(0);
        FeedContentBase feed = postFeed(author, getFolloweableId(author), false);
        
        List<UserBase> followers = getFollowers(getFolloweableId(author), 0, socializedUserNum).getResult();
        
        assertTrue(followers.size() == socializedUserNum-1);
        UserBase follower = followers.get(0);
        
        CommentBase comment = commentFeed(feed, follower, follower.getScreenName()).getResult();
        assertEquals(comment.getScreenName(),follower.getScreenName());
        
    }
    
    @Test
    public void testReadFeedComments() throws Exception{
        UserBase author = users.get(0);
        FeedContentBase feed = postFeed(author, getFolloweableId(author), false);
        
        List<UserBase> followers = getFollowers(getFolloweableId(author), 0, socializedUserNum).getResult();
        
        assertTrue(followers.size() == socializedUserNum-1);
        
        for(UserBase follower:followers){
            commentFeed(feed, follower, "comment by "+follower.getScreenName());
        }
        
        CommandResult<List<CommentBase>> comments = readFeedComment(feed, 0, socializedUserNum-1);
        assertEquals(comments.getResult().size(), socializedUserNum -1);
        
        assertEquals(comments.getNextStart().intValue(), 1);
        
        comments = readFeedComment(feed, comments.getNextStart(), socializedUserNum-1);
        
        assertEquals(comments.getResult().size(), 0);
    }
    
    /*
    public void testReplyComment(){
        
    }
    
    public void testReadReplys(){
        
    }*/
}