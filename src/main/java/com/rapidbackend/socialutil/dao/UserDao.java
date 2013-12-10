package com.rapidbackend.socialutil.dao;

import java.util.List;

import com.rapidbackend.socialutil.model.reserved.Subscription;
import com.rapidbackend.socialutil.model.reserved.UserBase;

public interface UserDao extends FollowableDao{
    
    public UserBase getUserByScreenName(String screenName) throws DataAccessException;
    
    public UserBase getUserByEmail(String email) throws DataAccessException;
    
    public List<UserBase> selectFollowersBySubscription(List<Subscription> subscriptions) throws DataAccessException;
}
