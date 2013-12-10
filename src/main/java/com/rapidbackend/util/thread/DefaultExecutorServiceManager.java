package com.rapidbackend.util.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.socialutil.monitor.SocialServiceMBean;

import java.util.*;
import java.util.concurrent.*;
/**
 * orginal written by apache camel(http://camel.apache.org)
 * Will construct new thread management when having time
 * @author chiqiu
 *
 */
public class DefaultExecutorServiceManager extends AppContextAware implements SocialServiceMBean{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultExecutorServiceManager.class);

    
    private ThreadPoolFactory threadPoolFactory = new DefaultThreadPoolFactory();
    private final List<ExecutorService> executorServices = new ArrayList<ExecutorService>();
    private String defaultThreadPoolProfileId = "defaultThreadPoolProfile";
    private final Map<String, ThreadPoolProfile> threadPoolProfiles = new ConcurrentHashMap<String, ThreadPoolProfile>();
    private final Map<String, ThreadPoolProfile> ondeckThreadPools = new ConcurrentHashMap<String, ThreadPoolProfile>();
    private ThreadPoolProfile builtIndefaultProfile;
    private String threadNamePattern;
    public DefaultExecutorServiceManager() {
        builtIndefaultProfile = new ThreadPoolProfile(defaultThreadPoolProfileId);
        builtIndefaultProfile.setDefaultProfile(true);
        builtIndefaultProfile.setPoolSize(10);
        builtIndefaultProfile.setMaxPoolSize(20);
        builtIndefaultProfile.setKeepAliveTime(60L);
        builtIndefaultProfile.setTimeUnit(TimeUnit.SECONDS);
        builtIndefaultProfile.setMaxQueueSize(1000);
        builtIndefaultProfile.setRejectedPolicy(ThreadPoolRejectedPolicy.CallerRuns);
        registerThreadPoolProfile(builtIndefaultProfile);
    }
    
    /**
     * @return the threadNamePattern
     */
    public String getThreadNamePattern() {
        return threadNamePattern;
    }


    /**
     * @param threadNamePattern the threadNamePattern to set
     */
    public void setThreadNamePattern(String threadNamePattern) {
        this.threadNamePattern = threadNamePattern;
    }


    /**
     * @return the threadPoolFactory
     */
    public ThreadPoolFactory getThreadPoolFactory() {
        return threadPoolFactory;
    }


    /**
     * @param threadPoolFactory the threadPoolFactory to set
     */
    public void setThreadPoolFactory(ThreadPoolFactory threadPoolFactory) {
        this.threadPoolFactory = threadPoolFactory;
    }


    /**
     * @return the defaultThreadPoolProfileId
     */
    public String getDefaultThreadPoolProfileId() {
        return defaultThreadPoolProfileId;
    }


    /**
     * @param defaultThreadPoolProfileId the defaultThreadPoolProfileId to set
     */
    public void setDefaultThreadPoolProfileId(String defaultThreadPoolProfileId) {
        this.defaultThreadPoolProfileId = defaultThreadPoolProfileId;
    }


    /**
     * @return the builtIndefaultProfile
     */
    public ThreadPoolProfile getBuiltIndefaultProfile() {
        return builtIndefaultProfile;
    }


    /**
     * @param builtIndefaultProfile the builtIndefaultProfile to set
     */
    public void setBuiltIndefaultProfile(ThreadPoolProfile builtIndefaultProfile) {
        this.builtIndefaultProfile = builtIndefaultProfile;
    }


    /**
     * @return the executorServices
     */
    public List<ExecutorService> getExecutorServices() {
        return executorServices;
    }


    /**
     * @return the threadPoolProfiles
     */
    public Map<String, ThreadPoolProfile> getThreadPoolProfiles() {
        return threadPoolProfiles;
    }
    
    public ThreadPoolProfile getDefaultThreadPoolProfile() {
        return getThreadPoolProfile(defaultThreadPoolProfileId);
    }


    public void registerThreadPoolProfile(ThreadPoolProfile profile) {
        ObjectHelper.notNull(profile, "profile");
        ObjectHelper.notEmpty(profile.getId(), "id", profile);
        threadPoolProfiles.put(profile.getId(), profile);
    }
    public void registerThreadPool(ThreadPoolProfile profile) {
        ObjectHelper.notNull(profile, "profile");
        ObjectHelper.notEmpty(profile.getId(), "id", profile);
        ondeckThreadPools.put(profile.getId(), profile);
    }
    
    public void unRegisterThreadPool(ThreadPoolProfile profile) {
        ObjectHelper.notNull(profile, "profile");
        ObjectHelper.notEmpty(profile.getId(), "id", profile);
        ondeckThreadPools.remove(profile.getId());
    }
    
    public ThreadPoolProfile getThreadPoolProfile(String id) {
        return threadPoolProfiles.get(id);
    }
    public ExecutorService newThreadPool(Object source, String name, String profileId) {
        ThreadPoolProfile profile = getThreadPoolProfile(profileId);
        if (profile != null) {
            return newThreadPool(source, name, profile);
        } else {
            // no profile with that id
            return null;
        }
    }
    public ExecutorService newThreadPool(Object source, String name, ThreadPoolProfile profile) {
        //String sanitizedName = URISupport.sanitizeUri(name);
        String sanitizedName = name;
        ObjectHelper.notNull(profile, "ThreadPoolProfile");

        ThreadPoolProfile defaultProfile = getDefaultThreadPoolProfile();
        profile.addDefaults(defaultProfile);

        ThreadFactory threadFactory = createThreadFactory(sanitizedName, true);
        ExecutorService executorService = threadPoolFactory.newThreadPool(profile, threadFactory);
        onThreadPoolCreated(executorService, source, profile.getId());
        
        //if (LOG.isDebugEnabled()) {
            //LOG.debug("Created new ThreadPool for source: {} with name: {}. -> {}", new Object[]{source, sanitizedName, executorService});
            LOG.debug("Created new ThreadPool for source: "+source+" with name: "+sanitizedName+". -> "+executorService);
        //}
        registerThreadPool(profile);
        return executorService;
    }
    
    
    
    /**
     * Invoked when a new thread pool is created.
     * This implementation will invoke the {@link LifecycleStrategy#onThreadPoolAdd(org.apache.camel.CamelContext,
     * java.util.concurrent.ThreadPoolExecutor, String, String, String, String) LifecycleStrategy.onThreadPoolAdd} method,
     * which for example will enlist the thread pool in JMX management.
     *
     * @param executorService the thread pool
     * @param source          the source to use the thread pool
     * @param threadPoolProfileId profile id, if the thread pool was created from a thread pool profile
     */
    private void onThreadPoolCreated(ExecutorService executorService, Object source, String threadPoolProfileId) {
        executorServices.add(executorService);
        String id;
        
        if (source != null) {
            // fallback and use the simple class name with hashcode for the id so its unique for this given source
            id = source.getClass().getSimpleName() + "(" + ObjectHelper.getIdentityHashCode(source) + ")";
        } else {
            // no source, so fallback and use the simple class name from thread pool and its hashcode identity so its unique
            id = executorService.getClass().getSimpleName() + "(" + ObjectHelper.getIdentityHashCode(executorService) + ")";
        }
        ObjectHelper.notEmpty(id, "id for thread pool " + executorService);
        /*
         *  //TODO
         *  add jmx function here
         */
    }
    public ExecutorService newThreadPool(Object source, String name, int poolSize, int maxPoolSize) {
        ThreadPoolProfile profile = new ThreadPoolProfile(name);
        profile.setPoolSize(poolSize);
        profile.setMaxPoolSize(maxPoolSize);
        return  newThreadPool(source, name, profile);
    }
    
    public ExecutorService newSingleThreadExecutor(Object source, String name) {
        return newFixedThreadPool(source, name, 1);
    }
    
    public ExecutorService newFixedThreadPool(Object source, String name, int poolSize) {
        ThreadPoolProfile profile = new ThreadPoolProfile(name);
        profile.setPoolSize(poolSize);
        profile.setMaxPoolSize(poolSize);
        profile.setKeepAliveTime(0L);
        return newThreadPool(source, name, profile);
    }
    //@Override
    public ScheduledExecutorService newScheduledThreadPool(Object source, String name, ThreadPoolProfile profile) {
        //String sanitizedName = URISupport.sanitizeUri(name);
        String sanitizedName = name;
        profile.addDefaults(getDefaultThreadPoolProfile());
        ScheduledExecutorService answer = threadPoolFactory.newScheduledThreadPool(profile, createThreadFactory(sanitizedName, true));
        onThreadPoolCreated(answer, source, null);

        //if (LOG.isDebugEnabled()) {
            LOG.debug("Created new ScheduledThreadPool for source: {} with name: {}. -> {}", new Object[]{source, sanitizedName, answer});
        //}
        return answer;
    }

    //@Override
    public ScheduledExecutorService newScheduledThreadPool(Object source, String name, String profileId) {
        ThreadPoolProfile profile = getThreadPoolProfile(profileId);
        if (profile != null) {
            return newScheduledThreadPool(source, name, profile);
        } else {
            // no profile with that id
            return null;
        }
    }

    //@Override
    public ScheduledExecutorService newScheduledThreadPool(Object source, String name, int poolSize) {
        ThreadPoolProfile profile = new ThreadPoolProfile(name);
        profile.setPoolSize(poolSize);
        return newScheduledThreadPool(source, name, profile);
    }
    
    public void doStart() throws Exception {
        if (threadNamePattern == null) {
            // set default name pattern which includes the camel context name
            threadNamePattern = "Rapidbackend ("  + ") thread #${counter} - ${name}";
        }
    }
    public void doStop() throws Exception {
        // noop
    }
    private ThreadFactory createThreadFactory(String name, boolean isDaemon) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadNamePattern, name, isDaemon);
        return threadFactory;
    }
    /**
     * implementing SocialServiceMBean
     */
    
    /**
     * 
     */
    public String getName(){
        return "SocialExecutorServiceManager";
    }
    
    
    public List<String> getServices(){
        List<String> val= new ArrayList<String>();
        for(ThreadPoolProfile poolProfile : ondeckThreadPools.values()){
            val.add(poolProfile.toString());
        }
        return val;
    }
}
