package com.rapidbackend.socialutil.dao;

import java.util.List;

import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;

/**
 * feed dao is the dao object to get all the feed ids belong to one followable from the 'feed' table
 * @author chiqiu
 *
 */
public interface FeedDao extends BaseDao{
    
    public List<Integer> selectFeedIdByFollowableId(Integer followableId, PagingInfo pagingInfo) throws DataAccessException;
    
    public List<Integer> selectRepostIdsByFeedId(Integer repostToFeedId,PagingInfo pagingInfo) throws DataAccessException;
}
