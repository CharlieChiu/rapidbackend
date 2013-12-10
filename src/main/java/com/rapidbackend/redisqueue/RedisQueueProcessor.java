package com.rapidbackend.redisqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.util.comm.redis.client.RedisPoolConfig;

/**
 * A queue processor handles queue processing jobs with a poller thread and several handler threads.
 * User can create multiple processors against one queue for poll performance need.
 * @author chiqiu
 *
 */
public class RedisQueueProcessor extends AppContextAware{
    protected Logger logger = LoggerFactory.getLogger(RedisQueueProcessor.class);
    
    @SuppressWarnings("rawtypes")
    private final BlockingQueue<Exchanger> exchangerQueue = new LinkedBlockingQueue<Exchanger>();
    private ExecutorService pollerExecutor;
    private ExecutorService handlerExecutor;
    protected int handlerNum = 1;
    protected String handlerBeanName;
    protected String queueProcessorName;
    protected RedisQueuePoller poller;
    protected String queueName;
    protected RedisPoolConfig redisConfig;
    protected RedisQueueService container;
    
    
    public void setContainer(RedisQueueService container) {
        this.container = container;
    }
    public String getQueueName() {
        return queueName;
    }
    @Required
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    public RedisPoolConfig getRedisConfig() {
        return redisConfig;
    }
    @Required
    public void setRedisConfig(RedisPoolConfig redisConfig) {
        this.redisConfig = redisConfig;
    }
    
    @Required
    public void setQueueProcessorName(String queueProcessorName) {
        this.queueProcessorName = queueProcessorName;
    }

    public String getHandlerBeanName() {
        return handlerBeanName;
    }
    @Required
    public void setHandlerBeanName(String handlerBeanName) {
        this.handlerBeanName = handlerBeanName;
    }
    
    public int getHandlerNum() {
        return handlerNum;
    }
    public void setHandlerNum(int handlerNum) {
        this.handlerNum = handlerNum;
    }
    public void doStart() throws Exception{
        logger.info("--------------> RedisQueueProcessor "+queueProcessorName+" dostart()");
        initPollHandler();
        initPoller();
        //TODO set mbean info here
        logger.info("<-------------- RedisQueueProcessor "+queueProcessorName+" dostart()");
    }
    /**
     * register the executor we created into the container for further management
     * @param executorService
     */
    public void registerExecutor(ExecutorService executorService){
        container.getExecutorServices().add(executorService);
    }
    
    public void initPollHandler(){
        handlerExecutor = Rapidbackend.getCore().getThreadManager().newFixedThreadPool(null, "QueueProcessor "+ queueProcessorName+ " handler", handlerNum+1);
        registerExecutor(handlerExecutor);
        for(int i =0;i<handlerNum;i++){
            RedisQueuePollHandler handler = (RedisQueuePollHandler)getApplicationContext().getBean(handlerBeanName);
            handler.setContainer(container);
            handler.setExchangerQueue(exchangerQueue);
            //TODO set mbean info here
            handlerExecutor.submit(handler);
        }
    }
    public void initPoller(){
        poller = new RedisQueuePoller(redisConfig.getTargetName(), queueName, exchangerQueue,container);
        //TODO set mbean info here
        pollerExecutor = Rapidbackend.getCore().getThreadManager().newFixedThreadPool(null, "QueueProcessor "+ queueProcessorName+ " Poller", 1);
        registerExecutor(pollerExecutor);
        pollerExecutor.submit(poller);
    }
    /**
     * stop the poller
     */
    public void doStop(){
        logger.info("--------------> RedisQueueProcessor "+queueProcessorName+" dostop()");
        
        logger.info("<-------------- RedisQueueProcessor "+queueProcessorName+" dostop()");
    }
}
