package com.rapidbackend.core.process.handler.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * locks some ids of a redis cache, locked ids are marked for updating
 * @author chiqiu
 *
 */

public class LockCacheHandler extends CacheDataHandler{
    Logger logger = LoggerFactory.getLogger(LockCacheHandler.class);
    protected String lockIdListName;
    
    public String getLockIdListName() {
        return lockIdListName;
    }
    @Required
    public void setLockIdListName(String lockIdListName) {
        this.lockIdListName = lockIdListName;
    }
    
    /**
     * locks some ids in the redis cache
     */
    
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            int[] ids = (int[])request.getTemporaryData(getLockIdListName());
            if(ids !=null){
                logger.debug("locked ids size is "+ids.length);
            }else {
                logger.debug("locked ids "+ids);
            }
            
            if(ids!=null && ids.length>0){
                getRedisCache().lock(ids);
            }else {
                //throw new SocialUtilRuntimeException(SocialErrorCodes.ErrorInCacheLockHandler,"lock id list is empty");
            }
        } catch (Exception e) {
            if( e instanceof BackendRuntimeException){
                throw (BackendRuntimeException)e;
            }else {
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName(), e);
            }
        }
    }
}
