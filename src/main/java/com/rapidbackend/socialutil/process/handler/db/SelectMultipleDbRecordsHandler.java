package com.rapidbackend.socialutil.process.handler.db;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.ProcessException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.core.model.DbRecord;

/**
 * select data records by ids
 * @author chiqiu
 *
 */
public class SelectMultipleDbRecordsHandler extends DbDataHandler{
    Logger logger = LoggerFactory.getLogger(SelectMultipleDbRecordsHandler.class);
    /**
     * use dao's select by id method
     */
    
    @Override
    @Required
    public void setdBQueryIdListName(String dBQueryIdListName) {
        this.dBQueryIdListName = dBQueryIdListName;
    }
    
    protected boolean isReadSeedFeeds(){//
        String hadlerName = getHandlerName();
        if(hadlerName.indexOf("SeedFeed")>0){
            return true;
        }else {
            return false;
        }
    }
    @SuppressWarnings("unchecked")    
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        try{/*
            if(request.getProcessStatus()==ProcessStatus.Processing){// check if we need to stop
                
            }*/
            int[] ids;
            logger.debug("read ids from "+dBQueryIdListName);
            
            ids = getIntListInRequestSliently(request, dBQueryIdListName);
            
            if(ids!=null&&ids.length>0){
                List<?> resultList = dao.selectByIds(ids, dao.getModelClass());
                logger.debug("get records from db ,size :"+resultList.size());
                HashMap<Integer, DbRecord> handlerResult = (HashMap<Integer, DbRecord>)grab(request);
                if(handlerResult == null){
                    handlerResult = new HashMap<Integer, DbRecord>();
                    yield(request,handlerResult);
                }
                for(Object o: resultList){
                    DbRecord record = (DbRecord) o;
                    handlerResult.put(record.getId(), record);
                }
            }else {
                request.addHandleInformation(dBQueryIdListName+"id list is empty, do nothing");
                logger.debug(dBQueryIdListName+" is empty, do nothing");
            }
            
        }catch (Exception e) {
            logger.error(getHandlerName(),e);
            throw new ProcessException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName(),e);
        }
    }
    
    
}
