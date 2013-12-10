package com.rapidbackend.socialutil.process.handler.feeds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.feeds.service.FeedService;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.process.handler.db.DbDataHandler;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class RepostFollowableFeedHandler extends DbDataHandler{
    Logger logger = LoggerFactory.getLogger(RepostFollowableFeedHandler.class);
    
    protected FeedService feedService;
    public FeedService getFeedService() {
        return feedService;
    }
    @Required
    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }
        
    @Override
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        try {
            ;
            Integer userId = getIntParamInRequest(request, ParamNameUtil.USER_ID);
            Integer followableId = getIntParamInRequest(request, ParamNameUtil.Followable_ID);
            Integer repostToFeedId = getIntParamInRequest(request, ParamNameUtil.REPOST_TO_FEED_ID);
            String content =  getStringParamInRequest(request, ParamNameUtil.CONTENT);
            Class<? extends FeedContentBase> feedContentClass =(Class<? extends FeedContentBase>) feedService.getFeedContentClass();
            FeedContentBase feed = feedService.repost(repostToFeedId, userId, followableId, content,feedContentClass);
            response.setResult(feed);
            
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
    }
    
    
}
