package com.rapidbackend.socialutil.process.handler.subscription;


import org.junit.Test;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.FollowableDao;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.time.SimpleTimer;

public class UnsubscribeHandler extends SubscriptionHandler{
    protected FollowableDao followableDao;
    
    public FollowableDao getFollowableDao() {
        return followableDao;
    }
    @Test
    public void setFollowableDao(FollowableDao followableDao) {
        this.followableDao = followableDao;
    }

    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            /**
             * 
             */
            Integer followableId = getIntParamInRequest(request, ParamNameUtil.FOLLOWABLE);
            Integer followerId = getIntParamInRequest(request, ParamNameUtil.FOLLOWER);
            if(followableId!=null&& followerId!=null){//we don't need this , check them in the schema?
                SimpleTimer timer = new SimpleTimer("delete subscription from db and inbox");
                
                subscriptionService.removeSubscription(followableId, followerId);
                timer.stop();
                request.getCurrentHandleInfo().addMessage(timer.getIntervalString());
                
                if(subscriptionService.isSubscriptionCacheConfigured()){
                    SimpleTimer cacheTimer = new SimpleTimer("syncronize delete to cache");
                    subscriptionService.getSubscriptionCache().zrem(followableId.toString(), new String[]{}, followableId);
                    cacheTimer.stop();
                    request.getCurrentHandleInfo().addMessage(cacheTimer.getIntervalString());
                }
                
                SimpleTimer readDbTimer = new SimpleTimer("read followable from db");
                
                Object followable = followableDao.selectById(followableId, getModelClass(followableDao.getTableName()));
                
                readDbTimer.stop();
                request.getCurrentHandleInfo().addMessage(readDbTimer.getIntervalString());
                
                response.setResult(followable);
                
            }else {
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName()+": cannot find input ids!");
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName(), e);
        }
    }
}
