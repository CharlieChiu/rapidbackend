package com.rapidbackend.redisqueue;

import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.util.comm.QueueMapper;

/**
 * simple example implementation of QueueMapper
 * @author chiqiu
 *
 */
public class SimpleQueueMapper extends QueueMapper{
	
	private String redisTargetName ;
	private String queueName ;
	
	
	@Required
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	@Required
	public void setRedisTargetName(String redisTargetName) {
		this.redisTargetName = redisTargetName;
	}
    
    @Override
    public String getRedisTargetName(Object input){
        return redisTargetName;
    }
    @Override
    public String getQueueName(Object input){
    	return queueName;
    }
    @Override
    public Set<String> getAllRedisTargetNames(){
        TreeSet<String> result = new TreeSet<String>();
        result.add(redisTargetName);
        return result;
    }
}
