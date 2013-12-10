package com.rapidbackend.socialutil.process.handler.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.SeqCommandParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.dao.BaseDao;
import com.rapidbackend.socialutil.dao.BaseDao.DisplayOrder;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.general.Tuple;

/**
 * query records against one model class
 * @author chiqiu
 *
 */
public class QueryMultipleDbRecordHandler extends DbDataHandler{
    
    Logger logger = LoggerFactory.getLogger(QueryMultipleDbRecordHandler.class);
    
    public void setBaseDao(BaseDao dao){
        this.dao =dao;
    }
    
    static String[] queryOperators = new String[]{">=","<=",">","<"};
    static String DEFAULT_OPERATOR = "=";
    
    protected String queryParams;
    
    public String getQueryParams() {
        return queryParams;
    }
    @Required
    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    private List<Tuple<CommandParam, String>> queryPartials = null;
    
    
    public List<Tuple<CommandParam, String>> getQueryPartials() {
        if(queryPartials == null){
            queryPartials = new ArrayList<Tuple<CommandParam,String>>();
            String[] params = queryParams.split(",");
            for(String param:params){
                if(!StringUtils.isBlank(param)){
                    param = param.replaceAll(" ", "");
                    String beanId = null;
                    String op = null;
                    for(String operator:queryOperators){
                        if(StringUtils.endsWith(param, operator)){
                            beanId = StringUtils.removeEnd(param, operator);
                            op = operator;
                            break;
                        }
                    }
                    if(beanId == null){
                        beanId = param;
                        op = DEFAULT_OPERATOR;
                    }
                    Object paramBean =null;
                    try {
                        paramBean = getApplicationContext().getBean(beanId);
                    } catch (Exception e) {
                    }
                    
                    if(paramBean == null){
                        throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"no bean defination for query param "+ beanId);
                    }
                    if(!(paramBean instanceof CommandParam)){
                        throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"bean defination for query param "+ beanId + " is not a commandparm bean");
                    }
                    if(paramBean instanceof SeqCommandParam){
                        throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"no support fot seqcommandparam currently :"+ beanId + " is a seqcommandparam bean");
                    }
                    CommandParam predefinedParam = (CommandParam) paramBean;
                    
                    Tuple<CommandParam, String> queryPartial = new Tuple<CommandParam, String>(predefinedParam, op);
                    queryPartials.add(queryPartial);
                    
                    
                }else {
                    throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"bad format in you query param config:"+ queryParams);
                }
                
            }
        }
        if(queryPartials == null || queryPartials.size() == 0){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"no query partials can be found, check your query config:"+ queryParams);
        }
        return queryPartials;
    }
    /**
     * order by id desc by default
     */
    DisplayOrder displayOrder = DisplayOrder.desc;
    
    
    public DisplayOrder getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(DisplayOrder displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    @Override
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        try {
            Integer start = getIntParamInRequestSilently(request, ParamNameUtil.START);
            Integer pageSize = getIntParamInRequestSilently(request, ParamNameUtil.PAGE_SIZE);
                    
            PagingInfo pagingInfo = new PagingInfo(start, pageSize);
            
            String querySql = dao.createQuerySqlFromQueryPartials(getQueryPartials(), request,pagingInfo,displayOrder);
            logger.debug(querySql);
            List<?> result = dao.selectListBySql(querySql, dao.getModelClass());
            
            response.setResult(result);
            
            PagingInfo newPagingInfo = PagingInfo.createPagingInfo(pagingInfo, result);
            
            response.setPagingInfo(newPagingInfo);
            
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
        
        
    }
    
    
    
}
