package com.rapidbackend.core.process.handler.cache;

import com.rapidbackend.cache.RedisCache;

public interface CacheAware {
    public RedisCache getRedisCache() ;
}
