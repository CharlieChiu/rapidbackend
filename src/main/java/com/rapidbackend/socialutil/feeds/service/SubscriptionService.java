package com.rapidbackend.socialutil.feeds.service;

import com.rapidbackend.core.model.DbRecord;

public interface SubscriptionService extends FollowableService{
    
    public void removeSubscription(Integer followedId,Integer followerId) throws Exception;
    
    public void addFollowerToSubsciptionCache(Integer followerId, Integer followedId,DbRecord subscriptionRecord) throws Exception;
    
    public int[] getFollowerList(Integer followedId,StringBuffer handleInfo) throws Exception;
    
}
