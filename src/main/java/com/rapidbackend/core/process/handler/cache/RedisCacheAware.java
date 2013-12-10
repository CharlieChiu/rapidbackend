package com.rapidbackend.core.process.handler.cache;

import com.rapidbackend.cache.RedisCache;

public abstract class RedisCacheAware {
    protected RedisCache redisCache;

    public RedisCache getRedisCache() {
        return redisCache;
    }

    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
    
}
