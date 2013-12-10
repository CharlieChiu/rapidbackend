package com.rapidbackend.core.process.handler.cache;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.cache.RedisCache;
import com.rapidbackend.core.process.handler.cache.CacheAware;
import com.rapidbackend.socialutil.model.util.TypeFinder;
import com.rapidbackend.socialutil.process.handler.IntermediateDatahandler;

public abstract class ReturnableCacheDataHandler extends IntermediateDatahandler implements CacheAware{
    protected RedisCache redisCache;
    protected String modelClassName;
    
    @Required
    public void setModelTypeFinder(TypeFinder modelTypeFinder) {
        this.modelTypeFinder = modelTypeFinder;
    }
    public String getModelClassName() {
        return modelClassName;
    }
    
    protected String modelClassNameLowerCase = null;
    
    public String getModelClassNameLowerCase() {
        if(modelClassNameLowerCase == null){
            modelClassNameLowerCase = modelClassName.toLowerCase();
        }
        return modelClassNameLowerCase;
    }
    
    @Required
    public void setModelClassName(String modelClassName) {
        this.modelClassName = modelClassName;
    }
    public RedisCache getRedisCache() {
        return redisCache;
    }
    @Required
    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
    
}
