package com.rapidbackend.socialutil.dao.mysql;

import java.util.HashMap;
import java.util.List;



import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.socialutil.dao.DataAccessException;
import com.rapidbackend.socialutil.model.reserved.Subscription;
import com.rapidbackend.socialutil.model.reserved.UserBase;

public class UserDao extends BaseDao implements com.rapidbackend.socialutil.dao.UserDao{
    @Override
    public UserBase getUserByScreenName(String screenName) throws DataAccessException{
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("screenName", screenName);
        return (UserBase)selectSingleRecordByColumns(params, ModelReflectionUtil.getUserClass());
    }
    @Override
    public UserBase getUserByEmail(String email) throws DataAccessException{
        HashMap<Object, Object> params = new HashMap<Object, Object>();
        params.put("email", email);
        return (UserBase)selectSingleRecordByColumns(params, ModelReflectionUtil.getUserClass());
    }
    @Override
    public List selectFollowersBySubscription(List<Subscription> subscriptions) throws DataAccessException{
        int[] followerIds = new int[subscriptions.size()];
        for(int i =0;i<subscriptions.size();i++){
            int followerid = subscriptions.get(i).getFollower();
            followerIds[i] = followerid;
        }
        Class<?> userClass = ModelReflectionUtil.getUserClass();
        List result = selectByIds(followerIds, userClass);
        return result;
    }
}
