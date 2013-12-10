package com.rapidbackend.util.comm.redis.client;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.util.comm.redis.RedisException;

/**
 * A pool-like container which is used to maintain redis clients
 * @author chiqiu
 *
 */
public class RedisClientPoolContainer {
	Logger logger = LoggerFactory.getLogger(RedisClientPoolContainer.class);
	protected static HashMap<String, String> targetInfo = new HashMap<String, String>();
	protected HashMap<String, BlockingQueue<RedisClient>> clientPools;
	protected HashMap<String, RedisPoolConfig> poolConfigs;
	
	protected HashMap<String, LinkedBlockingQueue<String>> callerInfos = new HashMap<String, LinkedBlockingQueue<String>>();
	protected HashMap<String, LinkedBlockingQueue<String>> returnInfos = new HashMap<String, LinkedBlockingQueue<String>>();
	
	protected boolean shutDown = false;
	
	protected long clientTimeout = 6*1000l;
	
	
	public long getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(long clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

    public boolean isShutDown() {
        return shutDown;
    }

    public void setShutDown(boolean shutDown) {
        this.shutDown = shutDown;
    }

    public HashMap<String, RedisPoolConfig> getPoolConfigs() {
		return poolConfigs;
	}

	public void setPoolConfigs(HashMap<String, RedisPoolConfig> poolConfigs) {
		this.poolConfigs = poolConfigs;
	}

	public RedisClientPoolContainer(){
		clientPools = new HashMap<String, BlockingQueue<RedisClient>>();
		poolConfigs = new HashMap<String, RedisPoolConfig>();
	}

	public HashMap<String, BlockingQueue<RedisClient>> getClientPools() {
		return clientPools;
	}

	public void setClientPools(
			HashMap<String, BlockingQueue<RedisClient>> clientPools) {
		this.clientPools = clientPools;
	}
	
	public BlockingQueue<RedisClient> getPool(String targetName){
		BlockingQueue<RedisClient> result = clientPools.get(targetName);
		if(result==null){
		    throw new RedisException(BackendRuntimeException.INTERNAL_SERVER_ERROR, "no RedisClient pool created for redis target "+targetName);
		}else{
		    return result;
		}
	}
	
	public BlockingQueue<RedisClient> checkPoolExistance(String targetName){
        BlockingQueue<RedisClient> result = clientPools.get(targetName);
        return result;
    }
	/**
	 * get redis client witout timeout, you must call returnClient() after you call this method
	 * @param targetName the redis target name of the pool, it should be unique in one application
	 * @return
	 * @throws InterruptedException
	 */
	public RedisClient borrowClient(String targetName){
	    if(isShutDown()){
	        return null;
	    }else {
	        String callerThread  = Thread.currentThread().getName();
	        LinkedBlockingQueue<String> callerInfo = getCallerInfo(targetName);
	        
	        try {
	            //logger.debug("borrowClient ==========>"+System.currentTimeMillis());
	            
	            RedisClient client =  getPool(targetName).poll(clientTimeout,TimeUnit.MILLISECONDS);
	            
	            //logger.debug("borrowClient"+client);
	            if(client == null){
	                String callinfo = callerInfoString(targetName);
	                String returnInfo = returnInfoString(targetName);
	                StringBuffer sb = new StringBuffer();
	                sb.append("error borrow client:").append(callinfo).append("return info:").append(returnInfo);
	                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error borrow client" + sb.toString());
	            }else {
	                callerInfo.add(callerThread + ','+client.toString()+','+targetName+','+System.currentTimeMillis());
                }
	            //logger.debug("borrowClient <=========="+System.currentTimeMillis());
	            return client;
            } catch (Exception e) {
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error borrow client at date "+System.currentTimeMillis(),e);
            }
        }
		
	}
	
	public LinkedBlockingQueue<String> getCallerInfo(String targetName){
	    LinkedBlockingQueue<String> info = callerInfos.get(targetName);
        int capacity = 100;
        if(info==null){
            info = new LinkedBlockingQueue<String>();//keep 20 caller info
            callerInfos.put(targetName, info);
        }
        int size = info.size();
        if(size > capacity){
            info.remove();
        }
        return info;
	}
	
	public LinkedBlockingQueue<String> getReturnInfo(String targetName){
        LinkedBlockingQueue<String> info = returnInfos.get(targetName);
        int capacity = 100;
        if(info==null){
            info = new LinkedBlockingQueue<String>();//keep 20 caller info
            returnInfos.put(targetName, info);
        }
        int size = info.size();
        if(size > capacity){
            info.remove();
        }
        return info;
    }
	
	public String callerInfoString(String targetName) {
	    LinkedBlockingQueue<String> info = callerInfos.get(targetName);
	    StringBuffer sb = new StringBuffer();
	    for(String string : info){
	        sb.append(string);
	        sb.append(";\n\t");
	    }
	    return sb.toString();
	}
	
	public String returnInfoString(String targetName) {
        LinkedBlockingQueue<String> info = returnInfos.get(targetName);
        StringBuffer sb = new StringBuffer();
        for(String string : info){
            sb.append(string);
            sb.append(";\n\t");
        }
        return sb.toString();
    }
	/**
	 * return client to pool, must be called after every borrow
	 * @param redisClient
	 * @param targetName
	 * @return
	 */
	public boolean returnClient(RedisClient redisClient,String targetName){
	    if(redisClient!=null){
	        String callerThread  = Thread.currentThread().getName();
            LinkedBlockingQueue<String> returnInfo = getReturnInfo(targetName);
            
            try {
                BlockingQueue<RedisClient> pool = getPool(targetName);
                pool.put(redisClient);
                StringBuffer sb = new StringBuffer();
                sb.append(callerThread).append(',');
                sb.append(redisClient.toString()).append(',');
                sb.append("targetName:").append(targetName).append(',');
                sb.append("size:").append(pool.size()).append(',');
                sb.append(System.currentTimeMillis());
                
                returnInfo.add(sb.toString());
            } catch (InterruptedException e) {
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error return redisclient "+ targetName +','+ redisClient,e);
            }
	        
	        return true;
	    }else {
            return false;
        }
	}
	/**
	 * get redis client, returns null if timeout
	 * @param targetName
	 * @param miliseconds
	 * @return
	 * @throws InterruptedException
	 */
	public RedisClient borrowClient(String targetName,long miliseconds) throws InterruptedException{
	    if(isShutDown()){
	        return null;
	    }else {
	        return getPool(targetName).poll(miliseconds, TimeUnit.MILLISECONDS);
        }
	}
	/**
	 * remove a client before you want to reCreate one
	 * @param client
	 */
	public void removeClient(RedisClient client){
		 BlockingQueue<RedisClient> pool = getPool(client.getProfile().getTargetName());
		 pool.remove(client);
	}
	/**
	 * release all the resource and close all connections
	 */
	public void destroy(){
		shutDown = true;
		try {
		    closeConnections();
	        try {
	            // wait a little while and check again
	            Thread.sleep(100l);
	        } catch (Exception ignore) {
	        }
	        closeConnections();
        } catch (Exception e) {
            logger.warn("error during destroying redis pool container:", e);
        }
		
	}
	
	public void closeConnections() throws InterruptedException{
	    for(BlockingQueue<RedisClient> clientQueue : clientPools.values()){
	        while(!clientQueue.isEmpty()){
	            RedisClient client = clientQueue.take();
	            client.getJedis().quit();
	        }
        }
	}
	
	public void addRedisClientPool(RedisPoolConfig poolConfig){
	    String targetName = poolConfig.getTargetName();
        
        if (targetInfo.containsKey(poolConfig.getConfig())) {
            String errorInfo = "reject config for target "+targetName+
            " because "+targetInfo.get(poolConfig.getConfig())+ " is using the same config: "
            +poolConfig.getConfig();
            throw new RedisException(BackendRuntimeException.INTERNAL_SERVER_ERROR, errorInfo);
        }
        
        if(checkPoolExistance(targetName)!=null){
            logger.error("targetName "+targetName +" already exists!");
            throw new RedisException(BackendRuntimeException.INTERNAL_SERVER_ERROR, "targetName "+targetName +" already exists!");
        }
        getPoolConfigs().put(targetName, poolConfig);
        int poolCapacity = poolConfig.getPoolCapacity();
        BlockingQueue<RedisClient> pool =  new ArrayBlockingQueue<RedisClient>(poolCapacity,true);
        getClientPools().put(targetName, pool);
        try {
            for(int i = 0; i<poolCapacity;i++){
                RedisClientProfile clientProfile = new RedisClientProfile(poolConfig,i);
                RedisClient redisClient = RedisClientFactory.createRedisClient(clientProfile);
                pool.add(redisClient);
                Thread.sleep(50l);// sleep a while, not too fast
            }
        } catch (InterruptedException e) {
            throw new RedisException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error during add redis client pool",e);
        }
        
	}
	
}
