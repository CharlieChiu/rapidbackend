package com.rapidbackend.socialutil.process.handler.feeds;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.ProcessStatus;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.FeedDao;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.socialutil.process.handler.IntermediateDatahandler;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class GetFeedIdByFollowableHandler extends IntermediateDatahandler{
    Logger logger = LoggerFactory.getLogger(GetFeedIdByFollowableHandler.class);
        
    protected FeedDao feedDao;
    
    public FeedDao getFeedDao() {
        return feedDao;
    }
    @Required
    public void setFeedDao(FeedDao feedDao) {
        this.feedDao = feedDao;
    }

    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        Integer followableId = getIntParamInRequest(request, ParamNameUtil.ID);
        Integer start = getIntParamInRequestSilently(request, ParamNameUtil.START);
        Integer pageSize = getIntParamInRequestSilently(request, ParamNameUtil.PAGE_SIZE);
        
        
        PagingInfo pagingInfo = new PagingInfo(start, pageSize);
        List<Integer> ids = feedDao.selectFeedIdByFollowableId(followableId, pagingInfo);
        
        PagingInfo newPagingInfo = PagingInfo.createPagingInfo(pagingInfo, ids);
        
        response.setPagingInfo(newPagingInfo);
        
        if(ids.size()==0){
            request.setProcessStatus(ProcessStatus.Canceled);
            response.setResult(null);
        }
        yield(request, ids);
    }
    
    
}
