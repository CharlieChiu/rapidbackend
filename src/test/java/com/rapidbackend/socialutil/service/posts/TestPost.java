package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

/**
 * transient class for testing basic functions
 * @author chiqiu
 *
 */
public class TestPost extends SocialUtilTestBase{
	
    public TestPost(String followableName){
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
            logger.debug("TestPost before <===============");
        } catch (Exception e) {
            logger.error("TestPost before :" ,e);
            throw e;
        }
    }
	
	
	/**
	 * test post one userpost and check if all followers receive  it
	 * @throws Exception
	 */
	@Test
	public void testPostFeed() throws Exception{
		
		UserBase author = users.get(0);
		FeedContentBase feed = postFeed(author,author.getId(),true);
		Integer followableId = getFolloweableId(author);
        List<UserBase> followers = getFollowers(followableId);
		
		for(UserBase user: followers){
		    List<FeedContext> timeline = getTimeline(user);
		    FeedContext feedContext = timeline.get(0);
		    assertNotNull(feedContext);
		    FeedContentBase receievedFeed = feedContext.feed();
		    assertTrue(feed.getId()== receievedFeed.getId());
		    assertTrue(feed.getContent().equals(receievedFeed.getContent()));
		}
	}
	
	
}
