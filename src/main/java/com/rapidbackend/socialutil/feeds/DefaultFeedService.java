package com.rapidbackend.socialutil.feeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.util.Assert;

import redis.clients.jedis.Tuple;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.redisqueue.RedisQueueService;
import com.rapidbackend.socialutil.dao.DataAccessException;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.socialutil.dao.FeedContentDao;
import com.rapidbackend.socialutil.dao.FeedDao;
import com.rapidbackend.socialutil.dao.UserDao;
import com.rapidbackend.socialutil.feeds.service.FeedService;
import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.socialutil.model.extension.FeedEntity;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.FollowabeFeedBase;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.general.ConversionUtils;
/**
 * Feedservice is called when user send a feed.<BR>
 * The post process is asyn, user post a feed returned immidiately after the
 * feed is saved into a traditional database.<BR>
 * The push and pull behavior is processed by backend threads.
 * @author chiqiu
 *
 */
public class DefaultFeedService extends AppContextAware implements FeedService{
    private Logger logger = LoggerFactory.getLogger(DefaultFeedService.class);
	private FeedDao feedDao;
	private UserDao userDao;
	private FeedContentDao feedContentDao;
	
	private RedisFeedCounterService redisFeedCounterService;
	private RedisQueueService postFeedQueueService;
	
    private String followableDomain;
	
	public RedisQueueService getPostFeedQueueService() {
        return postFeedQueueService;
    }
    public void setPostFeedQueueService(RedisQueueService postFeedQueueService) {
        this.postFeedQueueService = postFeedQueueService;
    }
    public UserDao getUserDao() {
		return userDao;
	}
    
	@Required
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	public FeedDao getFeedDao() {
        return feedDao;
    }
	@Required
    public void setFeedDao(FeedDao feedDao) {
        this.feedDao = feedDao;
    }
	public FeedContentDao getFeedContentDao() {
		return feedContentDao;
	}
	@Required
	public void setFeedContentDao(FeedContentDao feedContentDao) {
		this.feedContentDao = feedContentDao;
	}
	@Override
	public String getFollowableDomain() {
		return followableDomain;
	}
	@Required
	public void setFollowableDomain(String followableDomain) {
		this.followableDomain = followableDomain;
	}
	@Override
	public FeedContentBase updateFeed(HashMap<?, ?> updateValues,Integer id) throws FeedException{
	    HashMap<String,Integer> queryParam = new HashMap<String, Integer>();
        queryParam.put(ParamNameUtil.ID, id);
        
        List records = feedContentDao.selectListByColumns(queryParam,feedContentDao.getModelClass());
        
        if(records.size() == 0){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"records doesn't exists, nothing to update"); 
        }
        
        int res = feedContentDao.updateRecordByColumns(queryParam, updateValues, feedContentDao.getModelClass());
        
        if(res==0){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"no DB records updated, the id you input maybe wrong");
        }
        
        FeedContentBase record =  (FeedContentBase)records.get(0);
        record = (FeedContentBase)feedContentDao.selectById(record.getId(), feedContentDao.getModelClass());// read updated result
        return record;
	}
	@Override
	public Class<?> getFeedContentClass(){
	    return getFeedContentDao().getModelClass();
	}
	@Override
    public FeedContentBase postFeed(FeedContentBase feed,Integer userId,Integer followableId) throws FeedException{
    	/**
    	 * first insert the feedcontent into the feedcontent table and get the feedcontent id
    	 */
    	int feedId;
    	try {
    		feedId = feedContentDao.storeNewModelBean(feed);
		} catch (DataAccessException e) {
			logger.error("error during creating feedcontent",e);
			throw e;
		}
    	FollowabeFeedBase followabeFeedBase = new FollowabeFeedBase();
    	followabeFeedBase.setFeedId(feedId);
    	followabeFeedBase.setFollowableId(followableId);
    	try {
			feedDao.storeNewModelBean(followabeFeedBase);
		} catch (DataAccessException e) {
			logger.error("error during creating feed",e);
			throw e;
		}
    	feed.setId(feedId);
    	try {
			pushToInbox(feed,followableId);
		} catch (Exception e) {
			throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error pushing to inboxes "+userId+','+followableId,e);
		}
    	
    	return feed;
    }
	
	@Override
	public FeedContentBase deleteFeed(Integer feedId,Integer userId,Integer followableId) throws FeedException{
	    try {
	        FeedContentBase feed = (FeedContentBase)feedContentDao.selectById(feedId, feedContentDao.getModelClass());
	        
	        feedContentDao.deleteModelById(feedId);
	        HashMap<String, Integer> param = new HashMap<String, Integer>();
	        param.put(ParamNameUtil.FEED_ID, feedId);
	        int num = feedDao.deleteModelByParam(param);
	        if(num == 1){
	            return feed;
	        }else {
	            throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"feed not deleted: "+feedId+','+userId+','+followableId);
            }
	        
        } catch (Exception e) {
            throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error delete feed "+feedId+','+userId+','+followableId,e);
        }
	}
	
	/**
     * 
     * @param repostToFeedId
     * @param userId
     * @param followableId
     * @param content
     * @return
     * @throws Exception
     */
    @Override
    public FeedContentBase repost(int repostToFeedId,Integer userId,Integer followableId,String content,Class<? extends FeedContentBase> feedContentClass) throws FeedException{
        FeedContentBase toRepost = feedContentDao.selectById(repostToFeedId,feedContentClass);// we select the orginal post first
        //we now use an aggressive way to repost feeds, we copy all the info into the new feed
        if(!StringUtils.isEmpty(content)){
            toRepost.setContent(content); //update contents
        }
        
        toRepost.setId(null);
        toRepost.setSeedFeedId(repostToFeedId);
        toRepost.setRepostToFeedId(repostToFeedId);
        toRepost.setUserId(null);
        DbRecord.initTime(toRepost);
        
        toRepost.setUserId(userId);
        
        int feedId;
        try {
            feedId = feedContentDao.storeNewModelBean(toRepost);
        } catch (DuplicateKeyException e) {
            // throw a post error if user try to repost the same post
            String errmsg = e.toString();
            if(errmsg.indexOf("repostUnique")>0){
                throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"User cannot repost one post twice",e);
            }else {
                throw e;
            }
        }catch (DataAccessException e) {
            logger.error("error during creating feedcontent",e);
            throw e;
        }
        FollowabeFeedBase followabeFeedBase = new FollowabeFeedBase();
        followabeFeedBase.setFeedId(feedId);
        followabeFeedBase.setFollowableId(followableId);
        followabeFeedBase.setRepostToFeedId(repostToFeedId);
        try {
            feedDao.storeNewModelBean(followabeFeedBase);
        } catch (DataAccessException e) {
            logger.error("error during creating feed",e);
            throw e;
        }
        toRepost.setId(feedId);
        pushToInbox(toRepost,followableId);// push to followers' inbox
        
        return toRepost;
    }
    
    /**
     * 
     * @param feed
     * @return
     * @throws FeedException
     */
    public boolean pushToInbox(FeedContentBase feed, Integer followableId) throws FeedException{
    	boolean res = false;
    	try{
    		String feedEssential = new FeedEssential(feed, followableId).toString();
    		if(!postFeedQueueService.isRunning()){
    		    postFeedQueueService.tryToStart();
    		}
    		postFeedQueueService.getRedisQueue().addItem(feedEssential);
    	}catch(Exception e){
    		logger.error("error during push into postFeedQueue" , e);
    		throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"",e);
    	}
    	return res;
    }
    
    public static class FeedEssential{
        private String followableId;
        private String createdTime;
        private String feedId;
        public String getFollowableId() {
            return followableId;
        }
        public void setFollowableId(String followableId) {
            this.followableId = followableId;
        }
        public String getCreatedTime() {
            return createdTime;
        }
        public void setCreatedTime(String createdTime) {
            this.createdTime = createdTime;
        }
        public String getFeedId() {
            return feedId;
        }
        public void setFeedId(String feedId) {
            this.feedId = feedId;
        }
        
        public FeedEssential(){
        }
        public static int feedEssentialPartNumber = 3;
        protected static String[] convertFeedEssentialArray(String feedEssential){
            StringTokenizer st = new StringTokenizer(feedEssential, ",");
            String[] info = new String[feedEssentialPartNumber];
            int i= 0 ;
            while (st.hasMoreTokens()) {
                info[i]=st.nextToken();
                i++;
            }
            return info;
        }
        
        public FeedEssential(String feedEssential){
            String[] info = convertFeedEssentialArray(feedEssential);
            String followableId = info[0];
            String feedId = info[1];
            String createdTime = info[2];
            setFeedId(feedId);
            setCreatedTime(createdTime);
            setFollowableId(followableId);
            //verifyData(); sime times can't call this because createdtime can be null if we constructs from redis inbox elements
        }
        
        public FeedEssential(FollowabeFeedBase feed){
            if(null == feed.getFeedId()){
                throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"feed id is null");
            }
            if(null == feed.getFollowableId()){
                throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"FollowableId is null");
            }
            if(null == feed.getCreated()){
                throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"Created date is null");
            }
            setFeedId(feed.getFeedId().toString());
            setFollowableId(feed.getFollowableId().toString());
            setCreatedTime(feed.getCreated().toString());
        }
        
        public FeedEssential(FeedContentBase feed,Integer followableId){
            if(feed.getCreated()==null || feed.getId()==null || followableId == null){
                throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error create feed string to push:"+feed.getUserId()+":"+feed.getId()+ ":"+ followableId) ;
            }
            setFeedId(feed.getId().toString());
            setFollowableId(followableId.toString());
            setCreatedTime(feed.getCreated().toString());
        }
        
        
        public Tuple createFeedTuple() {
            verifyData();
            
            StringBuffer sb = new StringBuffer();
            sb.append(getFollowableId()).append(",");
            sb.append(getFeedId());
            String followableIdFeedId = sb.toString();
            
            Tuple t = new Tuple(followableIdFeedId, Double.valueOf(getCreatedTime()));
            return t;
        }
        @Override
        public String toString(){
            StringBuffer sb = new StringBuffer();
            sb.append(getFollowableId()).append(',');
            sb.append(getFeedId()).append(',');
            sb.append(getCreatedTime());
            return sb.toString();
        }
        
        private void verifyData(){
            if(null == getFeedId()){
                throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"feed id is null");
            }
            if(null == getFollowableId()){
                throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"FollowableId is null");
            }
            if(null == getCreatedTime()){
                throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"Created date is null");
            }
        }
        
    }
    
    /**
     * parse the feed contents, return all user screenames,
     * @param feedContent
     * @return
     */
    public List<String> grepUserNameFromFeedContent(String feedContent){
    	//TODO
    	return null;
    }
    
    /**
     * serialize the feed to a small string
     * use @FeedEssential toString() instead
     * @param feed
     * @return
     */
    @Deprecated
    public static String createFeedEssentialString(FeedContentBase feed,Integer followableId) throws BackendRuntimeException{
    	if(feed.getUserId()==null || feed.getId()==null || followableId == null){
    		throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error create feed string to push:"+feed.getUserId()+":"+feed.getId()+ ":"+ followableId) ;
    	}
    	FeedEssential feedEssential = new FeedEssential(feed,followableId);
    	return feedEssential.toString();
    }
    
    
    
    /*
    @Deprecated
    public static int getFollowedIndexInFeedEssential(FollowType followType){
        int result =0;
        switch (followType) {
        case User:
            result = 0;
            break;
        default:
            result = 0;
            break;
        }
        return result;
    } */
    /**
     * create feed from the feed string
     * @param feedEssential
     * @return
     */
    public static FeedEssential createFeedFromEssentialString(String feedEssential) throws BackendRuntimeException{
        FeedEssential result = null;
    	try{
    	    
        	FeedEssential fe = new FeedEssential(feedEssential);
        	result = fe;
    	
    	}catch(Exception e){
    		throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"createfeedessanial from" + feedEssential,e);
    	}
    	return result;
    }
    /**
     * convert a feed into a jedis 'tuple'
     * @param feed
     * @return
     */
    @Deprecated
    public static Tuple createFeedTuple(FeedEssential feedEssential) {
        Assert.notNull(feedEssential.getFeedId());
        Assert.notNull(feedEssential.getCreatedTime());
        StringBuffer sb = new StringBuffer();
        sb.append(feedEssential.getFollowableId()).append(",");
        sb.append(feedEssential.getFeedId());
        String followableIdFeedId = sb.toString();
        
        Tuple t = new Tuple(followableIdFeedId, Double.valueOf(feedEssential.getCreatedTime()));
        return t;
    }
    /**
     * return feed contexts, if a feed does not exist, an empty feed context will be returned
     * The contexts will be return in sorted format, based on feed ids
     * @param feedIds
     * @return
     */
    public HashMap<Integer, FeedContext> getFeedContexts(int[] feedIds){
        HashMap<Integer, FeedContext> feedContexts = new HashMap<Integer, FeedContext>();
        if(feedIds.length>0){
            for(Integer id: feedIds){
                FeedContext context = new FeedContext(id);
                feedContexts.put(id, context);
            }
            List<FeedContext> existingFeedsContexts = getFeedContextsWithoutRootContext(feedIds);
            
            HashMap<Integer, FeedContext> hasRootFeedIds = new HashMap<Integer, FeedContext>();
            for(FeedContext feedContext:existingFeedsContexts){
                if(feedContext.feed().getSeedFeedId()!=0){
                    hasRootFeedIds.put(feedContext.feed().getSeedFeedId(), feedContext);
                }
            }
            if(hasRootFeedIds.size()>0){
                Set<Integer> rootFeedIds = hasRootFeedIds.keySet();
                int[] rootIds = ConversionUtils.integerCollectionToIntArray(rootFeedIds);
                List<FeedContext> rootFeedContexts = getFeedContextsWithoutRootContext(rootIds);
                for(FeedContext rootFeedContext : rootFeedContexts){
                    Integer rootFeedId = rootFeedContext.seedFeed().getId();
                    FeedContext feedContext = hasRootFeedIds.get(rootFeedId);
                }
            }
            for(FeedContext context:existingFeedsContexts){
                feedContexts.put(context.feed().getId(), context);
            }
        }
        return feedContexts;
    }
    
    public void getFeedRootContexts(HashMap<Integer, FeedContext> feedContexts) {
        
    }
    
    /**
     * 
     * @param feedContexts final feed contexts results from db and cache
     * @return
     * @throws Exception
     */
    public List<FeedEntity> getFeedEntities(HashMap<Integer, FeedContext> feedContexts) throws Exception{
        Set<Integer> keys = feedContexts.keySet();
        int[] feedIds = ConversionUtils.integerCollectionToIntArray(keys);
        int[] repostCounts = redisFeedCounterService.getRepostCounts(feedIds);
        if (feedIds.length!=repostCounts.length) {
            throw new FeedException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error processing feeds. repost counter array length is shorter than feedId array length");
        }
        List<FeedEntity> res = new ArrayList<FeedEntity>();
        
        for(int j=0;j<feedIds.length;j++){
            Integer i = feedIds[j];
            FeedContext feedContext = feedContexts.get(i);
            int repostCount = repostCounts[j];
            FeedEntity feedEntity = new FeedEntity();
            feedEntity.setFeedContext(feedContext);
            feedEntity.setRepostCount(repostCount);
            res.add(feedEntity);
        }
        return res;
    }
    /**
     * get contexts, only return contexts which contains a valid feed.
     * Do not get users in this function
     * @param feedIds
     * @return
     */
    public List<FeedContext> getFeedContextsWithoutRootContext(int[] feedIds){
        List<FeedContentBase> feeds = feedDao.selectByIds(feedIds, FeedContentBase.class);
        List<FeedContext> result = new ArrayList<FeedContext>();
        if(feeds.size()>0){
            for(FeedContentBase f:feeds){
                FeedContext context = new FeedContext(f.getId());
                context.setFeed(f);
                result.add(context);
            }
        }
        return result;
    }
    
    public void getUsers(int[] userIds){
        /*
        int[] usersIdArray = ConversionUtils.integerCollectionToIntArray(userIds);
        List<User> users = userDao.selectByIds(usersIdArray, User.class);
        if (users.size()>0) {
            
        }
        for(User u:users){
            FeedContext context = contexts.get(u.getId());
            if(context!=null){
                context.setUser(u);
            }
        }*/
    }
    
    /**
     * TODO
     * @param repostTo
     * @param repost
     * @return
     */
    public boolean repostAndReply(FeedContentBase repostTo,FeedContentBase repost){
        boolean res = false;
        return res;
    }
    /**
     * TODO
     * @param replyTo
     * @param reply
     * @return
     */
    public boolean replyOnly(FeedContentBase replyTo,FeedContentBase reply){
        boolean res = false;
        return res;
    }
    
}
