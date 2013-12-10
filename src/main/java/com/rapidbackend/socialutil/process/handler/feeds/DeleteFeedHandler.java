package com.rapidbackend.socialutil.process.handler.feeds;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.feeds.service.FeedService;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.process.handler.db.DbDataHandler;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class DeleteFeedHandler extends DbDataHandler{
    protected FeedService feedService;
    
    public FeedService getFeedService() {
        return feedService;
    }
    @Required
    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }
    
    @Override
    @Required
    public void setdBQueryIdListName(String dBQueryIdListName) {
        this.dBQueryIdListName = dBQueryIdListName;
    }
    
    @Override
    @Required
    public void setYield(String yield){
        this.yield = yield;
    }

    @Override
    public void handle(CommandRequest request,CommandResponse response){
        try {
            Integer userId = getIntParamInRequestSilently(request, ParamNameUtil.USER_ID);
            
            if(userId == null){
                userId = getCurrentUserId(request);
            }
            
            Integer followableId = getIntParamInRequest(request, ParamNameUtil.Followable_ID);
            Integer feedId = getIntParamInRequest(request, ParamNameUtil.ID);
            FeedContentBase feed = feedService.deleteFeed(feedId, userId, followableId);
            
            //store info for update cache handler
            HashMap<Integer, DbRecord> toDelete = new HashMap<Integer, DbRecord>();
            toDelete.put(feedId, null);
            yield(request, toDelete);// link deleted records ids withs null
            
            setIdListParam(request, new int[]{feedId});
            Integer repostToFeedId = feed.getRepostToFeedId();
            if(repostToFeedId!= null && repostToFeedId>0){
                request.putTemporaryData(ParamNameUtil.REPOST_TO_FEED_ID, repostToFeedId);
            }
            response.setResult(feed);
        } catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error deleting followable feed",e);
        }
    }
}
