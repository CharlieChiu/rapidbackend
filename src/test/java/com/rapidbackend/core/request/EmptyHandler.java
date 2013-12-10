package com.rapidbackend.core.request;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.RequestHandlerBase;
import com.rapidbackend.core.response.CommandResponse;

public class EmptyHandler extends RequestHandlerBase{
    @Override
    public  void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        
    }
}
