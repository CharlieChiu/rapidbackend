package com.rapidbackend.socialutil.process.handler.db;

import java.util.HashMap;
import java.util.List;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.socialutil.util.ParamNameUtil;

/**
 * Deletes single db record, put deleted record in return results. might need to update cache.
 * @author chiqiu
 *
 */
public class DeleteSingleDbRecordHandler extends DbDataHandler{
    protected String queryParamMapObjectName;
    protected String updateValueMapObjectName;
    protected String keysToUpdateInCache;
    
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
            HashMap queryParam = (HashMap)request.getTemporaryData().get(getQueryParamMapObjectName());
            if(null == queryParam){
                queryParam = new HashMap();
                IntParam id = (IntParam)request.getParam(ParamNameUtil.ID);
                
                if(id==null){
                    throw new IllegalArgumentException("no valid query params for operation delete in "+getHandlerName());
                }
                queryParam.put(id.getName(), id.getData());
            }
            
            List records = dao.selectListByColumns(queryParam, dao.getModelClass());
            if(records.size()==1){
                
                DbRecord record =  (DbRecord)records.get(0);
                //record = dao.loadById(record.getId(), getModelClass(modelClassName));
                
                response.setResult(record);
                int[] ids = new int[1];
                ids[0] = record.getId();
                int res = dao.deleteModelById(ids[0]);
                if(res!=1){
                    throw new RuntimeException(res+"records deleted by "+getHandlerName());
                }
                request.getTemporaryData().put(keysToUpdateInCache, ids[0]);
                HashMap<Integer, DbRecord> resultContainer = new HashMap<Integer, DbRecord>();
                resultContainer.put(record.getId(), null);// cache updater will update the null value automatically
                request.getTemporaryData().put(getHandlerResultContainerObjectName(), resultContainer);
                
                if (setRecordAsResponse) {
                    response.setResult(record);
                }
                
            }else if (records.size()==0) {
                throw new IllegalArgumentException("bad reques param, no record found according to input params, nothing to delete");
            }else {
                throw new IllegalArgumentException("bad reques param, more than 1 record found according to input params, deleting rejected");
            }
        } catch (IllegalArgumentException e) {
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,getHandlerName(),e);
        } catch (Exception e){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"Error in "+getHandlerName(),e);
        }
    }
    
}
