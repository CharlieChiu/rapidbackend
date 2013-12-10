package com.rapidbackend.core.process;

import com.rapidbackend.core.BackendRuntimeException;

public class ProcessException extends BackendRuntimeException{
    
    /**
     * 
     */
    private static final long serialVersionUID = 3593595757987218302L;

    public ProcessException(Integer errCode,String err,Throwable throwable){
        super(errCode,err,throwable);
    }
    
    public ProcessException(Integer errCode,String err){
        super(errCode,err);
    }
    
    public ProcessException(Integer errCode){
        super(errCode);
    }
}
