package com.rapidbackend.socialutil.process.handler.merge;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.process.handler.IntermediateDatahandler;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.util.general.ConversionUtils;

public class GetAllFeedUserIdHandler extends IntermediateDatahandler{
    /**
     * merge results for feed handlers
     */
    @SuppressWarnings("unchecked")
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            HashMap<Integer, FeedContentBase> allFeeds = (HashMap<Integer, FeedContentBase>)grab(request);
            Collection<FeedContentBase> values = allFeeds.values();
            Set<Integer> userIds = new HashSet<Integer>();
            for(FeedContentBase feed : values){
                if(!DbRecord.isEmptyRecord(feed)){
                    userIds.add(feed.getUserId());
                }
            }
            int[] ids = ConversionUtils.integerCollectionToIntArray(userIds);
            yield(request, ids);
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
    }
}
