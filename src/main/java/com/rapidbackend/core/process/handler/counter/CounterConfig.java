package com.rapidbackend.core.process.handler.counter;

import org.springframework.beans.factory.annotation.Required;

public class CounterConfig {
    private String counterKeyPrefix = "";
    private String redisCounterTarget;
    public String getCounterKeyPrefix() {
        return counterKeyPrefix;
    }
    public void setCounterKeyPrefix(String counterKeyPrefix) {
        this.counterKeyPrefix = counterKeyPrefix;
    }
    public String getRedisCounterTarget() {
        return redisCounterTarget;
    }
    @Required
    public void setRedisCounterTarget(String redisCounterTarget) {
        this.redisCounterTarget = redisCounterTarget;
    }
    
}
