package com.rapidbackend.core.process.interceptor;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
/**
 * This interceptor will agree all commands.
 * @author chiqiu
 *
 */
public class NiceGuyInterceptor implements CommandInterceptor{
    @Override
    public boolean agree(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        return true;
    }
}
