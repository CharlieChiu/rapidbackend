package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTimeline extends SocialUtilTestBase{
    // TODO add an after method to clean all redis instances because sometimes some of the redis targets are not initialized in some tests
    @Before
    public void before() throws Exception{
        try {
            cleanDb();
            cleanRedisInstances();
            initDataContainers();
            socializedUserNum = 10;
            createUsersAndFollowables();
            
            createRelationships();
            
            
            logger.debug("TestTimeline before <===============");
        }  catch (Exception e) {
            logger.error("hit exception in before()",e);
            throw e;
        }
        
    }
    public TestTimeline(String followableName){
        super(followableName);
    }
    
    @After
    public void after() throws Exception{
        try {
            logger.debug("TestTimeline after ===============>");
            cleanDb();
            cleanRedisInstances();
            initDataContainers();
            socializedUserNum = 10;
            createUsersAndFollowables();
            
            createRelationships();
            logger.debug("TestTimeline after <===============");
        }  catch (Exception e) {
            logger.error("hit exception in before()",e);
            throw e;
        }
        
    }
    /**
     * Test feeds added into one users timeline after subscribing a followable
     * @throws Exception
     */
    @Test
    public void testSubscribe() throws Exception{
        UserBase user = lonelyOne();
        int postNum = 21;
        List<UserBase> socializedones = socializedUsers();
        postMultipleFeedByMultipleFollowable(socializedones,postNum);
        
        for(int i = 0;i<socializedones.size();i++){
            
            UserBase tofollow = users.get(i);//follow one by one
            Integer followableId = getFolloweableId(tofollow);
            createSubsctiprion(followableId, user,true);
            
            List<UserBase> usersubSet = users.subList(0, i+1);
            List<FeedContentBase> posts = sortPosts(usersubSet);
            int totalPostNum = posts.size();
            List<FeedContext> timeline = getTimeline(user,0,totalPostNum);
            int j = 0;
            for(FeedContext feedContext :timeline){// compare time line with cached posts
                FeedContentBase feed = (FeedContentBase)feedContext.getFeed();
                assertNotNull(feed);
                FeedContentBase cachedFeed = posts.get(j++);
                assertEquals(feed.getId(), cachedFeed.getId());
            }
        }
        
        System.out.println();
    }
    
    
    
    /**
     * Test post multiple posts by one user, test the order of the feeds is correct
     * @throws Exception
     */
    @Test
    public void testPostMultipleFeedByOneUser() throws Exception{
        
        UserBase author = users.get(0);
        int postNum = 150;
        postMultipleFeedByOneFollowable(author,postNum);
        
        List<FeedContentBase> allPosts = cachedPosts.get(getFolloweableId(author));
        Integer followableId = getFolloweableId(author);
        List<UserBase> followers = getFollowers(followableId);
        
        for(UserBase user: followers){
            List<FeedContext> timeline = getTimeline(user,0,postNum);
            assertEquals(timeline.size(), postNum);
            int i = 0;
            for(FeedContext feedContext :timeline){
                FeedContentBase feed = (FeedContentBase)feedContext.getFeed();
                assertNotNull(feed);
                int lastIndex = postNum-1;
                FeedContentBase cachedFeed = allPosts.get(lastIndex - i++);
                assertEquals(feed.getId(), cachedFeed.getId());
            }
        }
    }
    
    /**
     * Test feeds removed from one users timeline after Unsubscribe a followable
     * @throws Exception
     */
    @Test
    public void testUnsubscribe() throws Exception{
        
        int postNumPerUser = 21;
        List<UserBase> socializedones = socializedUsers();
        postMultipleFeedByMultipleFollowable(socializedones,postNumPerUser);
        
        UserBase user = users.get(0);
        
        List<FeedContentBase> allPosts = sortAllPosts();
        int socializedNum = socializedones.size();
        List<FeedContext> timeline = getTimeline(user, 0, postNumPerUser * socializedNum);
        
        int postNumInFollowersTimeline = getPostNumInFollowersTimeline(postNumPerUser,socializedUserNum);
        
        assertEquals(postNumInFollowersTimeline,timeline.size());
        
        for(FeedContext feed : timeline){
            FeedContentBase f = (FeedContentBase)feed.getFeed();
            logger.debug(f.getId()+"");
            assertTrue(findFeed(f, allPosts));    
        }
        
        UserBase u = users.get(1);
        Integer followableId = getFolloweableId(u);
        
        List<FeedContentBase> postsToRemove = cachedPosts.get(followableId);// posts from user1 should be removed in user0's inbox
        
        unsubscribe(followableId, user);
        
        timeline = getTimeline(user, 0, postNumPerUser * socializedNum);
        
        assertEquals(getPostNumInFollowersTimeline(postNumPerUser, socializedUserNum-1),timeline.size());
        
        for(FeedContext feed : timeline){
            FeedContentBase f = (FeedContentBase)feed.getFeed();
            assertFalse(findFeed(f, postsToRemove));
        }
        logger.debug("testUnsubscribe ends");
    }
    /**
     * Test post multiple posts by multiple users, test the order of the feeds is correct
     * @throws Exception
     */
    @SuppressWarnings("static-access")
    @Test
    public void testPostMultipleFeedsByMultipleFollowables() throws Exception{
        //for the first num-1 users, each user posts 21 feeds
        int postNumPerUser = 21;
        List<UserBase> socializedones = socializedUsers();
        postMultipleFeedByMultipleFollowable(socializedones,postNumPerUser);
        int postNumInFollowersTimeline = getPostNumInFollowersTimeline(postNumPerUser,socializedUserNum);
        /**
         * sorted all user posts, the results should be the same with every user's timeline sequence
         */
        List<FeedContentBase> allPosts = sortAllPosts();
        
        for(UserBase user : socializedones){
            List<FeedContext> timeline = getTimeline(user,0,postNumInFollowersTimeline);
            
            if(postNumInFollowersTimeline != timeline.size()){
                Thread.currentThread().sleep(1000l);// sleep another second to wait for feed pushing completes
                timeline = getTimeline(user,0,postNumInFollowersTimeline);
            }
            
            assertEquals(postNumInFollowersTimeline, timeline.size());
            /*
            int i =0;
            
            for(FeedContext feedContext :timeline){
                FeedContentBase feed = (FeedContentBase)feedContext.getFeed();
                assertNotNull(feed);
                FeedContentBase cachedFeed = allPosts.get(i++);
                assertEquals(cachedFeed.getId(),feed.getId());
            }*/
        }
    }
    /**
     * test deletes a feed then check if one follower's time line has changed to mark the feed as not found
     * @throws Exception
     */
    @Test
    public void testDeleteFeed() throws Exception{
        UserBase author = users.get(0);
        int postNum = 150;
        postMultipleFeedByOneFollowable(author,postNum);

        Integer followableId = getFolloweableId(author);
        List<UserBase> followers = getFollowers(followableId);
        
        UserBase follower = followers.get(0);
        
        FeedContentBase todelete = cachedPosts.get(getFolloweableId(author)).get(0);
        deleteFeed(author,todelete);
        
        List<FeedContext> timeline = getTimeline(follower,0,postNum);
        assertEquals(timeline.size(), postNum);
        
        Integer deletedFeedId = todelete.getId();
        FeedContext shouldBeDeleted = null;
        for(FeedContext feedContext :timeline){
            assertNotNull(feedContext.getId());
            if(feedContext.getId().equals(deletedFeedId)){
                shouldBeDeleted = feedContext;
            }
        }
        
        assertNotNull(shouldBeDeleted);
        assertTrue(shouldBeDeleted.isFeedNotFound());
    }
    
    protected int getPostNumInFollowersTimeline(int postsPerUser, int socializedNum){
        if(followableName.equalsIgnoreCase("user")){
            return socializedNum *postsPerUser;
        }else {
            return (socializedNum-1)*postsPerUser;
            // TODO for now user don't subscribe the followable he creates, should we change it to subscribe as default???
            // if we do so, then we need to move all create followable functions into the service.xml and append a subscribepipeline right after the createfollowable command
        }
    }
    
    
}
