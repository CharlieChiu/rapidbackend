package com.rapidbackend.socialutil.process.handler.feeds;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.ProcessStatus;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.FeedDao;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.socialutil.process.handler.db.DbDataHandler;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class GetRepostIdHandler extends DbDataHandler{
    protected FeedDao feedDao;
    
    public FeedDao getFeedDao() {
        return feedDao;
    }
    @Required
    public void setFeedDao(FeedDao feedDao) {
        this.feedDao = feedDao;
    }

    @Override
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        Integer repostToFeedId = getIntParamInRequest(request, ParamNameUtil.REPOST_TO_FEED_ID);
        Integer start = getIntParamInRequestSilently(request, ParamNameUtil.START);
        Integer pageSize = getIntParamInRequestSilently(request, ParamNameUtil.PAGE_SIZE);
        
        PagingInfo pagingInfo = new PagingInfo(start, pageSize);
        
        List<Integer> ids = feedDao.selectRepostIdsByFeedId(repostToFeedId, pagingInfo);
        
        PagingInfo newPagingInfo = PagingInfo.createPagingInfo(pagingInfo, ids);
        
        response.setPagingInfo(newPagingInfo);
        
        if(ids.size()==0){
            request.setProcessStatus(ProcessStatus.Canceled);
            response.setResult(null);
        }
        yield(request, ids);
    }
    
}
