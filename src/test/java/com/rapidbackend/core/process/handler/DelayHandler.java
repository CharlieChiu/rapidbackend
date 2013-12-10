package com.rapidbackend.core.process.handler;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.RequestHandlerBase;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * this handler delays for a long time. It is used to test handle request timeout 
 * @author chiqiu
 *
 */
public class DelayHandler extends RequestHandlerBase{
    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            Thread.sleep(1000l*1024);
        } catch (Exception e) {
        }
    }
}
