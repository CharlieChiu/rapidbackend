package com.rapidbackend.redisqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;

import org.apache.commons.lang3.StringUtils;

import com.rapidbackend.util.comm.AbstractPoller;

public class RedisQueuePoller extends AbstractPoller{
    protected RedisQueueService container;
    protected String queueName;
    protected String redisTargetName;
    
    
    public String getQueueName() {
        return queueName;
    }
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getRedisTargetName() {
        return redisTargetName;
    }
    public void setRedisTargetName(String redisTargetName) {
        this.redisTargetName = redisTargetName;
    }

    /**
     * This handler is binded to a thread, it is not state less as other handlers
     * @param queue exchanger queue to exchange with pollers
     * @param service service which calls this handler
     */
    public RedisQueuePoller(String redisTargetName, String queueName, BlockingQueue<Exchanger> exchangerQueue,RedisQueueService container){
        setRedisTargetName(redisTargetName);
        setQueueName(queueName);
        setExchangerQueue(exchangerQueue);
        this.container = container;
    }
    
    @Override
    public String getPollerName(){
        return getPollTarget();
    }
    @Override
    public String getPollTarget(){
        return getRedisTargetName() + ":"+getQueueName();
    }
    @Override
    public boolean underPollingCondition(){
        return container.isRunAllowed() && !container.isShutdownPending();
    }
    @SuppressWarnings("static-access")
    @Override
    public void handleNoPollingResult(){
      //do noting for now because timeoutMs is default set to 100ms
        //logger.warn("nothing polled, sleep 100 ms");
        try{
            Thread.currentThread().sleep(100);// avoid overheat
        }catch(Exception e){
            logger.error("",e);
        }
    }
    @Override
    public boolean resultNotEmpty(Object result) {
        return result !=null && !StringUtils.isEmpty(result.toString());
    }
    @Override
    public void onStartPolling(){
        state = "running";
    }
    @Override
    public void onStopPolling(){
        //
    }
    @Override
    public Object doPolling() throws Exception{
        logger.debug("do polling --------->");
        return container.getRedisQueue().getAndDelItemAsString(redisTargetName, queueName);
    }
    @Override
    public void handleIntrupt(){
        //
    }
}
