package com.rapidbackend.socialutil.process.handler.db;

import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.Params;
import com.rapidbackend.socialutil.process.handler.IntermediateDatahandler;
import com.rapidbackend.socialutil.dao.BaseDao;


/**
 * 
 * @author chiqiu
 *
 */
public abstract class DbDataHandler extends IntermediateDatahandler{
    
    protected BaseDao dao;
    
    protected String dBQueryIdListName;
    public void markDBQueryIdList(CommandRequest request,int[] idList){
        request.putTemporaryData(dBQueryIdListName, idList);
    }
    /**
     * use {@link #markDBQueryIdList(CommandRequest, Object)} instead
     * @return
     */
    @Deprecated
    public String getdBQueryIdListName() {
        return dBQueryIdListName;
    }
    public void setdBQueryIdListName(String dBQueryIdListName) {
        this.dBQueryIdListName = dBQueryIdListName;
    }
    protected boolean autowireModelPropertyAsParam;
    
    protected boolean setRecordAsResponse = true;
    
    public boolean isSetRecordAsResponse() {
        return setRecordAsResponse;
    }
    public void setSetRecordAsResponse(boolean setRecordAsResponse) {
        this.setRecordAsResponse = setRecordAsResponse;
    }
    
    public BaseDao getDao() {
        return dao;
    }
    
    public void setDao(BaseDao dao) {
        this.dao = dao;
    }
    
    public boolean isAutowireModelPropertyAsParam() {
        return autowireModelPropertyAsParam;
    }
    public void setAutowireModelPropertyAsParam(boolean autowireModelPropertyAsParam) {
        this.autowireModelPropertyAsParam = autowireModelPropertyAsParam;
    }
    
    /**
     * create a hashmap which contains <param.name, param.value> as its entries.<br/>
     * Note that array params will be skipped, and the param with name "id" or "created" will be skipped too
     * @param request
     * @return 
     */
    
    @SuppressWarnings("rawtypes")
    public HashMap createUpdateValues(CommandRequest request,Class<?> modelClasss){
        Params params = request.getRequestParams();
        Collection<CommandParam> paramSet = params.params();
        HashMap<Object,Object> result = new HashMap<Object, Object>();
        for(CommandParam param : paramSet){
            Object value = param.getData();
            String name = param.getName();
            if(isAlowToUpdate(name)){
                Class<?> fieldType = param.getDataClass();
                if(ModelReflectionUtil.containsField(name, fieldType, modelClasss)){
                    result.put(name, value);
                }
            }
            
        }
        if(result.size()==0){
            throw new IllegalArgumentException("no new model property values to update");
        }
        return result;
    }
    
    private static boolean isAlowToUpdate(String name){
        if(StringUtils.equalsIgnoreCase(name, "id")){// id should never be updated
            return false;
        }
        if(StringUtils.equalsIgnoreCase(name, "created")){// created time should not be updated
            return false;
        }
        return true;
    }
}
