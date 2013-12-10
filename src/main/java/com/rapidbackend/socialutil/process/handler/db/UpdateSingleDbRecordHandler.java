package com.rapidbackend.socialutil.process.handler.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.handler.cache.CacheTrigger;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.core.model.DbRecord;

/**
 * @author chiqiu
 */

public class UpdateSingleDbRecordHandler extends DbDataHandler implements CacheTrigger{
    
    protected String queryParamMapObjectName;
    protected String updateValueMapObjectName;
    protected String keysToUpdateInCache;
    /**
     * 
     * @return a Hashmap which contains the query parameters to construct a query clause condition against one type of object
     */
    public String getQueryParamMapObjectName() {
        return queryParamMapObjectName;
    }
    
    public void setQueryParamMapObjectName(String queryParamMapObjectName) {
        this.queryParamMapObjectName = queryParamMapObjectName;
    }
    public String getUpdateValueMapObjectName() {
        return updateValueMapObjectName;
    }
    
    public void setUpdateValueMapObjectName(String updateValueMapObjectName) {
        this.updateValueMapObjectName = updateValueMapObjectName;
    }
    public String getKeysToUpdateInCache() {
        return keysToUpdateInCache;
    }
    
    public void setKeysToUpdateInCache(String keysToUpdateInCache) {
        this.keysToUpdateInCache = keysToUpdateInCache;
    }
    
    @SuppressWarnings("rawtypes")
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        try {
            IntParam id = (IntParam)request.getParam("id");
            if(id==null){
                throw new IllegalArgumentException("param id is missing");
            }
            
            HashMap<String,Integer> queryParam = new HashMap<String, Integer>();
            queryParam.put(id.getName(), id.getData());
            
            HashMap updateValues = (HashMap)request.getTemporaryData(getUpdateValueMapObjectName());
            
            if(updateValues == null){
                updateValues = createUpdateValues(request,dao.getModelClass());
            }
            
            List records = dao.selectListByColumns(queryParam,dao.getModelClass());
            
            if(records.size() == 0){
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"records doesn't exists, nothing to update"); 
            }
            
            int res = dao.updateRecordByColumns(queryParam, updateValues, dao.getModelClass());
            
            if(res==0){
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"no DB records updated, the id you input maybe wrong");
            }
            
            DbRecord record =  (DbRecord)records.get(0);
            record = (DbRecord)dao.selectById(record.getId(), dao.getModelClass());// read updated result
            response.setResult(record);
            int[] ids = new int[1];
            ids[0] = record.getId();
            request.putTemporaryData(keysToUpdateInCache, ids[0]);
            HashMap<Integer, DbRecord> resultContainer = new HashMap<Integer, DbRecord>();
            resultContainer.put(record.getId(), record);
            setHandlerResult(request, resultContainer);
            //request.getTemporaryData().put(getHandlerResultContainerObjectName(), resultContainer);
            
            if(setRecordAsResponse){
                response.setResult(record);
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
    }
    
    @Override
    public void passHandlerResultToCacheHandler(CommandRequest request, Map<String, Object> objects){
        //TODO
    }
}
