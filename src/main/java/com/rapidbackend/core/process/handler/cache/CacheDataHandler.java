package com.rapidbackend.core.process.handler.cache;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.cache.RedisCache;
import com.rapidbackend.core.process.handler.cache.CacheAware;
import com.rapidbackend.socialutil.process.handler.DataHandler;

public abstract class CacheDataHandler extends DataHandler implements CacheAware{
    protected RedisCache redisCache;

    public RedisCache getRedisCache() {
        return redisCache;
    }
    @Required
    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
}
