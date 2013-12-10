package com.rapidbackend.util.comm.redis.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.util.comm.redis.RedisException;

import com.rapidbackend.util.comm.redis.client.Jedis;

public class RedisClientFactory {
    protected static Logger logger = LoggerFactory.getLogger(RedisClientFactory.class);
	/**
	 * create redis client
	 * @param profile
	 * @return
	 */
	public static RedisClient createRedisClient(RedisClientProfile profile) {
		RedisClient redisClient = new RedisClient();
		redisClient.setProfile(profile);
        Jedis jedis = null;
		try{
		    jedis = createJedis(profile);
        }catch (Exception e) {
            throw new RedisException(BackendRuntimeException.InternalServerError,"error creating jedis: "+profile.toString(),e);
        }
		redisClient.setJedis(jedis);
		return redisClient;
	}
	/**
	 * renew old jedis connection
	 * @param old
	 * @return
	 */
	public static RedisClient reNewRedisClient(RedisClient old) throws RedisException{
		
		Jedis jedis = null;
		RedisClientProfile profile = old.getProfile();
		try {
            old.getJedis().disconnect();
        } catch (Exception ignore) {
            logger.error("error closing old redis "+ profile.toString());
        }
		try{
		    jedis = createJedis(old.getProfile());
		}catch (Exception e) {
		    logger.error("error creating jedis: "+profile.toString());
		    throw new RedisException(BackendRuntimeException.InternalServerError,"error renewing jedis: "+profile.toString(),e);
        }
		old.setJedis(jedis);
		return old;
	}
	
	public static Jedis createJedis(RedisClientProfile profile){
		Jedis jedis = new Jedis(profile.getHostaddress(), profile.getPort());
		jedis.connect();
		try {
		    //jedis.getClient().setTimeoutInfinite();
	        jedis.getClient().getSocket().setKeepAlive(true);// keep alive, with time out set to 2 seconds
	        //jedis.getClient().getSocket().setSoTimeout(50);
	        //jedis.getClient().setTimeout(5);
        } catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.InternalServerError,"error creating jedis connection "+profile.toString(),e);
        }
		jedis.select(profile.getDatabaseIndex());
		return jedis;
	}
}
