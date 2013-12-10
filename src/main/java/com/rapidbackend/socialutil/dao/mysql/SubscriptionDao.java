package com.rapidbackend.socialutil.dao.mysql;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.dao.DataAccessException;
import com.rapidbackend.socialutil.model.reserved.Subscription;

public class SubscriptionDao extends BaseDao implements com.rapidbackend.socialutil.dao.SubscriptionDao{

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    RecordOrder order = new RecordOrder(DisplayOrder.asc, OrderByColumn.id);// id is always indexed
        
    public String genSelectFeedIdByFollowableSql(PagingInfo pagingInfo,Integer followableId){
        
        Integer pageSize = pagingInfo.getPageSize();
        Integer start = pagingInfo.getStart();
        if(null == start){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param start is null");
        }
        if(null == pageSize){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param pageSize is null");
        }
        if(followableId == null){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param followableId is null");
        }
        
        StringBuffer sb = new StringBuffer("SELECT * FROM ");
        sb.append(getTableName());
        sb.append(" where followable=");
        sb.append(followableId);
        sb.append(" and id>").append(start);
        sb.append(order.toString());
        sb.append(" limit ").append(pageSize);
        sb.append(";");
        logger.debug(sb.toString());
        return sb.toString();
    }
    
    @Override
    public List<Subscription> selectSubscriptionsByFollowableId(Integer followableId,PagingInfo pagingInfo) throws DataAccessException{
        String selectString = genSelectFeedIdByFollowableSql(pagingInfo,followableId);
        List<Subscription> subscriptions = new ArrayList<Subscription>();
        try {
            subscriptions = jdbcTemplate.query(selectString, ParameterizedBeanPropertyRowMapper.newInstance(Subscription.class));
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("no results fetched for "+selectString);
        }
        return subscriptions;
    }

}