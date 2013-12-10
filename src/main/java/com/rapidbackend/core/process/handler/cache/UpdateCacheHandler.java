package com.rapidbackend.core.process.handler.cache;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.cache.RedisCache;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.core.model.DbRecord;

/**
 * update cache 
 * 
 * TODO change dBQueryIdListName => class(dbQueryIdList:int[],results:HashMap<>)
 * @author chiqiu
 */
public  class UpdateCacheHandler extends ReturnableCacheDataHandler{
   protected Logger logger = LoggerFactory.getLogger(UpdateCacheHandler.class);
    @Override
    @Required
    public void setdBQueryIdListName(String dBQueryIdListName) {
        this.dBQueryIdListName = dBQueryIdListName;
    }
    @Override
    @Required
    public void setGrab(
            String grab) {
        this.grab = grab;
    }
    
    @SuppressWarnings("unchecked")
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        if(redisCache.isCacheReady()){
            try {
                int[] keyList = (int[])request.getTemporaryData(dBQueryIdListName);
                if(keyList!=null&&keyList.length>0){
                    HashMap<Integer, DbRecord> results = (HashMap<Integer, DbRecord>)grab(request);
                    if(results == null){
                        throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName()+":previous handler result is null");
                    }else {
                        for(Integer i: keyList){
                            DbRecord record = results.get(i);
                            if (null== record) {
                                redisCache.setStringWithPrefix(RedisCache.emptyRecord, i,getModelClassNameLowerCase());
                            }else {
                                redisCache.setJsonObjectWithKeyPrefix(record, i,getModelClassNameLowerCase());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName(),e);
            }
        }else {
            logger.debug(getHandlerName() + " cache not ready, skipping this handler");
            request.getCurrentHandleInfo().addMessage(getHandlerName() + " cache not ready, skipping this handler");
        }
    }
}
