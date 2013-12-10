package com.rapidbackend.socialutil.feeds;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import redis.clients.jedis.Tuple;

import com.rapidbackend.redisqueue.RedisQueuePollHandler;
import com.rapidbackend.socialutil.feeds.DefaultFeedService.FeedEssential;
import com.rapidbackend.socialutil.subscription.SubscriptionService;
import com.rapidbackend.util.io.JsonUtil;
import com.rapidbackend.util.time.SimpleTimer;

public class FeedPollHandler extends RedisQueuePollHandler{
    protected Logger logger = LoggerFactory.getLogger(FeedPollHandler.class);
    protected SimpleTimer timer = new SimpleTimer("FeedPoller");
    
    protected SubscriptionService subscriptionService;
    protected InboxService inboxService;
    protected boolean pushUserFeeds = false;
    
    public boolean isPushUserFeeds() {
        return pushUserFeeds;
    }
    public void setPushUserFeeds(boolean pushUserFeeds) {
        this.pushUserFeeds = pushUserFeeds;
    }
    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }
    @Required
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    
    public InboxService getInboxService() {
        return inboxService;
    }
    @Required
    public void setInboxService(InboxService inboxService) {
        this.inboxService = inboxService;
    }
    public void handleReturnValue(Object redisqueueItem){
        logger.debug("handleReturnValue  ------> "+redisqueueItem);
        setCurrentState("handling return value");
        timer.reset();
        try{
            String returnValue  = redisqueueItem.toString();
            if(!StringUtils.isEmpty(returnValue)){
                String feedEssentialString  = JsonUtil.readObject(returnValue, String.class);
                logger.debug("return value is "+feedEssentialString);
                FeedEssential feedEssential = DefaultFeedService.createFeedFromEssentialString(feedEssentialString);
                //User followed = UserFactory.createUser(feedEssential.getUserId());
                int[] followerIds =  subscriptionService.getFollowerList(Integer.valueOf(feedEssential.getFollowableId()),new StringBuffer());
                List<Integer> onlineFollowers = inboxService.getOnlineFollowers(followerIds);
                
                Tuple tuple = feedEssential.createFeedTuple();
                for(Integer followerId : onlineFollowers){
                    logger.debug("push feed into "+followerId.toString());
                    //for now we just create db for every one
                    inboxService.getInboxCache().zaddLuaRemSmallest(followerId.toString(), 
                            tuple, followerId, inboxService.getInboxSize());
                }
                if(isPushUserFeeds()){// push the feed into the users own inbox, the means we are posting a `UserFeed`
                    String followerId = feedEssential.getFollowableId().toString();
                    inboxService.getInboxCache().zaddLuaRemSmallest(followerId, 
                            tuple, Integer.valueOf(followerId), inboxService.getInboxSize());
                }
                
                //String timeElapsed = timer.getIntervalString();
            }
            
            
        }catch(Exception e){
            logger.error("error handling polled feed from queue :",e);
        }
        logger.debug("handleReturnValue  <------");
    }
}
