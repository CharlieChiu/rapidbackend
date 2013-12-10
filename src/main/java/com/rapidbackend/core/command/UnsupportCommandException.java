package com.rapidbackend.core.command;

import com.rapidbackend.core.BackendRuntimeException;

public class UnsupportCommandException extends BackendRuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = -5341353302532628660L;

    public UnsupportCommandException(String command){
        super(BackendRuntimeException.BAD_REQUEST,"unsupported command:" +command);
    }
        
}
