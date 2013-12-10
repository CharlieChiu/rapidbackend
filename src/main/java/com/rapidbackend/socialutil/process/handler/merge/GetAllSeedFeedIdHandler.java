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

/**
 * 
 * @author chiqiu
 *
 */
public class GetAllSeedFeedIdHandler extends IntermediateDatahandler{
    
    @SuppressWarnings("unchecked")
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            HashMap<Integer, FeedContentBase> allFeeds = (HashMap<Integer, FeedContentBase>)grab(request);
            Collection<FeedContentBase> values = allFeeds.values();
            Set<Integer> seedFeedIds = new HashSet<Integer>();
            for(FeedContentBase feed : values){
                if(!DbRecord.isEmptyRecord(feed)){
                    if(feed.getSeedFeedId()!=null&&feed.getSeedFeedId()>0){
                        if(allFeeds.get(feed.getSeedFeedId())==null){// if the seed feed has already been contained in all feeds, no need to get it again!
                            seedFeedIds.add(feed.getSeedFeedId());
                        }
                    }
                }
            }
            int[] ids = ConversionUtils.integerCollectionToIntArray(seedFeedIds);
            yield(request, ids);
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
    }
    
}
