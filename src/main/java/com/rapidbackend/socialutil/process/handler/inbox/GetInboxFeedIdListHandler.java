package com.rapidbackend.socialutil.process.handler.inbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;





import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.process.handler.IntermediateDatahandler;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.feeds.InboxService;

/**
 * @author chiqiu
 */
public class GetInboxFeedIdListHandler extends IntermediateDatahandler{
    Logger logger = LoggerFactory.getLogger(GetInboxFeedIdListHandler.class);
    protected InboxService inboxService;
    
    protected Integer defaultStart = 0;
    protected Integer defaultPageSize = 20;
    
    @Required
    public void setInboxService(InboxService inboxService) {
        this.inboxService = inboxService;
    }
    protected InboxService getInboxService() {
        return inboxService;
    }
    
    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            Integer userId = getIntParamInRequest(request, ParamNameUtil.USER_ID);
            Integer start = getIntParamInRequestSilently(request, ParamNameUtil.START);
            Integer pageSize = getIntParamInRequestSilently(request, ParamNameUtil.PAGE_SIZE);
            if(!getInboxService().isUserInboxReady(userId)){
                logger.debug("ibox not readey for "+ userId );
                getInboxService().createInboxFromDb(userId);
            }
            if(null == start){
                start = defaultStart;
            }
            if(null == pageSize){
                pageSize = defaultPageSize;
            }
            
            int[] feedIds = inboxService.getFeedIdsFromInbox(userId, start, pageSize);
            
            logger.debug("feedIds size "+feedIds.length);
            logger.debug("put it into "+handlerResultContainerObjectName);
            yield(request, feedIds);
            // when get the first page of feed ids, get one more than the page size in case we get a reserved empty item
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName(), e);
        }
    }
}
