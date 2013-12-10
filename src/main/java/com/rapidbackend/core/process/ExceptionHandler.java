package com.rapidbackend.core.process;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * handles the exceptions
 * @author chiqiu
 *
 */
public class ExceptionHandler extends RequestHandlerBase{
    
    protected BackendRuntimeException exception;
    
    @Override
    public String getHandlerName() {
        return "SocialutilExceptionHandler";
    }
        
    public ExceptionHandler(BackendRuntimeException runtimeException){
        this.exception = runtimeException;
    }
    /**
     * set response status and put the exception into the response.<br/>
     * For now we don't covert exception into json, because embedded socialutil server may not need json values.<br/>
     * Leave all json conversions on the web interface side.
     */
    @Override
    public void handle(CommandRequest request, CommandResponse response) throws BackendRuntimeException{
        response.setException(exception);
        response.setError(true);
    }
}
