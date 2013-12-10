package com.rapidbackend.socialutil.process.handler.feeds;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.feeds.service.FeedService;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.process.handler.db.DbDataHandler;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class PostFollowableFeedHandler extends DbDataHandler{
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
            FeedContentBase feedContentBase = (FeedContentBase)createModelObjectFromRequest(request, feedService.getFeedContentClass());// model class should be the correct feedcontent class
            Integer userId = getIntParamInRequest(request, ParamNameUtil.USER_ID);
            Integer followableId = getIntParamInRequest(request, ParamNameUtil.Followable_ID);
            FeedContentBase feed = feedService.postFeed(feedContentBase, userId, followableId);
            response.setResult(feed);
            /*
            if(model instanceof DbRecord){
                ((DbRecord) model).setId(id);
            }
            if (setRecordAsResponse) {
                response.setResult(model);
            }*/
        } catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.InternalServerError,getHandlerName(),e);
        }
    }
    
    
}
