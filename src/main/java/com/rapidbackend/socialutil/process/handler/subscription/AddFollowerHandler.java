package com.rapidbackend.socialutil.process.handler.subscription;


import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.time.SimpleTimer;

public class AddFollowerHandler extends SubscriptionHandler{
    
    
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            /**
             * we don't need this , check them in the schema?
             */
            String followableParam = getInputFollowableIdParamName() == null ?ParamNameUtil.FOLLOWABLE:getInputFollowableIdParamName();
            String followerParam = getInputFollowerIdParamName()==null ? ParamNameUtil.FOLLOWER:getInputFollowerIdParamName();
            Integer followable = getIntParamInRequest(request, followableParam);
            Integer follower = getIntParamInRequest(request, followerParam);
            if(followable!=null&& follower!=null){
                
                SimpleTimer timer = new SimpleTimer("insert subscription into db");
                Object model = createModelObjectFromRequest(request, subscriptionClassName);
                /*
                subscriptionService.getSubscriptionDao().storeNewModelBean(model);
                */
                subscriptionService.addSubscription(followable, follower);
                
                timer.stop();
                request.getCurrentHandleInfo().addMessage(timer.getIntervalString());
                
                if(subscriptionService.isSubscriptionCacheConfigured()){
                    SimpleTimer cacheTimer = new SimpleTimer("syncronize add to cache");
                    subscriptionService.addFollowerToSubsciptionCache(follower, followable, (DbRecord)model);
                    cacheTimer.stop();
                    request.getCurrentHandleInfo().addMessage(cacheTimer.getIntervalString());
                }
                
            }else {
                throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,getHandlerName()+": cannot find input ids!");
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName(), e);
        }
    }
}
