package com.rapidbackend.socialutil.service.posts;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.service.SocialUtilTestBase;

public class TestFollowerList extends SocialUtilTestBase{
    
    public TestFollowerList(String followableName){
        super(followableName);
    }
    
    
    @Before
    public void before() throws Exception{
        cleanDb();
        cleanRedisInstances();
        initDataContainers();
        
        socializedUserNum = 30;// adjust the follower number to a larger value, this will generate n*n relationships
        
        createUsersAndFollowables();
        
        
        createRelationships();
    }
    /**
     * test read the follower list 
     * 
    public void testReadFollowers() throws Exception{
        
        UserBase author = users.get(1);
        Integer followableId = getFolloweableId(author);
        
        Integer pageSize = socializedUserNum-1;// the follower number should be socializedUserNum-1
        Integer start = 0;
        CommandResult<?> getFollowerResults =  getFollowers(followableId, start, pageSize);
        List<UserBase> followers = (List<UserBase>)getFollowerResults.getResult();
        
        assertTrue(followers.size() == pageSize);
    }*/
    
    @Test
    public void testReadFollowersWithPaging() throws Exception{
        Integer userIndex = 3;
        UserBase author = users.get(userIndex);
        Integer followableId = getFolloweableId(author);
        
        Integer pageSize = 10;// the follower number should be socializedUserNum-1
        Integer start = 0;
        CommandResult<?> getFollowerResults =  getFollowers(followableId, start, pageSize);
        List<UserBase> followers = (List<UserBase>)getFollowerResults.getResult();
        
        assertTrue(followers.size() == pageSize);
        assertTrue(followers.size() == getFollowerResults.getPageSize());
        assertNotNull(getFollowerResults.getNextStart());
        
        Integer nextStart = getFollowerResults.getNextStart();
        Integer nextStartShouldBe = (socializedUserNum-1)*userIndex + pageSize;
        assertEquals(nextStart, nextStartShouldBe);
        
        CommandResult<?> getFollowerResultsRound2 =  getFollowers(followableId, nextStart, pageSize);
        List<UserBase> followersNextPage = (List<UserBase>)getFollowerResultsRound2.getResult();
        
        assertTrue(followersNextPage.size() == pageSize);
        assertTrue(followersNextPage.size() == getFollowerResultsRound2.getPageSize());
        assertNotNull(getFollowerResultsRound2.getNextStart());
        
        Integer nextStart2 = getFollowerResultsRound2.getNextStart();
        Integer nextStartShouldBe2 = (socializedUserNum-1)*userIndex + pageSize*2;
        
        assertEquals(nextStart2, nextStartShouldBe2);
    }
    
    
    //@Test
    public void testReadFollowersAfterUnsubscribtion() throws Exception{
        UserBase author = users.get(0);
        Integer followableId = getFolloweableId(author);
        
    }
}
