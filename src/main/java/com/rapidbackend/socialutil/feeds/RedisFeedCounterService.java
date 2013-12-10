package com.rapidbackend.socialutil.feeds;

import java.util.ArrayList;
import java.util.List;

import com.rapidbackend.core.BackendErrorCodes;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.sharding.defaultmappers.RedisCounterLocationMapper;
import com.rapidbackend.util.comm.redis.RedisException;
import com.rapidbackend.util.comm.redis.RedisService;
import com.rapidbackend.util.comm.redis.client.RedisClient;
import com.rapidbackend.util.general.ConversionUtils;


/**
 * counter service which talks to redis server
 * @author chiqiu
 *
 */
public class RedisFeedCounterService extends AppContextAware{
    
    protected RedisService redisService;
    protected RedisCounterLocationMapper redisCounterLocationMapper;
    public RedisService getRedisService() {
        return redisService;
    }

    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    public RedisFeedCounterService(RedisService redisService){
        this.redisService = redisService;
        this.redisCounterLocationMapper = (RedisCounterLocationMapper)getApplicationContext().getBean("RedisCounterLocationMapper");
    }
    /**
     * @param feed the feed to be reposted
     */
    public void increaseRepostCount(FeedContentBase feed) throws Exception{
        int userId = feed.getUserId();
        String counterTarget = redisCounterLocationMapper.getRedisCounterTargetName(userId);
        RedisClient redisClient = redisService.getRedisPoolContainer().borrowClient(counterTarget);
        try {
            redisClient.getJedis().incr(createRepostCounterKey(feed.getId()));
        }finally{
            if(null!=redisClient)
                redisService.getRedisPoolContainer().returnClient(redisClient, counterTarget);
        }
    }
    /**
     * @param one repost that is deleted
     */
    public void decreaseRepostCount(FeedContentBase feed) throws Exception{
        int userId = feed.getRepostToUserId();
        String counterTarget = redisCounterLocationMapper.getRedisCounterTargetName(userId);
        RedisClient redisClient = redisService.getRedisPoolContainer().borrowClient(counterTarget);
        try {
            redisClient.getJedis().decr(createRepostCounterKey(feed.getRepostToFeedId()));
        }finally{
            if(null!=redisClient)
                redisService.getRedisPoolContainer().returnClient(redisClient, counterTarget);
        }
    }
    /**
     * remove one post's repost count when the post get deleted
     * @param feed
     * @throws Exception
     */
    public void destroyRepostCount(FeedContentBase feed) throws Exception{
        int userId = feed.getUserId();
        String counterTarget = redisCounterLocationMapper.getRedisCounterTargetName(userId);
        RedisClient redisClient = redisService.getRedisPoolContainer().borrowClient(counterTarget);
        try {
            long deletedKeys = redisClient.getJedis().del(createRepostCounterKey(feed.getId()));
            if(deletedKeys>1){
                throw new RedisException(BackendErrorCodes.RedisGenericError, "del key:"+createRepostCounterKey(feed.getId())+",returned "+deletedKeys);
            }
        }finally{
            if(null!=redisClient)
                redisService.getRedisPoolContainer().returnClient(redisClient, counterTarget);
        }
    }
    @Deprecated
    public Integer getRepostCount(FeedContentBase feed) throws Exception{
        int feedId = feed.getId();
        int userId = feed.getUserId();
        int count = 0;
        String counterTarget = redisCounterLocationMapper.getRedisCounterTargetName(userId);
        RedisClient redisClient = redisService.getRedisPoolContainer().borrowClient(counterTarget);
        try {
            String repostCountString = redisClient.getJedis().get(createRepostCounterKey(feedId));
            count = Integer.parseInt(repostCountString);
        }finally{
            redisService.getRedisPoolContainer().returnClient(redisClient, counterTarget);
        }
        return count;
    }
    /**
     * 
     * @param feedIds
     * @return
     * @throws Exception
     */
    public int[] getRepostCounts(int[] feedIds) throws Exception{
        String counterTarget = redisCounterLocationMapper.getRedisCounterTargetName(null);//TODO for now I think we don't need to shard a redis counter service, one instance is enough, we might need to use round robin here if we use a redis cluster
        RedisClient redisClient = redisService.getRedisPoolContainer().borrowClient(counterTarget);
        List<Integer> counts = new ArrayList<Integer>();
        try {
            List<String> counterStrings= redisClient.getJedis().mget(createRepostCounterKeys(feedIds));
            
            for(String k: counterStrings){
                if(k==null){
                    counts.add(0);
                }else {
                    counts.add(Integer.parseInt(k));
                }
            }
        }finally{
            redisService.getRedisPoolContainer().returnClient(redisClient, counterTarget);
        }
        return ConversionUtils.integerCollectionToIntArray(counts);
    }
    
    public static String createRepostCounterKey(int feedId){
        return "r"+feedId;
    }
    
    public static String[] createRepostCounterKeys(int[] feedIds){
        
        String[] res = new String[feedIds.length];
        for(int i=0;i<res.length;i++){
            res[i] = createRepostCounterKey(feedIds[i]);
        }
        return res;
    }
}