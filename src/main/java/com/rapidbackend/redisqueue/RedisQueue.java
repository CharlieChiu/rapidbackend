package com.rapidbackend.redisqueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.cache.RedisCache;
import com.rapidbackend.util.comm.QueueMapper;
/**
 * Simple queue implementation based on redis list.<br/>
 * Note, in this version , if the task handling failed, the job removed from a queue cann't be restored.<br/>
 * Should make job restore  an optional queue policy in the next version.(rpoplpush)<br/>
 * Second, there's no limit on a queue size, this should also be added in a lua script next time.<br/>
 * Third, if we want to set expire to items inside a queue, we should use zset with a time stamp instead of using list
 * @author chiqiu
 *
 */
public class RedisQueue {
    Logger logger  = LoggerFactory.getLogger(RedisQueue.class);
    protected RedisCache redis = new RedisCache();
    protected QueueMapper queueMapper;
    
    @Required
    public void setQueueMapper(QueueMapper queueMapper) {
        this.queueMapper = queueMapper;
    }

    public void addItem(Object input) throws Exception{
        String queueName = queueMapper.getQueueName(input);
        addItem(input, queueName);
    }
    
    public Long addItem(Object input,String queueName) throws Exception{
        String redisTarget = queueMapper.getRedisTargetName(input);
        return redis.lpushJsonObject(queueName, input, redisTarget);
    }
    
    public Long addItem(Object input,String redisTargetName,String queueName) throws Exception{
        return redis.lpushJsonObject(queueName, input, redisTargetName);
    }
    /**
     * 
     * @param mapperInput input for a mapper to get the correct redis instance and queue name, can be null if queue mapper accepts null input
     * @param queue
     * @param clazz
     * @return
     * @throws Exception
     */
    public <T> T getItem(Object mapperInput, Class<T> clazz) throws Exception{
        String redisTarget = queueMapper.getRedisTargetName(mapperInput);
        String queueName = queueMapper.getQueueName(mapperInput);
        return redis.lindexLastJsonObject(queueName, redisTarget, clazz);
    }
    
    public <T> T getItem(String redisTarget, String queueName, Class<T> clazz) throws Exception{
        
        return redis.lindexLastJsonObject(queueName, redisTarget, clazz);
    }
    
    /**
     * 
     * @param mapperInput input for a mapper to get the correct redis instance and queue name, can be null if queue mapper accepts null input
     * @param queue
     * @param clazz
     * @return
     * @throws Exception
     */
    public <T> T getAndDelItem(Object mapperInput, String queue, Class<T> clazz) throws Exception{
        String redisTarget = queueMapper.getRedisTargetName(mapperInput);
        String queueName = queueMapper.getQueueName(mapperInput);
        return redis.rpopJsonObject(queueName, redisTarget, clazz);
    }
    
    public <T> T getAndDelItem(String redisTarget, String queueName, Class<T> clazz) throws Exception{
        return redis.rpopJsonObject(queueName, redisTarget, clazz);
    }
    /**
     * 
     * @param redisTarget
     * @param queueName
     * @param clazz
     * @return
     * @throws Exception
     */
    public String getAndDelItemAsString(String redisTarget, String queueName) throws Exception{
        logger.debug("getAndDelItemAsString "+queueName);
        return redis.rpop(queueName, redisTarget);
    }
    /**
     * 
     * @param queue
     * @param mapperInput mapperInput input for a mapper to get the correct redis instance and queue name, can be null if queue mapper accepts null input
     * @throws Exception
     */
    public void deleteQueue(String queue,Object mapperInput) throws Exception{
        String redisTarget = queueMapper.getRedisTargetName(mapperInput);
        redis.del(redisTarget, queue);
    }
    
    public void deleteQueue(String queue,String redisTargetName) throws Exception{
        redis.del(redisTargetName, queue);
    }
    
    /**
     * 
     * @param queue
     * @param mapperInput mapperInput input for a mapper to get the correct redis instance and queue name, can be null if queue mapper accepts null input
     * @return
     * @throws Exception
     */
    public Long size(String queue, Object mapperInput) throws Exception{
        String redisTarget = queueMapper.getRedisTargetName(mapperInput);
        return redis.listLength(queue, redisTarget);
    }
    
    public Long size(String queue, String redisTarget) throws Exception{
        return redis.listLength(queue, redisTarget);
    }
    
}
