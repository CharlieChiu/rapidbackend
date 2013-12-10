package com.rapidbackend.core.process.interceptor;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * This interceptor will reject all command and make all handlers after it egnore the current command.
 * @author chiqiu
 *
 */
public class BadGuyInterceptor implements CommandInterceptor{
    @Override
    public boolean agree(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        return false;
    }
}
