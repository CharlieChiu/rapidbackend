package com.rapidbackend.socialutil.dao.mysql;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.dao.DataAccessException;

public class FeedDao extends BaseDao implements com.rapidbackend.socialutil.dao.FeedDao{
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    protected String selectFeedIdByFollowableSql = "select feedId FROM xxxx where feedId <";
    RecordOrder order = new RecordOrder(DisplayOrder.desc, OrderByColumn.id);// id is always indexed
    
    public String genSelectFeedIdByFollowableSql(PagingInfo pagingInfo,Integer followableId){
        StringBuffer sb = new StringBuffer("select feedId FROM ");
        sb.append(getTableName());
        sb.append(" where id < ");
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
        if(start == 0 && order.getOrder() == DisplayOrder.desc){
            start = Integer.MAX_VALUE;
        }
        sb.append(start);
        sb.append(" and followableId=").append(followableId);
        sb.append(" ").append(order.toString());
        sb.append(" limit ").append(pageSize);
        sb.append(";");
        logger.debug(sb.toString());
        return sb.toString();
    }
    
    public String genSelectFeedIdByRepostfeedIdSql(PagingInfo pagingInfo,Integer repostToFeedId){
        StringBuffer sb = new StringBuffer("select feedId FROM ");
        sb.append(getTableName());
        sb.append(" where repostToFeedId=").append(repostToFeedId);
        Integer pageSize = pagingInfo.getPageSize();
        Integer start = pagingInfo.getStart();
        if(null == start){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param start is null");
        }
        if(null == pageSize){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param pageSize is null");
        }
        if(repostToFeedId == null){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param repostToFeedId is null");
        }
        if(start == 0 && order.getOrder() == DisplayOrder.desc){
            start = Integer.MAX_VALUE;
        }
        sb.append(" and id<").append(start);
        sb.append(" ").append(order.toString());
        sb.append(" limit ").append(pageSize);
        sb.append(";");
        logger.debug(sb.toString());
        return sb.toString();
    }

    @Override
    public List<Integer> selectFeedIdByFollowableId(Integer followableId, PagingInfo pagingInfo) throws DataAccessException{
        String selectString = genSelectFeedIdByFollowableSql(pagingInfo,followableId);
        List<Integer> ids = new ArrayList<Integer>();// init with an object, don't return null. it is good to have a Some[] in scala
        try {
            ids = jdbcTemplate.query(selectString, new SingleColumnRowMapper<Integer>());
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("no results fetched for "+selectString);
        }
        return ids;
    }
    @Override
    public List<Integer> selectRepostIdsByFeedId(Integer repostToFeedId,PagingInfo pagingInfo) throws DataAccessException{
        String selectString = genSelectFeedIdByRepostfeedIdSql(pagingInfo, repostToFeedId);
        List<Integer> ids = new ArrayList<Integer>();// init with an object, don't return null. it is good to have a Some[] in scala
        try {
            ids = jdbcTemplate.query(selectString, new SingleColumnRowMapper<Integer>());
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("no results fetched for "+selectString);
        }
        return ids;
    }
}
