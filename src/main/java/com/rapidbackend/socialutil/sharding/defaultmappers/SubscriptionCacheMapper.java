package com.rapidbackend.socialutil.sharding.defaultmappers;

import java.util.Set;
import java.util.TreeSet;

import com.rapidbackend.cache.CacheMapper;

public class SubscriptionCacheMapper implements CacheMapper{
    public final String DEFAULT_TARGETNAME = "subscriptionCache";
    @Override
    public String getRedisTargetName(Object input){
        return DEFAULT_TARGETNAME;
    }
    
    @Override
    public Set<String> getAllRedisTargetNames(){
        TreeSet<String> result = new TreeSet<String>();
        result.add(DEFAULT_TARGETNAME);
        return result;
    }
}
