package com.rapidbackend.redisqueue;


import com.rapidbackend.util.comm.AbstractPollHandler;
/**
 * Handles queue items retrived by poller.
 * Note: PollHandlers created by spring should use bean scope 'prototype' 
 * @author chiqiu
 *
 */
public abstract class RedisQueuePollHandler extends AbstractPollHandler{
    protected RedisQueueService container;
    public void setContainer(RedisQueueService container) {
        this.container = container;
    }
    
    public RedisQueueService getContainer() {
        return container;
    }

    public RedisQueuePollHandler(){
        setCreated(System.currentTimeMillis());
    }
    
    
    public boolean underPollingCondition(){
        return container.isRunAllowed() && !container.isShutdownPending();
    }
    public void handleIntrupt(){
        
    }
    public void onStartExchange(){
        container.getPendingExchangeCount().incrementAndGet();
    }
    public void onFinishExchange(){
        container.getPendingExchangeCount().decrementAndGet();
        setCurrentState("waiting");
    }
    public void onStopHandle(){
        //TODO do something in the future
        setCurrentState("stopping");
    }
    
    public void handleReturnValue(Object redisqueueItem){
        logger.info("handleReturnValue  ------>");
        setCurrentState("handling return value");
        setStartHandleTime(System.currentTimeMillis());
        handledRequestNum ++;
        setTargetName(redisqueueItem.toString());
        
        logger.info("handling "+redisqueueItem.toString());
        
        logger.info("handleReturnValue  <------");
    }
    
    
    /*
     * implements handler mbean
     * */
    protected String handlerName;
    protected String targetName;
    protected String currentState;
    protected long handledRequestNum = 0;
    protected long created ;
    protected long startHandleTime;
    protected String[] statesStrings = {"handling","waiting","exchanging","timeout","stopping"};
    
    
    /**
     * @return the targetName
     */
    public String getTargetName() {
        return targetName;
    }

    /**
     * @param targetName the targetName to set
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * @param currentState the currentState to set
     */
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }
    
    /**
     * @return the handlerName
     */
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * @param handlerName the handlerName to set
     */
    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

   

    public long getHandledRequestNum() {
        return handledRequestNum;
    }
    /**
     * @param handledRequestNum the handledRequestNum to set
     */
    public void setHandledRequestNum(int handledRequestNum) {
        this.handledRequestNum = handledRequestNum;
    }

    

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    /**
     * @return the startHandleTime
     */
    public long getStartHandleTime() {
        return startHandleTime;
    }

    /**
     * @param startHandleTime the startHandleTime to set
     */
    public void setStartHandleTime(long startHandleTime) {
        this.startHandleTime = startHandleTime;
    }

    /**
     * @return the statesStrings
     */
    public String[] getStatesStrings() {
        return statesStrings;
    }

    /**
     * @param statesStrings the statesStrings to set
     */
    public void setStatesStrings(String[] statesStrings) {
        this.statesStrings = statesStrings;
    }

}
