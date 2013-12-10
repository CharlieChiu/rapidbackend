package com.rapidbackend.core.process.handler.counter;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.core.process.RequestHandlerBase;
import com.rapidbackend.util.comm.redis.client.RedisClientPoolContainer;

/**
 * @author chiqiu
 * Note, currently we don't need to implement sharding for counters, 
 * because counters have very small memory footprint. 
 */
public abstract class CounterHandler extends RequestHandlerBase{
    
    protected CounterConfig counterConfig;
    
    public CounterConfig getCounterConfig() {
        return counterConfig;
    }
    @Required
    public void setCounterConfig(CounterConfig counterConfig) {
        this.counterConfig = counterConfig;
    }
    protected Rapidbackend rapidbackend;
    protected String intKeyParamName;
    protected String intKeyListParamName;
    protected String previousHandlerResultContainerObjectName;
    
    public String getPreviousHandlerResultContainerObjectName() {
        return previousHandlerResultContainerObjectName;
    }
    public void setPreviousHandlerResultContainerObjectName(
            String previousHandlerResultContainerObjectName) {
        this.previousHandlerResultContainerObjectName = previousHandlerResultContainerObjectName;
    }
    public String getIntKeyListParamName() {
        return intKeyListParamName;
    }
    public void setIntKeyListParamName(String intKeyListParamName) {
        this.intKeyListParamName = intKeyListParamName;
    }
    public String getIntKeyParamName() {
        return intKeyParamName;
    }
    public void setIntKeyParamName(String intKeyParamName) {
        this.intKeyParamName = intKeyParamName;
    }
    public RedisClientPoolContainer getRedisPoolContainer(){
        if(rapidbackend == null){
            rapidbackend = Rapidbackend.getCore();
        }
        return rapidbackend.getRedisClientPoolContainer();
    }
}
