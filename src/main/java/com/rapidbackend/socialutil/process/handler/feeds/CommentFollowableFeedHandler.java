package com.rapidbackend.socialutil.process.handler.feeds;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.FeedCommentDao;
import com.rapidbackend.socialutil.model.reserved.CommentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.process.handler.db.DbDataHandler;

public class CommentFollowableFeedHandler extends DbDataHandler{
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
            CommentBase comment = (CommentBase)createModelObjectFromRequest(request, commentDao.getModelClass());
            UserBase user = request.getUser();
            comment.setScreenName(user.getScreenName());
            int id = commentDao.storeNewModelBean(comment);
            comment.setId(id);
            response.setResult(comment);
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
        
        
    }
}
