package com.rapidbackend.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.context.AppContext;
import com.rapidbackend.core.request.RequestConfig;
import com.rapidbackend.extension.Extension;
import com.rapidbackend.extension.Extension4Test;
import com.rapidbackend.extension.ExtensionDescriptor;
import com.rapidbackend.extension.ExtensionRepository;
import com.rapidbackend.redisqueue.RedisQueueService;
import com.rapidbackend.socialutil.monitor.RapidbackendMBeanServer;
import com.rapidbackend.util.comm.redis.RedisService;
import com.rapidbackend.util.comm.redis.client.RedisClientPoolContainer;
import com.rapidbackend.util.io.JsonUtil;
import com.rapidbackend.util.thread.DefaultExecutorServiceManager;
import com.rapidbackend.webserver.netty.NettyHttpServer;

/**
 * Container for basic services like redis cache, kestrel and http web server.
 * User can use rapidbackend as a restful backend server without social services. 
 * @author chiqiu
 * TODO make all services plugable by spring
 */
public class Rapidbackend extends ServiceContainer{
    protected static Logger logger = LoggerFactory.getLogger(Rapidbackend.class);
    private static Rapidbackend core;
    protected DefaultExecutorServiceManager threadManager = new DefaultExecutorServiceManager();
    protected NettyHttpServer webserverService;
    protected RedisService redisService;
    protected RedisQueueService redisQueueService;
    protected ReentrantLock lock = new ReentrantLock();
    
    protected RedisClientPoolContainer redisClientPoolContainer = new RedisClientPoolContainer();
    
    protected ExtensionRepository extensionRepository;
    /**
     * a map used to store command mapping , used to map web request to backend command
     */
    protected HashMap<String, String> commandMap = new HashMap<String, String>();
    
    public HashMap<String, String> getCommandMap() {
        return commandMap;
    }
    
    protected String[] reservedContextFiles = {//TODO move to an json file
            "src/main/resources/config/jmxConfig.xml",
            "src/main/resources/config/httpComponentConf.xml"};
    public static String overridingTestContext = "src/test/resources/config/override.xml";
    /**
     * TODO remove mbean server to rapidbackend
     */
    protected  RapidbackendMBeanServer mbeanServer;
    
    public RedisClientPoolContainer getRedisClientPoolContainer() {
        return redisClientPoolContainer;
    }
    public void setRedisClientPoolContainer(
            RedisClientPoolContainer redisClientPoolContainer) {
        this.redisClientPoolContainer = redisClientPoolContainer;
    }
    public NettyHttpServer getWebserverService() {
        return webserverService;
    }
    public void setWebserverService(NettyHttpServer webserverService) {
        this.webserverService = webserverService;
    }
    @Deprecated
    public RedisService getRedisService() {
        return redisService;
    }
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }
    
    public RedisQueueService getRedisQueueService() {
        return redisQueueService;
    }
    public void setRedisQueueService(RedisQueueService redisQueueService) {
        this.redisQueueService = redisQueueService;
    }
    public DefaultExecutorServiceManager getThreadManager() {
        return threadManager;
    }
    public void setThreadManager(DefaultExecutorServiceManager threadManager) {
        this.threadManager = threadManager;
    }
    
    private Rapidbackend(){
        try {
            extensionRepository = JsonUtil.readObject(new File(ExtensionRepository.extensionConfig), 
                    new TypeReference<ExtensionRepository>() {
            });
            
            
        } catch (Exception e) {
            logger.error("error init contexts",e);
            System.exit(1);
        }
    }
    
    public static Rapidbackend getCore(){
        if(core==null){
            synchronized (Rapidbackend.class) {
                core = new Rapidbackend();
            }
        }
        return core;
    }
    @Deprecated
    public static String RedisServiceName = "redis";
    
    public static String WebserverServiceName = "webserver";
    
    public void init() {
        lock.lock();
        try {
            try {
                initContexts();
                this.mbeanServer = RapidbackendMBeanServer.getInstance();
                
                
                webserverService = (NettyHttpServer)getApplicationContext().getBean("WebServer");
                initClusterableService(webserverService,WebserverServiceName);
                
                initExtensions();
                
                registerInfoBeans();
                
                setInited(true);
                
            } catch (Exception e) {
                logger.error("error during init core services",e);
                System.out.println("error during init core services :"+e.toString());
                System.exit(100);
            }
        } finally{
            lock.unlock();
        }
    }
    
    private void registerInfoBeans(){
        if(mbeanServer.isEnableJmx()){
            try{
                // first of all we rigister the thread manager
                mbeanServer.register(getThreadManager());
            }catch(Exception registerException){
                logger.error("error registering mbean server",registerException);
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error in registerInfoBeans",registerException);
            }
        }
    }
    
    private void initContexts() throws IOException{
        String[] extensionContexts = getExtentionContexts();
        List<String> contextList = new ArrayList<String>();
        for(String context:reservedContextFiles){
            contextList.add(context);
        }
        
        if(null != extensionContexts && extensionContexts.length>0){
            for(String context:extensionContexts){
                contextList.add(context);
            }
        }
        if(areWeInTests()){
            Extension4Test extension4Test = Extension4Test.getInstance();
            Set<String> testContexts = extension4Test.getExtensionDescriptor().getSpringContextFiles();
            for(String c : testContexts){
                contextList.add(c);
            }
        }
        
        String[] contexts = contextList.toArray(new String[0]);
        AppContext.init(contexts);
        
        initRequestConfigs();
        
    }
    /**
     * 
     * @return true if we are in unit tests, this property should be set in the build script
     */
    public boolean areWeInTests(){
        boolean result = false;
        String testing = System.getProperty("testing");
        try {
            result = Boolean.parseBoolean(testing);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
    
    public Extension getExtension(String extensionName){
        return extensionRepository.getExtension(extensionName);
    }
    /**
     * scan all the ReqeustSchema beans in context files 
     * @throws IOException
     */
    private void initRequestConfigs() throws IOException{/*
        List<ExtensionDescriptor> extensionDescriptors = extensionRepository.getExtensionDescriptors();
        for(ExtensionDescriptor extensionDescriptor : extensionDescriptors){
            Set<String> requestConfigFiles = extensionDescriptor.getRequestContextFiles();
            if(requestConfigFiles!=null && requestConfigFiles.size()>0){
                String[] configFilePath = requestConfigFiles.toArray(new String[0]);
                RequestConfig.init(false, configFilePath);
            }else {
                logger.warn("request schema setting for "+extensionDescriptor.getExtentionName() +" is empty");
            }
        }*/
        
        RequestConfig.init();
    }
    
    private String[] getExtentionContexts() throws IOException{
        List<String> contexts = new ArrayList<String>();
        List<ExtensionDescriptor> extensionDescriptors = extensionRepository.getExtensionDescriptors();
        for(ExtensionDescriptor extensionDescriptor : extensionDescriptors){
            Set<String> extensionContexts = extensionDescriptor.getSpringContextFiles();
            for(String contextFile : extensionContexts){
                contexts.add(contextFile);
            }
        }
        return contexts.toArray(new String[0]);
    }
    
    private void initExtensions() throws IOException{
        List<ExtensionDescriptor> extensionDescriptors = extensionRepository.getExtensionDescriptors();
        for(ExtensionDescriptor extensionDescriptor : extensionDescriptors){
            String extensionBean = extensionDescriptor.getExtentionBean();
            Extension extension = (Extension)getApplicationContext().getBean(extensionBean);
            extension.setExtensionDescriptor(extensionDescriptor);
            extensionRepository.addExtension(extension);
            //extension.initCommandConfigs();
            extension.initServices();
            addExtensionCommandMapping(extension);
        }
        
        if(areWeInTests()){// init test extention command mapping
            Extension test = Extension4Test.getInstance();
            extensionRepository.addExtension(test);
            //extension.initCommandConfigs();
            test.initServices();
            addExtensionCommandMapping(test);
        }
    }
    
    
    
    private void addExtensionCommandMapping(Extension extension){
        HashMap<String, String> extensionCommandMapping = extension.getCommandMapping();
        Set<String> keys =  extensionCommandMapping.keySet();
        for(String k:keys){
            if(commandMap.containsKey(k)){
                throw new RuntimeException("error init extension "+extension.getClass()+",command already exists :"+k);
            }else {
                commandMap.put(k, extensionCommandMapping.get(k));
            }
        }
    }
    
    public static void main(String[] args){
        Rapidbackend rapidbackend = Rapidbackend.getCore();
        rapidbackend.init();
    }
}
