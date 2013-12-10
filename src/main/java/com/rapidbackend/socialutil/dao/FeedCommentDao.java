package com.rapidbackend.socialutil.dao;

import java.util.List;

public interface FeedCommentDao extends BaseDao{
    
    public List<?> selectCommentByFeedId(Integer feedId,PagingInfo pagingInfo) throws DataAccessException;
    
}
