package com.rapidbackend.socialutil.dao.mysql;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.dao.DataAccessException;

public class FeedCommentDao extends BaseDao implements com.rapidbackend.socialutil.dao.FeedCommentDao{
    private Logger logger = LoggerFactory.getLogger(getClass());
    protected String selectFeedIdByFollowableSql = "select * FROM xxxx where feedId=";
    RecordOrder order = new RecordOrder(DisplayOrder.desc, OrderByColumn.id);// id is always indexed
    
    public String genSelectCommentByFeedIdSql(PagingInfo pagingInfo,Integer feedId){
        StringBuffer sb = new StringBuffer("select * FROM ");
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
        if(feedId == null){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"param feedId is null");
        }
        if(start == 0 && order.getOrder() == DisplayOrder.desc){
            start = Integer.MAX_VALUE;
        }
        sb.append(start);
        sb.append(" and feedId=").append(feedId);
        sb.append(" ").append(order.toString());
        sb.append(" limit ").append(pageSize);
        sb.append(";");
        logger.debug(sb.toString());
        return sb.toString();
    }
    @Override
    public List<?> selectCommentByFeedId(Integer feedId,PagingInfo pagingInfo) throws DataAccessException{
        String selectString = genSelectCommentByFeedIdSql(pagingInfo,feedId);
        List results = new ArrayList();// init with an object, don't return null. it is good to have a Some[] in scala
        try {
            results = selectListBySql(selectString, getModelClass());
        } catch (EmptyResultDataAccessException ex) {
            logger.warn("no results fetched for "+selectString);
        }
        return results; 
    }
}
