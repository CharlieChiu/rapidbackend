package com.rapidbackend.util.comm.redis.client;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.util.comm.redis.RedisException;

/**
 * TODO add a check here, two targets should not use the same database!!
 * @author chiqiu
 *
 */
public class RedisPoolContainerFactory {
	Logger logger = LoggerFactory.getLogger(RedisPoolContainerFactory.class);
	
	public List<RedisPoolConfig> redisPoolConfigs;
	protected static HashMap<String, String> targetInfo = new HashMap<String, String>();
	public List<RedisPoolConfig> getRedisPoolConfigs() {
		return redisPoolConfigs;
	}

	public void setRedisPoolConfigs(List<RedisPoolConfig> redisPoolConfigs) {
		this.redisPoolConfigs = redisPoolConfigs;
	}
	/**
	 * construct a RedisPoolContainer with list redisPoolConfigs<br>
	 * Should config redisPoolConfigs in spring before calling this method
	 * @return
	 */
	public synchronized RedisClientPoolContainer createRedisPoolContainer(){
		try{
			RedisClientPoolContainer container = new RedisClientPoolContainer();
			for(RedisPoolConfig poolConfig : redisPoolConfigs){
							    
				container.addRedisClientPool(poolConfig);
				
			}
			return container;
		}catch(RedisException e){
			logger.error("",e);
			throw e;
		}catch(Exception e){
			logger.error("",e);
			throw new RedisException(BackendRuntimeException.InternalServerError,"error in createRedisPoolContainer",e);
		}
	}
	
	
}
