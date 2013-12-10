package com.rapidbackend.socialutil.process.handler.search;

import org.apache.commons.lang.StringUtils;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.socialutil.process.handler.DataHandler;
import com.rapidbackend.core.model.util.ModelList;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.core.SocialUtility;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.search.SearchService;

public class SearchUserHandler extends DataHandler{
    
    protected SearchService searchService = null;
    
    public SearchService getSearchService() {
        if(searchService==null){
            SocialUtility socialUtility = (SocialUtility)Rapidbackend.getCore().getExtension("socialutility");
            searchService = socialUtility.getSearchService();
        }
        return searchService;
    }

    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            Integer start = getIntParamInRequest(request, "start");
            Integer limit = getIntParamInRequest(request, "limit");
            String content = getStringParamInRequest(request, "userName");
            if(StringUtils.isEmpty(content)){
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName() + ": content keywords is null");
            }else {
                String startString = start==null?null:start.toString();
                String limitString = limit==null?null:limit.toString();
                ModelList<FeedContentBase> result = getSearchService().searchFeed(content, startString, limitString);
                response.setResult(result);
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, getHandlerName(), e);
        }
    }
}
