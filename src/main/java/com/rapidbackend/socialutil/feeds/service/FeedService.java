package com.rapidbackend.socialutil.feeds.service;

import java.util.HashMap;

import com.rapidbackend.socialutil.feeds.FeedException;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
/**
 * 
 * @author chiqiu
 *
 */
public interface FeedService extends FollowableService{
	
	
	public Class<?> getFeedContentClass();
    /**
     * 
     * @param feed
     * @param userId
     * @param followableId
     * @return
     * @throws FeedException
     */
    public FeedContentBase postFeed(FeedContentBase feed,Integer userId,Integer followableId) throws FeedException;
    //public FeedBase repost(Integer repostToFeedId,Integer userId,Integer followableId,String content) throws FeedException;
    /**
     * return the number of feed deleted
     * @param feedId
     * @param userId
     * @return
     * @throws FeedException
     */
    //public int deleteFeed(Integer feedId,Integer userId) throws FeedException;
    
    //public FeedBase updateFeed(Integer feedId, Integer userId, String content) throws FeedException;
    public FeedContentBase repost(int repostToFeedId,Integer userId,Integer followableId,String content,Class<? extends FeedContentBase> feedContentClass) throws FeedException;
    
    /**
     * 
     * @param feedId
     * @param userId
     * @param followableId
     * @return
     * @throws FeedException
     */
    public FeedContentBase deleteFeed(Integer feedId,Integer userId,Integer followableId) throws FeedException;
    
    /**
     * update a feed by its id
     * @param updates
     * @param id
     * @return
     * @throws FeedException
     */
    public FeedContentBase updateFeed(HashMap<?, ?> updates,Integer id) throws FeedException;
}
