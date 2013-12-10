package com.rapidbackend.util.comm.redis.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.util.comm.redis.client.Jedis;
/**
 * simple wrapper of jedis
 * @author chiqiu
 *
 */
public class RedisClient {
	Logger logger = LoggerFactory.getLogger(RedisClient.class);
	protected Jedis jedis;
	protected RedisClientProfile profile;
	public Jedis getJedis() {
		return jedis;
	}
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	public RedisClientProfile getProfile() {
		return profile;
	}
	public void setProfile(RedisClientProfile profile) {
		this.profile = profile;
	}
}
