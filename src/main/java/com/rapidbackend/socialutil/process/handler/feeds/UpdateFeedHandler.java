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

public class UpdateFeedHandler extends DbDataHandler{
    protected FeedService feedService;

    @Required
    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }

    @Override
    public void handle(CommandRequest request,CommandResponse response){
        try {
            
            //Integer userId = getCurrentUserId(request);// keep this line as a check?
            Integer id = getIntParamInRequest(request, ParamNameUtil.ID);
            
            HashMap<?, ?> updates = createUpdateValues(request, feedService.getFeedContentClass());
            FeedContentBase feed = feedService.updateFeed(updates,id);
            
            //store info for update cache handler
            HashMap<Integer, DbRecord> updateInCache = new HashMap<Integer, DbRecord>();
            updateInCache.put(feed.getId(), feed);
            yield(request, feed);// link deleted records ids withs null
            
            int[] idList = new int[]{feed.getId()};
            markDBQueryIdList(request, idList);// TODO encapsulate the dbQueryList and dbrecords into a single object for cleaner yield and grab
            // there should be a class FromCache and a class ToCache
            
            response.setResult(feed);
        } catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error deleting followable feed",e);
        }
    }
}
