package com.rapidbackend.socialutil.feeds.service;

import com.rapidbackend.core.model.DbRecord;


public interface SubscriptionCacheService {
    
    public int[] getFollowerListFromCache(Integer userId) throws Exception;
    
    public void addFollowerToSubsciptionCache(Integer followerId, Integer followedId,DbRecord subscriptionRecord) throws Exception;
}
