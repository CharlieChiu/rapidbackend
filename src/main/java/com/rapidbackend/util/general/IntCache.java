package com.rapidbackend.util.general;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.cache.CacheConfig;
/**
 * TODO add spupport for int array compressing?
 * @author chiqiu
 *
 */
public class IntCache extends BinaryCache{
    Logger logger = LoggerFactory.getLogger(IntCache.class);
    protected int maxIntArraySize = 0;// the largest object size in this cache
    protected int minIntArraySize = 0;// the smallest object size in the cache
    protected FastLRUCache<Integer, int[]> intCache;
    public IntCache(CacheConfig config){
        super(config);
        intCache = new FastLRUCache<Integer, int[]>();
        intCache.init(config.getCacheSize(), config.getName()+"intCache");
        cacheContainer.put("intCache", intCache);
    }
    public boolean put(Integer key,int[] value){
        if(value!=null){
            
            int lenth = value.length;
            maxIntArraySize = Math.max(lenth, maxIntArraySize);
            minIntArraySize = Math.min(lenth, minIntArraySize);
            
            boolean res = intCache.put(key, value)!=null;
            return res;
        }else{
            return false;
        }
    }
    
    public int[] get(Integer key){
        int[] res = intCache.get(key);
        if(res!=null){
            markAsUnchangedAfterReading(key);// always mark it as unchanged after getting
        }
        return intCache.get(key);
    }
    
    public int getIntCacheSize(){
        return intCache.size();
    }
    public int getMaxIntArraySize() {
        return maxIntArraySize;
    }
    
    public int getMinIntArraySize() {
        return minIntArraySize;
    }
    
    
}
