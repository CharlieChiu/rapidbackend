package com.rapidbackend.socialutil.process.handler.subscription;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.socialutil.dao.UserDao;
import com.rapidbackend.socialutil.model.reserved.Subscription;
import com.rapidbackend.socialutil.util.ParamNameUtil;

//TODO check if we need to add bloomfilter here
public class GetFollowerListHandler extends SubscriptionHandler{
    protected UserDao userDao;
    
    
    public UserDao getUserDao() {
        return userDao;
    }
    @Required
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            
            Integer followable = getIntParamInRequest(request, ParamNameUtil.FOLLOWABLE);
            Integer start = getIntParamInRequestSilently(request,ParamNameUtil.START);
            Integer pageSize = getIntParamInRequestSilently(request,ParamNameUtil.PAGE_SIZE);
            
            PagingInfo inputPagingInfo = new PagingInfo(start, pageSize);
            
            if(followable!=null){
                List<Subscription> subscriptions = subscriptionService.getFollowersIdsFromDatabase(followable, inputPagingInfo);
                int size = subscriptions.size();
                
                if(size > 0){
                     List<?> users = userDao.selectFollowersBySubscription(subscriptions);// TODO add cache support here
                     PagingInfo newPagingInfo = PagingInfo.createPagingInfo(inputPagingInfo, subscriptions);// remember to input subscription ids here
                     response.setPagingInfo(newPagingInfo);
                     response.setResult(users);
                                          
                }else {
                    PagingInfo newPagingInfo = PagingInfo.createPagingInfo(inputPagingInfo, null);
                    response.setPagingInfo(newPagingInfo);
                    response.setResult(null);
                }
                
                /*
                 * 
                 * work flow:
                 * 1. get follower ids
                 * 2. from follower ids to followers
                 */
                /*
                boolean gotCachedItems = false;
                boolean gotDbItems = false;
                if(subscriptionService.isSubscriptionCacheConfigured()){
                    SimpleTimer operationTimer = new SimpleTimer("read subscription cache");
                    int[] ids = subscriptionService.getFollowerListFromCache(followedId);
                    
                    if(ids!=null&& ids.length>0){
                        gotCachedItems = true;
                        setHandlerResult(request,ids);
                    }
                    
                    operationTimer.stop();
                    request.getCurrentHandleInfo().addMessage(operationTimer.getIntervalString());
                }
                if(!gotCachedItems){
                    SimpleTimer operationTimer = new SimpleTimer("read subscriptions from db");
                    subsctiptions = (List<DbRecord>)subscriptionService.getAllFollowersIdsFromDatabase(followedId);
                    int[] ids = ConversionUtils.socialDbRecordCollectionIdToIntArray(subsctiptions);
                    if(ids!=null&& ids.length>0){
                        gotDbItems = true;
                    }
                    setHandlerResult(request,ids);
                    operationTimer.stop();
                    request.getCurrentHandleInfo().addMessage(operationTimer.getIntervalString());
                }
                if(gotDbItems){
                    SimpleTimer operationTimer = new SimpleTimer("write all followers to cache,item number:"+subsctiptions.size());
                    Map<Double, String> scoremembers = subscriptionService.createScoreMembers(subsctiptions);
                    subscriptionService.getSubscriptionCache().zaddMulti(followedId.toString(), scoremembers, followedId);
                    operationTimer.stop();
                    request.getCurrentHandleInfo().addMessage(operationTimer.getIntervalString());
                }
                
                StringBuffer sb = new StringBuffer();
                int[] ids = subscriptionService.getFollowerList(followedId, sb);
                request.getCurrentHandleInfo().addMessage(sb.toString());
                if(ids!=null&& ids.length>0){
                    setHandlerResult(request,ids);
                }*/
            }else {
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName()+": cannot find input followable id!");
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName(), e);
        }
    }
    
}
