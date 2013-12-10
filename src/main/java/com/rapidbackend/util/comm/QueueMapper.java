package com.rapidbackend.util.comm;

import com.rapidbackend.cache.CacheMapper;
/**
 * 
 * @author chiqiu
 *
 */
public abstract class QueueMapper implements CacheMapper{
    
    /**
     * get queue name with a fixed prefix, which is useful when using "keys" to get all queues
     * @param input
     * @return
     */
    public abstract String getQueueName(Object input);
}
