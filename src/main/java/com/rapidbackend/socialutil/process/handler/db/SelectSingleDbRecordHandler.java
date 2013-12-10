package com.rapidbackend.socialutil.process.handler.db;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.ProcessException;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * this handler selects one db record by the unique column 'ID'
 * @author chiqiu
 *
 */
public class SelectSingleDbRecordHandler extends DbDataHandler{
    
    Logger logger = LoggerFactory.getLogger(SelectSingleDbRecordHandler.class);
    
    @Override
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        try{
            IntParam id = (IntParam)request.getParam("id");
            if(id==null){
                throw new IllegalArgumentException("param id is missing");
            }
            
            HashMap<Object, Object> queryParam = new HashMap<Object, Object>();
            queryParam.put("id", id.getData());
            
            Object record = dao.selectSingleRecordByParams(queryParam, dao.getModelClass());
            
            if (setRecordAsResponse) {
                response.setResult(record);
            }
            /*
            String idparamName = getIdParamName(request);
            Class clazz = getModelClass(request);
            IntParam param = (IntParam)request.getParam(idparamName);
            Integer id = param.getData();
            Object item = dao.loadById(id, clazz);
            List<Object> items = new ArrayList<Object>();
            items.add(item);
            response.setResultItems(items);*/
        }catch (Exception e) {
            String err = "error during SelectSingleDbRecordHandler";
            logger.error(err,e);
            throw new ProcessException(BackendRuntimeException.InternalServerError,err,e);
        }
    }
    
}
