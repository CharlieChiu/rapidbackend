package com.rapidbackend.socialutil.sharding.defaultmappers;

import java.util.Set;
import java.util.TreeSet;

import com.rapidbackend.cache.CacheMapper;

public class SessionCacheMapper implements CacheMapper{
    public String DEFAULT_TARGETNAME = "sessionStore";
    @Override
    public String getRedisTargetName(Object input){
        return "sessionStore";
    }
    
    @Override
    public Set<String> getAllRedisTargetNames(){
        TreeSet<String> result = new TreeSet<String>();
        result.add(DEFAULT_TARGETNAME);
        return result;
    }
}