package com.rapidbackend.core.command;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.core.response.ResponseBase;

public class FailedCommand extends DefaultCommand{
    public FailedCommand(String command,BackendRuntimeException e){
        super(0, null, null);
        CommandResponse response = new ResponseBase();
        response.setError(true);
        response.setException(e);
        this.setResponse(response);
    }
    @Override
    public String toString(){
        return "Unsupported";
    }
}
