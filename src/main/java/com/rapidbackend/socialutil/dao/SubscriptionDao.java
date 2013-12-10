package com.rapidbackend.socialutil.dao;

import java.util.List;

import com.rapidbackend.socialutil.model.reserved.Subscription;

public interface SubscriptionDao extends BaseDao{
    
    public List<Subscription> selectSubscriptionsByFollowableId(Integer followableId,PagingInfo pagingInfo) throws DataAccessException;
}
