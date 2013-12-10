package com.rapidbackend.socialutil.process.handler.feeds;


import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.process.handler.db.DbDataHandler;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.feeds.DefaultFeedService;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.search.SearchService;
@Deprecated
public class PostFeedHandler extends DbDataHandler{
    protected DefaultFeedService feedService = null;
    protected boolean setCreatedRecordAsResponse = false;
    protected SearchService searchService;
    
    public DefaultFeedService getFeedService() {
        return feedService;
    }
    public void setFeedService(DefaultFeedService feedService) {
        this.feedService = feedService;
    }
    public boolean isSetCreatedRecordAsResponse() {
        return setCreatedRecordAsResponse;
    }
    public void setSetCreatedRecordAsResponse(boolean setCreatedRecordAsResponse) {
        this.setCreatedRecordAsResponse = setCreatedRecordAsResponse;
    }
    
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            Object model = createModelObjectFromRequest(request, dao.getModelClass());
            FeedContentBase feed = (FeedContentBase)model;
           // getFeedService().postFeed(feed);
            Integer indexFeed = getIntParamInRequest(request, "indexFeed");
            if(indexFeed!=null&&indexFeed>0){
                searchService.indexFeed(feed);//TODO add remote index support
            }
            if (setCreatedRecordAsResponse) {
                response.setResult(model);
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName(), e);
        }
    }

}
