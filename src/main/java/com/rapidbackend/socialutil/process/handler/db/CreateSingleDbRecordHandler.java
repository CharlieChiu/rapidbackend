package com.rapidbackend.socialutil.process.handler.db;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

public class CreateSingleDbRecordHandler extends DbDataHandler{
        
    @Override
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        try {
            Object model = createModelObjectFromRequest(request, dao.getModelClass());
            int id = dao.storeNewModelBean(model);
            if(model instanceof DbRecord){
                ((DbRecord) model).setId(id);
            }
            if (setRecordAsResponse) {
                response.setResult(model);
            }
        } catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName(),e);
        }
    }
    
}
