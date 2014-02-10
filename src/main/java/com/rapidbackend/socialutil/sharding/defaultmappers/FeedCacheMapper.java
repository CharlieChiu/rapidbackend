package com.rapidbackend.socialutil.sharding.defaultmappers;

import java.util.Set;
import java.util.TreeSet;

import com.rapidbackend.cache.CacheMapper;

public class FeedCacheMapper implements CacheMapper{
    protected static final String DEFAULT_TARGET = "feedCache";
    @Override
    public String getRedisTargetName(Object input){
        return DEFAULT_TARGET;
    }
    
    @Override
    public Set<String> getAllRedisTargetNames(){
        TreeSet<String> result = new TreeSet<String>();
        result.add(DEFAULT_TARGET);
        return result;
    }
}
