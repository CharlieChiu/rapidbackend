package com.rapidbackend.socialutil.process.handler.db;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.QueryParam;
import com.rapidbackend.core.request.QueryParam.QueryPartial;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.BaseDao;
import com.rapidbackend.socialutil.dao.BaseDao.DisplayOrder;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class QueryDbRecordHandler extends DbDataHandler{
    
    @Required
    @Override
    public void setDao(BaseDao dao) {
        this.dao = dao;
    }
    
    @Override
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        try {
            String query = getStringParamInRequest(request, ParamNameUtil.MODEL_QUERY);
            if(StringUtils.isEmpty(query)){
               throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"no valid query found: "+query);
            }
            QueryParam queryParam = new QueryParam(query);
            List<QueryPartial> queryPartials = queryParam.getQueryPartials();
            
            String order = getStringParamInRequest(request, ParamNameUtil.ID_ORDER);
            DisplayOrder displayOrder = DisplayOrder.desc;
            if(order!=null && order.equalsIgnoreCase("asc")){
                displayOrder = DisplayOrder.asc;
            }
            
            Integer start = getIntParamInRequestSilently(request, ParamNameUtil.START);
            Integer pageSize = getIntParamInRequestSilently(request, ParamNameUtil.PAGE_SIZE);
                    
            PagingInfo pagingInfo = new PagingInfo(start, pageSize);
            
            String querySql = dao.createQuerySqlWithQueryPartials(queryPartials, pagingInfo, displayOrder);
            
            List<?> result = dao.selectListBySql(querySql, dao.getModelClass());
            
            response.setResult(result);
            
            PagingInfo newPagingInfo = PagingInfo.createPagingInfo(pagingInfo, result);
            
            response.setPagingInfo(newPagingInfo);
            
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName(), e);
        }
    }
}
