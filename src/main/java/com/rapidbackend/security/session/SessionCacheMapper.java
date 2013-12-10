package com.rapidbackend.security.session;

import java.util.Set;
import java.util.TreeSet;

import com.rapidbackend.cache.CacheMapper;

public class SessionCacheMapper implements CacheMapper{
    protected static String DEFAULT_TARGET = "sessioStore";
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
