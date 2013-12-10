package com.rapidbackend.socialutil.feeds;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Required;

import redis.clients.jedis.Tuple;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.cache.RedisCache;
import com.rapidbackend.socialutil.dao.FeedDao;
import com.rapidbackend.socialutil.feeds.DefaultFeedService.FeedEssential;
import com.rapidbackend.socialutil.model.reserved.FollowabeFeedBase;
import com.rapidbackend.socialutil.subscription.SubscriptionService;
import com.rapidbackend.socialutil.user.UserStatusService;
import com.rapidbackend.util.comm.redis.client.RedisClient;
import com.rapidbackend.util.comm.redis.client.RedisClientPoolContainer;
import com.rapidbackend.util.comm.redis.client.RedisPoolConfig;
import com.rapidbackend.util.general.ConversionUtils;

/**
 * Class which is called when user loads feeds
 * It has such work flows:
 * 1. If Inbox is not inicialized, init it from the outbox cache
 * 2. If user is online, inbox should be marked as "online", inbox status should be
 * cached in memory with user profile id
 * 3. If user is offline, inbox should be marked as "offline"
 * @author chiqiu
 *
 */
public class InboxService extends ClusterableService implements BeanNameAware{//TODO change the class name?need separate redis pool init into different classes?
	Logger logger = LoggerFactory.getLogger(InboxService.class);
	protected RedisClientPoolContainer redisPoolContainer ;
	protected InboxConfig inboxConfig;
	protected UserStatusService userStatusService;// if this service is set and is running, we will push notifications only to online users
	protected RedisCache inboxCache;
	protected SubscriptionService subscriptionService;
	protected FeedDao feedDao;
	protected int inboxExpireTime = 14*24*3600;// two weeks
	List<RedisPoolConfig> redisPoolConfigs;// we must set this value to let us know which redis instance we need to talk to
	private String beanName;
	
	
    public String getBeanName() {
        return beanName;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public UserStatusService getUserStatusService() {
        return userStatusService;
    }
    
    public void setUserStatusService(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }
    public List<RedisPoolConfig> getRedisPoolConfigs() {
        return redisPoolConfigs;
    }
    @Required
    public void setRedisPoolConfigs(List<RedisPoolConfig> redisPoolConfigs) {
        this.redisPoolConfigs = redisPoolConfigs;
    }
    public InboxConfig getInboxConfig() {
        return inboxConfig;
    }
    @Required
    public void setInboxConfig(InboxConfig inboxConfig) {
        this.inboxConfig = inboxConfig;
    }
    public int getInboxExpireTime() {
        return inboxExpireTime;
    }
    public void setInboxExpireTime(int inboxExpireTime) {
        this.inboxExpireTime = inboxExpireTime;
    }
    public FeedDao getFeedDao() {
        return feedDao;
    }
    @Required
    public void setFeedDao(FeedDao feedDao) {
        this.feedDao = feedDao;
    }
    
    public RedisCache getInboxCache() {
        return inboxCache;
    }
    @Required
    public void setInboxCache(RedisCache inboxCache) {
        this.inboxCache = inboxCache;
    }
    
    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }
    @Required
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    
    public InboxService(){
	}
    
    
    @Override
    public synchronized void doStart(){
		logger.info("doStart()=>start inbox service "+ getBeanName());
		
		redisPoolContainer = Rapidbackend.getCore().getRedisClientPoolContainer();
		for(RedisPoolConfig redisPoolConfig:redisPoolConfigs){
		    redisPoolContainer.addRedisClientPool(redisPoolConfig);
		}
		if(userStatusService!=null){
		    try{
		        userStatusService.tryToStart();
		    }catch(Exception e){
		        throw new BackendRuntimeException(BackendRuntimeException.InternalServerError,"error init userstatus service",e);
		    }
		}
		
		logger.info("doStart()<=start inbox service");
    }
	
	
	
    @Override
    public void doStop() throws Exception{
        logger.info("doStop()=>stop inbox service "+ getBeanName());
        if(isUserStatusServiceReady()){
            userStatusService.trytoStop();
            //userStatusCachePersistentWriter.stopWriteTask();
        }
        
    }
	
    public boolean isUserStatusServiceReady(){
        return userStatusService!=null && userStatusService.isRunning();
    }
    
    /**
     * Pull feed ids from the "hot person's feed" table, save 'push' efforts<br>
     * I believe twitter doesn't use push model on lady gaga's tweets<br>
     * TODO need to implement this with a calculating algorithm which is based on performance test<br>
     * Will implement it in next release, for now only push mode
     * @author chiqiu
     *
     */
    public static class DBScatterGatherInboxHandler{
    	
    }
    
    /**
     * 
     * @param inboxRedisTargetName
     * @param userId
     * @return
     * @throws InterruptedException
     */
    @Deprecated
    public Set<Tuple> getAllFeeds(String inboxRedisTargetName,int userId) throws InterruptedException{
        RedisClient redisClient = redisPoolContainer.borrowClient(inboxRedisTargetName);
        Set<Tuple> feeds = redisClient.getJedis().zrangeWithScores(userId+"", 0, inboxConfig.getAllowedFeedItemIndexRangeEnd());
        redisPoolContainer.returnClient(redisClient, inboxRedisTargetName);
        return feeds;
    }
    
    
    /**
     * 
     * @param redisClient
     * @param inboxKey the inbox key identified by the user's id
     * @return
     */
    @Deprecated
    public void initMetadata(RedisClient redisClient,String inboxKey){
        /*
    	int userId = getUserIdByInboxKey(inboxKey);
    	Tuple lastread = RedisInbox.createLastReadItem(0L);// set read time to earlier time
    	redisClient.getJedis().zadd(inboxKey, lastread.getScore(), lastread.getElement());
    	userStatusCache.markAsInitialized(userId);*/
    }
    /**
     * map user id to inbox key
     * @param userId
     * @return
     */
    public String getRedisInboxKeyByUserId(int userId){
    	return userId + "";
    }
    /**
     * map inbox key to user id
     * @param inboxKey
     * @return
     */
    public int getUserIdByInboxKey(String inboxKey){
    	return Integer.parseInt(inboxKey);
    }
    /**
     * check if user's inbox is created and valid.
     * The inbox is valid  if 1. user marked online in user online cache,2.inbox key exists in redis cache 
     * @param userId
     * @return
     */
    public boolean isUserInboxReady(Integer userId) throws InterruptedException{
        if(isUserStatusServiceReady()){
            return userStatusService.getUserStatusCache().isUserOnline(userId)&& inboxCache.exists(userId.toString(), userId);
        }else {
            return inboxCache.exists(userId.toString(), userId);
        }
        
    }
    
    protected boolean pushAllways = false;
        
    public boolean isPushAllways() {
        return pushAllways;
    }
    public void setPushAllways(boolean pushAllways) {
        this.pushAllways = pushAllways;
    }
    /**
     * TODO exception handling should be added to it
     * 
     * For now we keep the reserved item in the zset inbox, because we might need redis to hold user inboxes for a while as a status of the user
     * .In the future, if we don't need redis to hold an empty inbox, the reserved item should be removed. We may switch to list inbox too.
     * @param userId
     * @throws Exception
     */
    public void createInboxFromDb(Integer userId) throws InterruptedException{
        List<FollowabeFeedBase> inboxFeeds = getInboxFeeds(userId);
        // create the redis inbox
        initEmptyInbox(userId);// add an reserved tuple(Long.maxvalue,'reserved') into the zset to make sure feed will be pushed into that inbox
        if(inboxFeeds.size()>0){
            HashMap<Double, String> members = createInboxMembers(inboxFeeds);
            inboxCache.zaddMultiPopSmallest(userId.toString(), members, userId, getInboxSize());
        }
        expireUserAndInbox(userId,getInboxExpireTime());
    }
    
    public HashMap<Double, String> createInboxMembers(List<FollowabeFeedBase> feeds){
        HashMap<Double, String> members = new HashMap<Double, String>();
        for(FollowabeFeedBase f : feeds){
            FeedEssential feedEssential = new FeedEssential(f);
            Tuple tuple = feedEssential.createFeedTuple();
            String member = tuple.getElement();
            Double score = tuple.getScore();
            members.put(score, member);
        }
        return members;
    }
        
    public int getInboxSize(){
        return inboxConfig.getInboxSize();
    }
    /**
     * 
     * @param userId
     * @param ttl time to live
     */
    public void expireUserAndInbox(Integer userId, int ttl) throws InterruptedException{
        inboxCache.expire(userId.toString(), userId, ttl);
        if(isUserStatusServiceReady()){
            userStatusService.getUserStatusCache().expireOnlineUser(userId, ttl*1000l);
        }
        
    }
    
    protected static Double maxTimeStamp = new Long(Long.MAX_VALUE).doubleValue();
    protected static String reservedEmptyMember = "reservedEmptyMember";
    /**
     * create an empty user inbox in case there is no feed generated by the user and the followables he follows
     * @param userid
     * @throws InterruptedException
     */
    public void initEmptyInbox(Integer userid) throws InterruptedException{
        inboxCache.zadd(userid.toString(), maxTimeStamp, reservedEmptyMember, userid);
    }
    
    protected String genQueryFeedIdByIdsSql(int[] ids){
        if(ids==null || ids.length==0){
            return null;
        }
        String joinedString = ConversionUtils.join(ids, ',');
        StringBuffer sb = new StringBuffer();
        sb.append("select feedId,followableId,created from ").append(feedDao.getTableName()).append(" where followableId in (");
        sb.append(joinedString);
        sb.append(")");
        sb.append(" order by created desc limit 0,").
        append(getInboxSize()).
        append(";");
        return sb.toString();
    }
    /**
     * select inbox feeds from database
     * @param userId
     * @return
     * @throws InterruptedException
     */
    protected List<FollowabeFeedBase> getInboxFeeds(Integer userId) throws InterruptedException{
        List<Integer> followables = subscriptionService.getFollowedFollowableIdsFromDb(userId);
        List<FollowabeFeedBase> result = new ArrayList<FollowabeFeedBase>();
        if(followables!=null&&followables.size()>0){
            int[] ids = ConversionUtils.integerCollectionToIntArray(followables);
            result = feedDao.selectListBySql(genQueryFeedIdByIdsSql(ids), FollowabeFeedBase.class);
        }
        return result;
    }
    
    public Set<String> getFeedEssentialsFromRedisInbox(Integer userId,int start,int pageSize) throws InterruptedException{
        int actualStart = start+1;// because the reserved item is always there
        logger.debug("getFeedEssentialsFromRedisInbox start "+System.currentTimeMillis());
        logger.debug("getFeedEssentialsFromRedisInbox "+userId);
        Set<String> feedEssentials = inboxCache.zrevrange(userId.toString(), userId, actualStart, actualStart+pageSize);
        logger.debug("getFeedEssentialsFromRedisInbox size "+feedEssentials.size());
        logger.debug("getFeedEssentialsFromRedisInbox end "+System.currentTimeMillis());
        return feedEssentials;
    }
    
    public int[] getFeedIdsFromInbox(Integer userId,int start,int pageSize) throws InterruptedException{
        Set<String> feedEssentials = getFeedEssentialsFromRedisInbox(userId, start, pageSize);
        List<Integer> intList = new ArrayList<Integer>();
        for(String feedEssential:feedEssentials){
            FeedEssential fe = new FeedEssential(feedEssential);
            intList.add(Integer.valueOf(fe.getFeedId()));
        }
        return ConversionUtils.integerCollectionToIntArray(intList);
    }
    
    /**
     * return how many
     * @param userId
     * @param unixTimestamp
     * @return
     * @throws InterruptedException
     */
    public long getFeedCountsAfterTimestamp(Integer userId,Long unixTimestamp) throws InterruptedException{
        return inboxCache.zcount(userId.toString(), unixTimestamp+1, Long.MAX_VALUE-1, userId);
    }
    
    /**
     * return all online followers
     * @param followerIds followerIds selected from database
     * @return null if the status service is not ready
     */
    public List<Integer> getOnlineFollowers(int[] followerIds){
        if(isUserStatusServiceReady()){
            List<Integer> result = new ArrayList<Integer>();
            for(int f: followerIds){
                if(userStatusService.getUserStatusCache().isUserOnline(f)){
                    result.add(f);
                }
            }
            return result;
        }else {
            return null;
        }
        
    }
    
    /*
    public static class ReceivingMode{
        protected boolean receiveOnline;
        protected boolean receiveAlways;
        
        public boolean isReceiveOnline() {
            return receiveOnline;
        }
        public void setReceiveOnline(boolean receiveOnline) {
            this.receiveOnline = receiveOnline;
        }
        public boolean isReceiveAlways() {
            return receiveAlways;
        }
        public void setReceiveAlways(boolean receiveAlways) {
            this.receiveAlways = receiveAlways;
        }
        
    }*/
}
