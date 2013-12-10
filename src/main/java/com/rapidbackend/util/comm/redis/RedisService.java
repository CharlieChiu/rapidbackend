package com.rapidbackend.util.comm.redis;

import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.util.comm.redis.client.RedisClientPoolContainer;
import com.rapidbackend.util.comm.redis.client.RedisPoolContainerFactory;
/**
 * RedisService matains the redis connection pool <p>
 * TODO : Add other redis related status here
 * @author chiqiu
 *
 */
public class RedisService extends ClusterableService{
	
	protected RedisClientPoolContainer redisPoolContainer ;
	
	@Override
	public synchronized void doStart(){
		RedisPoolContainerFactory redisPoolContainerFactory = 
			(RedisPoolContainerFactory)getApplicationContext().getBean("RedisPoolContainerFactory");
		redisPoolContainer = redisPoolContainerFactory.createRedisPoolContainer();	
		setRunning(true);
	}
	@Override
	public synchronized void doStop(){
		if(redisPoolContainer!=null){
		    redisPoolContainer.destroy();
		}
	}
	public RedisClientPoolContainer getRedisPoolContainer() {
		return redisPoolContainer;
	}
	public void setRedisPoolContainer(RedisClientPoolContainer redisPoolContainer) {
		this.redisPoolContainer = redisPoolContainer;
	}
	
	
}
