package com.rapidbackend.socialutil.sharding.defaultmappers;

import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.cache.CacheMapper;


/**
 * Mapper class to map a user's inbox to different target names in redis pool
 * @author chiqiu
 *
 */
public class RedisUserInboxMapper implements CacheMapper{
    public final String DEFAULT_INBOX_TARGETNAME = "redisInbox";
    
    protected String redisTargetName;
    
    
    public String getRedisTargetName() {
        return redisTargetName;
    }
    @Required
    public void setRedisTargetName(String redisTargetName) {
        this.redisTargetName = redisTargetName;
    }
    /**
     * Current implemetation lets all users get their redis ibox by the inbox targetName<br>
     * configured in redis connection pool, when you want to map a user into different redis inboxes<br>
     * you can override this method to get the wanted redis inbox targetName
     * @return
     */
    @Override
    public String getRedisTargetName(Object userId){
        return redisTargetName;
    }
    
    
    
    @Override
    public Set<String> getAllRedisTargetNames(){
        TreeSet<String> result = new TreeSet<String>();
        result.add(redisTargetName);
        return result;
    }
}
