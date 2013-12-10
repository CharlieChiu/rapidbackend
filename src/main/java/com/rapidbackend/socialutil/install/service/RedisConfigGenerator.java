package com.rapidbackend.socialutil.install.service;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.install.dbinstall.FollowableConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * this class generates the redis instance configurations for all the followables.
 * 
 * Note: For now the default durability level is snapshot every 10 minutes and at least one key changes
 * If you want different durability levels please change the configuration files according to http://redis.io/topics/persistence
 * Some type of the instances doesn't needs snapshoting, you may snapshot too
 * @author chiqiu
 *
 */
public class RedisConfigGenerator {
    
    private Integer startPortNum = 21111;
    
    public static String redisBeanConfigFile = "src/main/resources/config/socialUtilRedisConf.xml";
    
    public static String DefaultConfigDir = "src/main/resources/redis/";
    
    protected static String templateFoder = "src/main/resources/dbInstall/redis/template";
    
    protected static String beanTemplateFile  = "redisConfigBean.ftl";
    
    protected static String springRedisConfigTemplate = "springRedisConfig.ftl";
    
    protected Configuration configuration ;
    
    protected List<String> redisConfigBeans = new ArrayList<String>();
    
    protected List<RedisConfig> redisInstances = new ArrayList<RedisConfigGenerator.RedisConfig>();
    
    protected String DefaultRedisAddress = "127.0.0.1";
    
    List<FollowableConfig> followableConfigs;
    
    public RedisConfigGenerator() throws Exception{
        configuration =  new Configuration();
        configuration.setDirectoryForTemplateLoading(new File(templateFoder));
        try{
            String redisaddress = FileUtils.readFileToString(new File("redisAddress"));// for testing purpose
            if(!StringUtils.isEmpty(redisaddress)){
                DefaultRedisAddress = redisaddress;
            }
        }catch(IOException e){
        }
        
        DbConfigParser dbConfigParser = new DbConfigParser();
        followableConfigs = (List<FollowableConfig>)dbConfigParser.parseSetting().get(DbConfigParser.FollowableConfigVariable);
    }
    
    /**
     * generation rules:
     * 1. create 1 inbox instance for each followable type
     * 2. create 1 counter instance for each followable type
     * 3. create 1 object cache for all models
     * 4. create 1 session instance for all the users
     * 
     * port numbers starts from 21111, and will increWase by one for each instance automatically
     * 
     * there should be a generate log create by this generator , which describes the instances' usage and port numbers.
     * 
     * this class creates redis conf files , a start redis script and a spring redis instance registry bean
     * @throws Exception
     */
    public void createRedisConfigs() throws Exception{
        genSessionInstanceConfig();
        genObjectCacheInstanceConfig();
        genPostQueueInstanceConfig();
        genMetaDataCounterConfig();
        genInboxConfig();
        
        genRedisStartScript();
        
        genRedisConfigBeans();
        
    }
    
    private void genRedisConfigBeans() throws Exception{
        Template template = configuration.getTemplate(springRedisConfigTemplate);
        StringWriter writer = new StringWriter();
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("redisConfigBeans", redisConfigBeans);
        template.process(variables, writer);
        FileUtils.write(new File(redisBeanConfigFile), writer.toString());
    }
    
    private void genRedisStartScript() throws Exception{
        List<String> lines = new ArrayList<String>();
        for(RedisConfig redisConfig:redisInstances){
            String line = "redis-server "+DefaultConfigDir+ redisConfig.targetName+".conf"+" &";
            lines.add(line);
        }
        FileUtils.writeLines(new File(DefaultConfigDir+"startRedis.sh"), lines,"\n");
    }    
    /**
     * the default configuration uses only one redis instance as session store
     * @throws Exception
     */
    private void genSessionInstanceConfig() throws Exception{
        RedisConfig sessionStore = new RedisConfig(getPortNum(), DefaultRedisAddress, "sessionStore", 2);
        redisInstances.add(sessionStore);
        createRedisConfigFile(sessionStore.getTargetName(), sessionStore.port);
        redisConfigBeans.add(createRedisBean(sessionStore,"RedisSessionStore"));
    }
    /**
     * the default configuration uses one redis instance for all models, the default configuration only generates
     * feedcache,usercache for all the followables
     * @throws Exception
     */
    private void genObjectCacheInstanceConfig() throws Exception{
        RedisConfig modelCahe = new RedisConfig(getPortNum(), DefaultRedisAddress, "modelCache", 1);
        createRedisConfigFile(modelCahe.getTargetName(), modelCahe.port);
        redisInstances.add(modelCahe);
        
        RedisConfig userCahe = new RedisConfig(modelCahe.port, DefaultRedisAddress, "userCache", 1);
        redisConfigBeans.add(createRedisBean(userCahe,"RedisUserCahce"));
        
        RedisConfig feedCache = new RedisConfig(modelCahe.port, DefaultRedisAddress, "feedCache", 1);
        redisConfigBeans.add(createRedisBean(feedCache,"RedisFeedCahce"));
    }
    /**
     * the default configuration uses one redis instance across multiple followables
     * @throws Exception
     */
    private void genPostQueueInstanceConfig() throws Exception{
        RedisConfig queue = new RedisConfig(getPortNum(), DefaultRedisAddress, "queue", 1);
        createRedisConfigFile(queue.getTargetName(), queue.port);
        redisInstances.add(queue);
        
        for(FollowableConfig f: followableConfigs){
            String followableName = f.getName().toLowerCase();
            String redisTargetName = StringUtils.capitalize(followableName)+"FeedPostQueueDB";
            RedisConfig redisConfig = new RedisConfig(queue.port, DefaultRedisAddress, redisTargetName, 5);
            redisConfigBeans.add(createRedisBean(redisConfig,StringUtils.capitalize(redisTargetName)));
        }
    }
    
    private void genInboxConfig() throws Exception{
        for(FollowableConfig f: followableConfigs){
            String targetName = StringUtils.capitalize(f.getName())+"InboxDB";
            String instanceName = targetName;
            String beanId = instanceName;
            
            RedisConfig redisConfig = new RedisConfig(getPortNum(), DefaultRedisAddress, targetName, 2);
            createRedisConfigFile(instanceName, redisConfig.port);
            redisInstances.add(redisConfig);
            redisConfigBeans.add(createRedisBean(redisConfig, beanId));
            
        }
    }
    
    private void genMetaDataCounterConfig() throws Exception{
        RedisConfig metadataCounter = new RedisConfig(getPortNum(), DefaultRedisAddress, "metadataCounter", 1);
        createRedisConfigFile(metadataCounter.getTargetName(), metadataCounter.port);
        redisInstances.add(metadataCounter);
        
        redisConfigBeans.add(createRedisBean(metadataCounter,"MetadataCounter"));
    }
        
    private void createRedisConfigFile(String instanceName,Integer portNum) throws Exception{
        String configfileName = DefaultConfigDir+instanceName+".conf";
        List<String> configs = new ArrayList<String>();
        configs.add(configLine("port", portNum) );
        configs.add(configLine("dbfilename", instanceName+".rdb") );
        configs.add(configLine("save", "600 1") );
        configs.add(configLine("logfile",instanceName + ".log"));
        FileUtils.writeLines(new File(configfileName), configs);
    }
    /**
     * this method should only get called when creating config for a physical redis instance
     * @return
     */
    private int getPortNum(){
        return startPortNum++;
    }
    
    private String configLine(String attr,Object value){
        return attr+" "+value.toString();
    }
    
    private String createRedisBean(RedisConfig redisConfig, String beanId) throws Exception{
        Template template = configuration.getTemplate(beanTemplateFile);
        StringWriter writer = new StringWriter();
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put("redisConfig", redisConfig);
        variables.put("beanId", beanId);
        template.process(variables, writer);
        return writer.toString();
    }
    
    public static class RedisConfig{
        private Integer port;
        private String hostAddress;
        private String targetName;
        private Integer poolCapacity;
        
        public String getPort() {
            return port.toString();// freemarker will print 21,111 if use integer
        }
        public void setPort(int port) {
            this.port = port;
        }
        public String getHostAddress() {
            return hostAddress;
        }
        public void setHostAddress(String hostAddress) {
            this.hostAddress = hostAddress;
        }
        public String getTargetName() {
            return targetName;
        }
        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }
        public int getPoolCapacity() {
            return poolCapacity;
        }
        public void setPoolCapacity(int poolCapacity) {
            this.poolCapacity = poolCapacity;
        }
        
        public RedisConfig(Integer port, String hostAddress, String targetName,
                Integer poolCapacity) {
            super();
            this.port = port;
            this.hostAddress = hostAddress;
            this.targetName = targetName;
            this.poolCapacity = poolCapacity;
        }
    }
}
