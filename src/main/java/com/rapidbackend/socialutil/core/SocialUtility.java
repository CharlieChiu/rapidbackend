package com.rapidbackend.socialutil.core;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.extension.Extension;
import com.rapidbackend.socialutil.core.cache.RedisInitializer;
import com.rapidbackend.socialutil.feeds.DefaultFeedService;
import com.rapidbackend.socialutil.feeds.InboxService;
import com.rapidbackend.socialutil.install.service.ServiceGenerator;
import com.rapidbackend.socialutil.install.service.ServiceRegistry;
import com.rapidbackend.socialutil.search.SearchService;
import com.rapidbackend.util.comm.redis.client.RedisClientPoolContainer;
import com.rapidbackend.util.comm.redis.client.RedisPoolConfig;
/**
 * Socialutility is an extension bean managed by Spring. Please check extensionRepository.js for description of this bean
 * @author chiqiu
 *
 */
public class SocialUtility extends Extension{
    Logger logger = LoggerFactory.getLogger(SocialUtility.class);
    
    
    public static String Security_Manager_Bean = "SecurityManager";
    public static String CredentialsMatcher_Bean = "credentialsMatcher";
        
    protected ServiceRegistry serviceRegistry;
    
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }
    @Required
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    /*
     * core services
     * 
     */
    protected DefaultFeedService feedService;
    protected InboxService inboxService;
    protected SearchService searchService;
    
    
    public DefaultFeedService getFeedService() {
        return feedService;
    }
    
    public void setFeedService(DefaultFeedService feedService) {
        this.feedService = feedService;
    }
    
    public InboxService getInboxService() {
        return inboxService;
    }
    public void setInboxService(InboxService inboxService) {
        this.inboxService = inboxService;
    }
        
    
    public SearchService getSearchService() {
        return searchService;
    }
    public void setSearchService(SearchService searchService) {
        this.searchService = searchService;
    }
    
    
    protected RedisInitializer redisInitializer;
    
    public RedisInitializer getRedisInitializer() {
        return redisInitializer;
    }

    public void setRedisInitializer(RedisInitializer redisInitializer) {
        this.redisInitializer = redisInitializer;
    }

    /**
     * init resources for all the child services
     * @return
     */
    @Override
    public void initServices(){
        try{
            
            initSessionStore();//TODO move session init into cacheInitializer????
            
            if(redisInitializer != null){
                redisInitializer.initCaches();
            }
            
            if(!inited){
                registerServices();
            }
            setInited(true);
            //TODO use redis config to config redis instances, see if we need to disable some handlers by config file
        }catch (Exception e) {
            logger.error("error during init core services",e);
            System.out.println("error during init core services :"+e.toString());
            System.exit(100);
        }
        
    }
    
    @Override
    public HashMap<String, String> getCommandMapping(){
        try {
            HashMap<String, String> crudCommandMap = ServiceGenerator.readCrudCommandMap();
            HashMap<String, String> serviceCommandMap = ServiceGenerator.readServiceCommandMap();
                                                
            HashMap<String, String> result = new HashMap<String, String>(crudCommandMap);
            for(String key:serviceCommandMap.keySet()){
                result.put(key, serviceCommandMap.get(key));
            }
                       
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("failed to get command mapping information for socialutil ",e);
        }
        
    }
    
    private void initSessionStore(){
        Rapidbackend rapidbackend = Rapidbackend.getCore();
        RedisClientPoolContainer redisClientPoolContainer = rapidbackend.getRedisClientPoolContainer();
        RedisPoolConfig sessionConfig = (RedisPoolConfig)getApplicationContext().getBean("RedisSessionStore");
        redisClientPoolContainer.addRedisClientPool(sessionConfig);
        
    }
    
    public void registerServices(){
        try {
            for(ClusterableService service : serviceRegistry.getServiceBeans()){
                service.tryToStart();
            }
        } catch (Exception e) {
            logger.error("error during registering services for socialutilty",e);
            logger.error("fail to start plugin socialutility");
            System.exit(1);
        }
    }
    
    public void stopServices(){
        try {
            for(ClusterableService service : serviceRegistry.getServiceBeans()){
                service.trytoStop();
            }
        } catch (Exception e) {
            logger.error("error during stop services for socialutilty",e);
        }
    }
    
    @Override
    protected void finalize()
            throws Throwable{
        stopServices();
    }
}
