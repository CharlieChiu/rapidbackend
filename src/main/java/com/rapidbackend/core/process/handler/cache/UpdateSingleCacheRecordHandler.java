package com.rapidbackend.core.process.handler.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

public class UpdateSingleCacheRecordHandler extends ReturnableCacheDataHandler{
    protected Logger logger = LoggerFactory.getLogger(UpdateSingleCacheRecordHandler.class);
    
    @SuppressWarnings("unchecked")
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        
    }
}
