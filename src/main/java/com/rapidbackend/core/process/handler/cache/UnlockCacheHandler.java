package com.rapidbackend.core.process.handler.cache;


import org.springframework.beans.factory.annotation.Required;


import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

public class UnlockCacheHandler extends CacheDataHandler{
    
    protected String unlockIdListName;
    
    public String getUnlockIdListName() {
        return unlockIdListName;
    }
    @Required
    public void setUnlockIdListName(String unlockIdListName) {
        this.unlockIdListName = unlockIdListName;
    }

    /**
     * locks some ids in the redis cache
     */
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            int[] ids = (int[])request.getTemporaryData( getUnlockIdListName());
            if(ids!=null && ids.length>0){
                getRedisCache().unlock(ids);
            }else {
                //throw new SocialUtilRuntimeException(SocialErrorCodes.ErrorInCacheUnLockHandler,"unlock id list is empty");
            }
        } catch (Exception e) {
            if( e instanceof BackendRuntimeException){
                throw (BackendRuntimeException)e;
            }else {
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR, "CacheUnLockHandler", e);
            }
        }
        
    }
}
