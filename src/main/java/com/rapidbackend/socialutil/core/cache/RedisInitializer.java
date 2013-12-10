package com.rapidbackend.socialutil.core.cache;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.util.comm.redis.client.RedisPoolConfig;

/**
 * init redis caches , most of the caches should be initialized using this initializer
 * @author chiqiu
 *
 */
public class RedisInitializer {
    
    protected List<RedisPoolConfig> cacheConfigs;

    public List<RedisPoolConfig> getCacheConfigs() {
        return cacheConfigs;
    }
    @Required
    public void setCacheConfigs(List<RedisPoolConfig> cacheConfigs) {
        this.cacheConfigs = cacheConfigs;
    }
    /**
     * create connection pool to Redis instances
     */
    public void initCaches(){
        for(RedisPoolConfig redisPoolConfig : cacheConfigs){
            Rapidbackend.getCore().getRedisClientPoolContainer().addRedisClientPool(redisPoolConfig);
        }
    }
    
}
