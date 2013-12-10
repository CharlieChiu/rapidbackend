package com.rapidbackend.socialutil.dao;

import com.rapidbackend.core.BackendRuntimeException;

public class DataAccessException extends BackendRuntimeException{
    
    /**
     * 
     */
    private static final long serialVersionUID = -8528469776339720356L;
    public static Integer errorCode = BackendRuntimeException.InternalServerError;
    
    public DataAccessException(String msg){
        super(errorCode, msg);
    }
    
    public DataAccessException(String msg, Throwable e){
        super(errorCode, msg, e);
    }
}
