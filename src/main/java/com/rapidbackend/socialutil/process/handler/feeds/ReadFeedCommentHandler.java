package com.rapidbackend.socialutil.process.handler.feeds;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.ProcessStatus;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.socialutil.dao.FeedCommentDao;
import com.rapidbackend.socialutil.process.handler.db.DbDataHandler;
import com.rapidbackend.socialutil.util.ParamNameUtil;


public class ReadFeedCommentHandler extends DbDataHandler{
    protected FeedCommentDao commentDao;
    
    
    public FeedCommentDao getCommentDao() {
        return commentDao;
    }
    @Required
    public void setCommentDao(FeedCommentDao commentDao) {
        this.commentDao = commentDao;
    }


    @Override
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        try {
            Integer feedId = getIntParamInRequest(request, ParamNameUtil.FEED_ID);
            Integer start = getIntParamInRequestSilently(request, ParamNameUtil.START);
            Integer pageSize = getIntParamInRequestSilently(request, ParamNameUtil.PAGE_SIZE);
            
            PagingInfo pagingInfo = new PagingInfo(start, pageSize);
            
            List comments = commentDao.selectCommentByFeedId(feedId, pagingInfo);
            
            PagingInfo newPagingInfo = PagingInfo.createPagingInfo(pagingInfo, comments);
            
            response.setPagingInfo(newPagingInfo);
            
            if(comments.size()==0){
                request.setProcessStatus(ProcessStatus.Finished);
            }
            response.setResult(comments);
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
        
    }
}
