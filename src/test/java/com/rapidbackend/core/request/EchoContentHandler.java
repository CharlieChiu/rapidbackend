package com.rapidbackend.core.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.RequestHandlerBase;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class EchoContentHandler extends RequestHandlerBase{
    Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public  void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        StringParam content = (StringParam)request.getParam(ParamNameUtil.CONTENT);
        logger.debug("content receieved "+content);
        response.setResult(content.getData());
    }
}
