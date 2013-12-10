package com.rapidbackend.core.process.interceptor;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
/**
 * User can implement this interceptor interface and configure them
 * @author chiqiu
 *
 */
public interface CommandInterceptor {
    /**
     * Return true to let the request pass to handlers after this interceptor.
     * @param request
     * @param response
     * @return
     * @throws BackendRuntimeException
     */
    public boolean agree(CommandRequest request,CommandResponse response) throws BackendRuntimeException;
}
