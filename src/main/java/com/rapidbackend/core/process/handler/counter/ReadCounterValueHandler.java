package com.rapidbackend.core.process.handler.counter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.model.metadata.FeedMetaData;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.comm.redis.client.RedisClient;
import com.rapidbackend.util.general.ConversionUtils;

/**
 * 
 * @author chiqiu
 */
public class ReadCounterValueHandler extends CounterHandler{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 
     * 
     */
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            
            //HashMap<Integer, FeedMetaData> metadatas = new HashMap<Integer, FeedMetaData>();//the hashmap which stores all metadatas
            int[] fids = getIntListInRequest(request, ParamNameUtil.FEED_IDS);
            List<FeedMetaData> result = new ArrayList<FeedMetaData>();
            if(fids.length>0){
                String[] keyStrings = ConversionUtils.intArrayToStringArray(fids,counterConfig.getCounterKeyPrefix());
                RedisClient redisClient = null;
                try {
                    redisClient = getRedisPoolContainer().borrowClient(counterConfig.getRedisCounterTarget());
                    List<String> countStrings = redisClient.getJedis().mget(keyStrings);
                    int[] counts = ConversionUtils.stringCollentionToIntArray(countStrings);
                    
                    if(counts.length != fids.length){
                        throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"mget result size is not equal to requet size");
                    }
                    
                    for(int j=0;j<counts.length;j++){
                        FeedMetaData metadata = new FeedMetaData();
                        metadata.setId(fids[j]);
                        metadata.setRepostCount(counts[j]);
                        
                        result.add(metadata);
                    }
                    
                } finally {
                    getRedisPoolContainer().returnClient(redisClient, counterConfig.getRedisCounterTarget());
                }
                
                response.setResult(result);
                
            }
            
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, "error during read counter value", e);
            
        }
    }
    
}
