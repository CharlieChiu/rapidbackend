package com.rapidbackend.redisqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.core.Rapidbackend;
/**
 * A redisqueue service is used for polling and hanlding job queues constructed on redis
 */
public class RedisQueueService extends ClusterableService{
    protected Logger logger = LoggerFactory.getLogger(RedisQueueService.class);
    protected RedisQueue redisQueue;
    
    protected AtomicInteger pendingExchangeCount = new AtomicInteger(0);
    
    protected List<ExecutorService> executorServices = new ArrayList<ExecutorService>();
    
    
    public List<ExecutorService> getExecutorServices() {
        return executorServices;
    }

    protected List<RedisQueueProcessor> queueProcessors;
    public RedisQueue getRedisQueue() {
        return redisQueue;
    }
    @Required
    public void setRedisQueue(RedisQueue redisQueue) {
        this.redisQueue = redisQueue;
    }
    
    public List<RedisQueueProcessor> getQueueProcessors() {
        return queueProcessors;
    }
    @Required
    public void setQueueProcessors(List<RedisQueueProcessor> queueProcessors) {
        this.queueProcessors = queueProcessors;
    }
    public AtomicInteger getPendingExchangeCount() {
        return pendingExchangeCount;
    }
    public void setPendingExchangeCount(AtomicInteger pendingExchangeCount) {
        this.pendingExchangeCount = pendingExchangeCount;
    }
    
    public RedisQueueService() throws BackendRuntimeException{
        
    }
    
    @Override
    public synchronized void doStart() throws Exception{
        logger.info("-------------> init redisqueue service");
        
        for(RedisQueueProcessor p:queueProcessors){
            Rapidbackend.getCore().getRedisClientPoolContainer().addRedisClientPool(p.getRedisConfig());
        }
        for(RedisQueueProcessor p: queueProcessors){
            p.setContainer(this);
            p.doStart();
        }
        logger.info("<------------- init redisqueue service");
    }
    
    @Override
    public synchronized void doStop() throws Exception{
        //TODO do we need to log all the queue status here?
        for(RedisQueueProcessor p: queueProcessors){
            p.doStop();
        }
    }
}
