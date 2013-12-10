package com.rapidbackend.socialutil.process.handler.merge;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.ProcessStatus;
import com.rapidbackend.socialutil.process.handler.IntermediateDatahandler;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.model.extension.*;
import com.rapidbackend.socialutil.model.metadata.FeedMetaData;
import com.rapidbackend.socialutil.model.metadata.ModelMetadata;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;

/**
 * work flow:
 * get idlistparam(from request)
 * get feeds from handler result hashmap
 * put all feeds into the handlerResult 
 * put all users into feedContexts
 * TODO here is a risk, do we need to separate no-exist feeds to another feed-cache ?because we need to cache non exists feeds
 * 
 * @author chiqiu
 *
 */
public class MergeFeedResultHandler extends IntermediateDatahandler{
    Logger logger = LoggerFactory.getLogger(MergeFeedResultHandler.class);
    protected String feedUserResultName = "users";
    protected String feedResultName = "feeds";
    protected String feedMetadataName = "feedMetadata";
    
    public String getFeedUserResultName() {
        return feedUserResultName;
    }
    public void setFeedUserResultName(String feedUserResultName) {
        this.feedUserResultName = feedUserResultName;
    }

    public String getFeedResultName() {
        return feedResultName;
    }
    public void setFeedResultName(String feedResultName) {
        this.feedResultName = feedResultName;
    }
    
    public String getFeedMetadataName() {
        return feedMetadataName;
    }
    public void setFeedMetadataName(String feedMetadataName) {
        this.feedMetadataName = feedMetadataName;
    }
    @Required
    public void setIdListParamName(String idListParamName) {
        this.idListParamName = idListParamName;
    }

    /**
     * merge results for feed handlers
     */
    @SuppressWarnings("unchecked")
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            int[] ids = null;
            ids = getIntListInRequest(request, idListParamName);
            if(ids!=null&& ids.length>0){
                HashMap<Integer, FeedContentBase> feedResults = (HashMap<Integer, FeedContentBase>)request.getTemporaryData(getFeedResultName());
                HashMap<Integer, UserBase> feedUsers = (HashMap<Integer, UserBase>)request.getTemporaryData(getFeedUserResultName());
                //HashMap<Integer, ModelMetadata> feedMetadatas = (HashMap<Integer, ModelMetadata>)request.getTemporaryData(getFeedMetadataName());
                if(feedResults!=null){
                    List<FeedContext> feeds =  mergeFeeds(ids, feedResults,true);
                    mergeUsers(feeds, feedUsers);
                    //mergeMetadatas(feeds, feedMetadatas);
                    logger.debug("merge feed results: "+ feeds.size());
                    response.setResult(feeds);
                    request.setProcessStatus(ProcessStatus.Finished);
                }else {// this should not happen
                    throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName()+" handle result is empty");
                }
            }else {
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName()+" id list is empty");
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
    }
    /**
     * merge feeds and seed feeds
     * @param ids
     * @param results
     * @param addSeedFeed
     * @return
     */
    public List<FeedContext> mergeFeeds(int[] ids,HashMap<Integer, FeedContentBase> results,boolean addSeedFeed){
        List<FeedContext> contexts = new ArrayList<FeedContext>();
        for(int id:ids){
            FeedContentBase feed = results.get(id);
            FeedContext context = null;
            //if(feed == null||feed.isNotFound()==true){
            if(feed == null){
                context = createEmptyFeedContext(id);
            }else {
                context = new FeedContext(id);
                context.setFeed(feed);
                FeedContentBase seedFeed = null;
                if(addSeedFeed){
                    if(feed.getSeedFeedId()!=null&&feed.getSeedFeedId()>0){
                        seedFeed = results.get(feed.getSeedFeedId());
                    }
                    if(seedFeed !=null){
                        addSeedFeed(context,seedFeed);
                    }
                }
                
            }
            contexts.add(context);
        }
        return contexts;
    }
    
    public void mergeUsers(List<FeedContext> feedContexts,HashMap<Integer, UserBase> users){
        for(FeedContext feedContext : feedContexts){
            FeedContentBase feed = (FeedContentBase)feedContext.getFeed();
            if(feed!=null){
                Integer userId = feed.getUserId();
                UserBase user = users.get(userId);
                addUser(feedContext,user);
                if(feedContext.getSeedFeed()!=null){
                    Integer seedFeedUserId = ((FeedContentBase)feedContext.getSeedFeed()).getUserId();
                    UserBase seedFeedUser = users.get(seedFeedUserId);
                    addSeedFeedUser(feedContext, seedFeedUser);
                }
            }
        }
    }
    /**
     * metadata will not be fetched through GetGeefsPipeline.
     * Metadata should be fetched in an independent GetMetadataPipeline.
     * @param feedContexts
     * @param feedMetadatas
     */
    @Deprecated
    public void mergeMetadatas(List<FeedContext> feedContexts,HashMap<Integer, ModelMetadata> feedMetadatas){
        for(FeedContext feedContext : feedContexts){
            FeedContentBase feed = (FeedContentBase)feedContext.getFeed();
            if(feed!=null){
                Integer feedId = feed.getId();
                ModelMetadata metadata = feedMetadatas.get(feedId);
                addMetadata(feedContext,(FeedMetaData)metadata);
                if(feedContext.getSeedFeed()!=null){
                    Integer seedfeedId = ((FeedContentBase)feedContext.getSeedFeed()).getId();
                    ModelMetadata sMetadata = feedMetadatas.get(seedfeedId);
                    addSeedMetadata(feedContext, (FeedMetaData)sMetadata);
                }
            }
        }
    }
    
    public void addMetadata(FeedContext feedContext, FeedMetaData metaData){
        if(metaData!=null){
            feedContext.setFeedMetaData(metaData);
        }
    }
    
    public void addSeedMetadata(FeedContext feedContext, FeedMetaData metaData){
        if(metaData!=null){
            feedContext.setSeedFeedMetaData(metaData);
        }
    }
    
    public FeedContext createEmptyFeedContext(int id){
        FeedContext context = new FeedContext(id);
        context.setFeedNotFound(true);
        return context;
    }
    
    public void addUser(FeedContext feedContext,UserBase user){
        //if(user!=null&&user.isNotFound()!=true){
        if(user!=null){
            feedContext.setUser(user);
        }else {
            feedContext.setUserNotFound(true);
        }
    }
    
    public void addSeedFeedUser(FeedContext feedContext,UserBase user){
        //if(user!=null&&user.isNotFound()!=true){
        if(user!=null){
            feedContext.setSeedFeedUser(user);
        }else {
            feedContext.setSeedFeedUserNotFound(true);
        }
    }
    
    public void addSeedFeed(FeedContext feedContext,FeedContentBase seedFeed){
        //if(seedFeed!=null&&seedFeed.isNotFound()!=true){
        if(seedFeed!=null){
            feedContext.setSeedFeed(seedFeed);
        }else{
            feedContext.setSeedFeedNotFound(true);
        }
    }
}
