package com.rapidbackend.socialutil.process.handler.feeds;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.process.handler.IntermediateDatahandler;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class GetFeedIdHandler extends IntermediateDatahandler{
    
    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        int[] ids = getIntListInRequest(request,ParamNameUtil.FEED_IDS);
        yield(request, ids);
    }
}
