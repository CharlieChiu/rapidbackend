package com.rapidbackend.socialutil.user;

import java.util.BitSet;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.socialutil.feeds.config.UserStatusCacheConfig;
import com.rapidbackend.util.io.ObjectIoUtil;
import com.rapidbackend.util.io.PeriodicWriter;
import com.rapidbackend.util.io.PeriodicWriter.OutputFormat;
import com.rapidbackend.util.io.PeriodicWriter.PersistantSourceProvider;

public class UserStatusService extends ClusterableService{
    protected Logger logger = LoggerFactory.getLogger(UserStatusService.class);
    protected PeriodicWriter userStatusCachePersistentWriter;
    protected ScheduledExecutorService persistentScheduler;
    protected UserStatusCache userStatusCache = null;
    protected UserStatusCacheConfig userStatusCacheConfig;
    
    
    public UserStatusCacheConfig getUserStatusCacheConfig() {
        return userStatusCacheConfig;
    }
    @Required
    public void setUserStatusCacheConfig(UserStatusCacheConfig userStatusCacheConfig) {
        this.userStatusCacheConfig = userStatusCacheConfig;
    }
    
    public UserStatusCache getUserStatusCache() {
        return userStatusCache;
    }
    
    @Override
    public void doStart() throws Exception{
        /*
         * initialize metadata cache
         */
      //FeedInboxMetadataCacheConfig metadataCacheConfig = (FeedInboxMetadataCacheConfig)getApplicationContext().getBean("FeedInboxMetadataCacheConfig");
        String cacheFile = userStatusCacheConfig.getJournalFileName();
        long cachePersistInterval = userStatusCacheConfig.getPersistInterval();
        
        try{
            Object deSerialized = ObjectIoUtil.loadObj(cacheFile);
            if(deSerialized instanceof UserStatusCache){
                UserStatusCache deserializedCache = (UserStatusCache)deSerialized;
                BitSet bitSet = deserializedCache.getInitializationStatus();
                Assert.notNull(bitSet);//TODO add more check if null create a new one
                int cardinality = bitSet.cardinality();
                logger.info("read inbox MetadataCache from disk, "+cardinality+" inboxes has been stored");
                userStatusCache = deserializedCache;
            }else{
                throw new ClassNotFoundException("error in deserialize inboxMetadataCache");
            }
        }catch(Exception e){
            logger.error("error initializing inbox metadata cache, this is normal when you run the app for the first time",e);
        }
        if(null == userStatusCache
                ||userStatusCache.getOnlineUsers()==null
                ||userStatusCache.getUserInboxExpireTime() == null){
            userStatusCache = new UserStatusCache();
        }
        persistentScheduler = Rapidbackend.getCore().getThreadManager().newScheduledThreadPool(null, "inboxMetadataPersistentScheduler", 1);
        userStatusCachePersistentWriter = new PeriodicWriter(cacheFile, persistentScheduler,cachePersistInterval,new CacheObjectProvider(),OutputFormat.JavaObject);
        userStatusCachePersistentWriter.startWriteTask();
        /*end of init metadata cache*/
    }
    
    @Override
    public void doStop() throws Exception{
        if(userStatusCachePersistentWriter!=null)
        userStatusCachePersistentWriter.stopWriteTask();
    }
    /**
     * provide metadata cache to persistant writer
     * @author chiqiu
     *
     */
    public class CacheObjectProvider extends PersistantSourceProvider{
        @Override
        public Object provideSource(){
            return userStatusCache;
        }
    }
}
