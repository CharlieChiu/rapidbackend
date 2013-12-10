package com.rapidbackend.socialutil.subscription;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.cache.RedisCache;
import com.rapidbackend.socialutil.dao.BaseDao.DisplayOrder;
import com.rapidbackend.socialutil.dao.BaseDao.OrderByColumn;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.socialutil.dao.SubscriptionDao;
import com.rapidbackend.socialutil.feeds.DefaultFeedService;
import com.rapidbackend.socialutil.feeds.DefaultFeedService.FeedEssential;
import com.rapidbackend.socialutil.feeds.InboxService;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.socialutil.model.reserved.FollowabeFeedBase;
import com.rapidbackend.socialutil.model.reserved.Subscription;
import com.rapidbackend.socialutil.model.util.TypeFinder;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.general.ConversionUtils;
import com.rapidbackend.util.time.SimpleTimer;
import com.rapidbackend.socialutil.dao.BaseDao.RecordOrder;

public class SubscriptionService extends ClusterableService{
    Logger logger = LoggerFactory.getLogger(SubscriptionService.class);
    protected SubscriptionDao subscriptionDao;
    
	/**
	 * The subscription cache is a cache which only stores followers of one user.
	 * The max size of memory consumed by the cache should be set in the redis setting.
	 *<p/> For now the cache logic on java side is quite simple:<br/>
	 *  Do add/delete in db.then,
	 *  If cache exists, do add/delete in cache.
	 *  else create the user's subscription cache.
	 *  The add remove cache part should be done in lua for performance.
	 *  //TODO all the redis object cache should be implemented with a locking schema, referring to http://redis.io/commands/setnx to implement the locking pattern
	 *  // this will be a major issue after the orginal release
	 */
	protected RedisCache subscriptionCache;
	protected InboxService inboxService;
	protected DefaultFeedService feedService;
	
	
    public InboxService getInboxService() {
        return inboxService;
    }
    @Required
    public void setInboxService(InboxService inboxService) {
        this.inboxService = inboxService;
    }
    public DefaultFeedService getFeedService() {
        return feedService;
    }
    @Required
    public void setFeedService(DefaultFeedService feedService) {
        this.feedService = feedService;
    }
    public RedisCache getSubscriptionCache() {
        return subscriptionCache;
    }
    public void setSubscriptionCache(RedisCache subscriptionCache) {
        this.subscriptionCache = subscriptionCache;
    }
    public SubscriptionDao getSubscriptionDao() {
        return subscriptionDao;
    }
    @Required
    public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }
    
    protected TypeFinder typeFinder;
    
    
    public TypeFinder getTypeFinder() {
        return typeFinder;
    }
    @Required
    public void setTypeFinder(TypeFinder typeFinder) {
        this.typeFinder = typeFinder;
    }
    
    public Map<Double, String> createScoreMembers(List<Subscription> subscriptions){
        Map<Double, String> scoreMembers = new HashMap<Double, String>();
        for(DbRecord record : subscriptions){
            Double scoreDouble = record.getCreated().doubleValue();
            String member = Integer.toString(record.getId());
            scoreMembers.put(scoreDouble, member);
        }
        return scoreMembers;
    }
    /**
     * TODO move to SubscriptionDao
     * @param followerId
     * @return
     */
    protected String genSelectFollowableSql(Integer followerId){
        StringBuffer sb = new StringBuffer();
        sb.append("select followable from ");
        sb.append(subscriptionDao.getTableName());
        sb.append(" where follower=");
        sb.append(Integer.toString(followerId)).append(";");
        return sb.toString();
    }
    
    public List<Integer> getFollowedFollowableIdsFromDb(Integer userId){
        String query = genSelectFollowableSql(userId);
        return subscriptionDao.selectIntColumBySql(query);
    }
    /**
     * return all followable's ids 
     * @param followable
     * @return
     */
    public List<Subscription> getAllFollowersIdsFromDatabase(Integer followable) {
        return subscriptionDao.selectListByColumn("followable", followable.toString(),Subscription.class);
    }
    protected RecordOrder orderByIdAsc = new RecordOrder(DisplayOrder.asc, OrderByColumn.id);
    
    
    public List<Subscription> getFollowersIdsFromDatabase(Integer followable,PagingInfo pagingInfo) {
        //return subscriptionDao.selectListByParamsWithPaging(params, Subscription.class, startId, pageSize, orderByIdAsc);
        return subscriptionDao.selectSubscriptionsByFollowableId(followable, pagingInfo);
    }
    
    public boolean isSubscriptionCacheConfigured(){
        return subscriptionCache!=null;
    }
    public int[] getFollowerListFromCache(Integer userId) throws InterruptedException{
        if(!isSubscriptionCacheConfigured()){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR, "subscriton cache isn't configured");
        }
        Set<String> keys = subscriptionCache.zrevrange(userId.toString(), userId, 0, Integer.MAX_VALUE);
        int[] result = ConversionUtils.stringCollentionToIntArray(keys);
        return result;
    }
    
    public int[] getFollowerList(Integer followableId,StringBuffer handleInfo) throws InterruptedException{
        SimpleTimer timer = new SimpleTimer("get follower list for userId:"+followableId);
        SimpleTimer cacheReadTimer =null;
        SimpleTimer cacheWriteTimer =null;
        SimpleTimer dbTimer=null;
        List<Subscription> result = null;
        boolean retrivedFromDb = false;
        int[] followerIds = null;
        if(isSubscriptionCacheConfigured()){
            cacheReadTimer = new SimpleTimer("read list from cache");
            followerIds = getFollowerListFromCache(followableId);
            cacheReadTimer.stop();
        }
        if(!(followerIds!=null && followerIds.length>0)){
            dbTimer = new SimpleTimer("read list from db");
            result = getAllFollowersIdsFromDatabase(followableId);
            followerIds = new int[result.size()];
            int i=0;
            for (Subscription s:result) {
                followerIds[i++] = s.getFollower();
            }
            retrivedFromDb = true;
            dbTimer.stop();
        }
        if(isSubscriptionCacheConfigured()
                &&retrivedFromDb&&followerIds!=null&&followerIds.length>0){
            cacheWriteTimer = new SimpleTimer("write follower list to cache");
            Map<Double, String> scoremembers = createScoreMembers(result);
            getSubscriptionCache().zaddMulti(followableId.toString(), scoremembers, followableId);
            cacheWriteTimer.stop();
        }
        
        handleInfo.append(timer.getIntervalString()).append(",");
        if (cacheReadTimer!=null) {
            handleInfo.append(cacheReadTimer.getIntervalString()).append(",");
        }
        if (dbTimer!=null) {
            handleInfo.append(dbTimer.getIntervalString());
        }
        if (cacheWriteTimer!=null) {
            handleInfo.append(cacheWriteTimer.getIntervalString());
        }
        //logger.info(handleInfo.toString());//TODO do we have to remove the log ??
        return followerIds;
    }
    
    /**
     * sync with redis is a little complicate for subscription cache.
     * When the cached follower list expired and removed from redis,
     * we should not add items into that set. We should wait the next
     * read follower list action create an accurate follower list set then
     * we can add items to that set again.
     */
    protected static String addFollowerLua = "local res\n" +
            "res = redis.call('EXISTS',KEYS[1])\n" +
            "if res==1 then redis.call('ZADD',KEYS[1],ARGV[1],ARGV[2]) end\n" +
            "return res";
    
    /**
     * 
     * @param followerId
     * @param followedId
     * @param subscriptionRecord
     * @throws Exception
     */
    public void addFollowerToSubsciptionCache(Integer followerId, Integer followedId,DbRecord subscriptionRecord) throws Exception{
        if(!isSubscriptionCacheConfigured()){
            return;
        }
        List<String> keys = new ArrayList<String>();
        keys.add(followerId.toString());
        List<String> args = new ArrayList<String>();
        args.add(subscriptionRecord.getCreated().toString());
        args.add(followerId.toString());
        subscriptionCache.eval(addFollowerLua, keys, args, followedId);
    }
    
    /**
     * removes one follower's subscription,clean  
     * @param followableId
     * @param followerId
     */
    public void removeSubscription(Integer followableId,Integer followerId) throws Exception{
        HashMap<String, Integer> params = new HashMap<String, Integer>();
        params.put("followable", followableId);
        params.put("follower", followerId);
        
        Class<?> modelClass = getTypeFinder().getModelClass(subscriptionDao.getTableName());
        // first check if the subscription exists
        Integer subscriptionId = null;
        try {
            subscriptionId = subscriptionDao.selectIntObject(params, "id", modelClass);
        } catch (Exception e) {
            // TODO: handle exception
        }
        int deleted = subscriptionDao.deleteModelById(subscriptionId);
        if(deleted >0){
            removeSubscriptionFromInbox(followableId,followerId);
        }
    }
    /**
     * remove the feed subscriptions from inbox
     */
    public void removeSubscriptionFromInbox(Integer followableId,Integer followerId) throws InterruptedException{
        Integer inboxsize = getInboxService().getInboxSize();
        Set<String> feedEssensials = getInboxService().getFeedEssentialsFromRedisInbox(followerId, 0, inboxsize);
        if(feedEssensials!=null && feedEssensials.size()>0){
            List<String> feeds = new ArrayList<String>();
            for(String feedEssential:feedEssensials){
                FeedEssential fe = new FeedEssential(feedEssential);
                
                //int idx = DefaultFeedService.getFollowedIndexInFeedEssential(followType);
                if( fe.getFollowableId().equalsIgnoreCase(followableId.toString())){// the first item in feedEssential should be followed's id
                    feeds.add(feedEssential);
                }
            }
            if (feeds.size()>0) {
                String[] members = feeds.toArray(new String[0]);
                getInboxService().getInboxCache().zrem(followerId.toString(), members, followerId);
            }
        }
    }
    
    protected RecordOrder orderByIdDesc = new RecordOrder(DisplayOrder.desc,OrderByColumn.id);
    /**
     * TODO handle info should be added
     * @param followedId
     * @param followerId
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws InterruptedException
     */
    public void addSubscription(Integer followableId,Integer followerId) throws IllegalAccessException,InvocationTargetException,InstantiationException,InterruptedException{
        Class<?> subscriptionClass = getTypeFinder().getModelClass(subscriptionDao.getTableName());
        Object subscription = createSubscriptionModel(subscriptionClass,  followableId,  followerId);
        
        if(getInboxService().isUserInboxReady(followerId)
                || getInboxService().isPushAllways()){
            Integer maxReturnFeedsNumber = getInboxService().getInboxSize();
            Map<String, Integer> params = new HashMap<String, Integer>();
            params.put(ParamNameUtil.Followable_ID, followableId);
            List<FollowabeFeedBase> result = getFeedService().getFeedDao().selectListByParamsWithPaging(params, FollowabeFeedBase.class, 0, maxReturnFeedsNumber, orderByIdDesc);
            if(result.size()>0){
                HashMap<Double, String> members = getInboxService().createInboxMembers(result);
                getInboxService().getInboxCache().zaddMultiPopSmallest(followerId.toString(), members, followerId, getInboxService().getInboxSize());
            }
        }
        
        subscriptionDao.storeNewModelBean(subscription);
    }
    
    public Subscription createSubscriptionModel(Class<?> subscriptionClass,Integer followableId,Integer followerId) throws IllegalAccessException,InvocationTargetException,InstantiationException,InterruptedException{
        Object model = subscriptionClass.newInstance();
        BeanUtils.copyProperty(model, "followable", followableId);
        BeanUtils.copyProperty(model, "follower", followerId);
        return (Subscription)model;
    }
    
    
    @Override
    public void doStart() throws Exception{
        //TODO we init the subscription cache here if it is configured
    }
    @Override
    public void doStop() throws Exception{
        
    }
}
