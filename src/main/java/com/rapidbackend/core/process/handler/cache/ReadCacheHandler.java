package com.rapidbackend.core.process.handler.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.util.general.ConversionUtils;
import com.rapidbackend.util.time.SimpleTimer;
/**
 * For now the design is to store each feed context with one key
 * the context contains replyto, metadata,......
 * @author chiqiu
 *
 */
public class ReadCacheHandler extends ReturnableCacheDataHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    protected static String needToUpdateCacheFlag = "updateCache";
    protected long waitLockTime = 100l;
    protected long maxWaitTime = 1000l;
    
    public static String getNeedToUpdateCacheFlag() {
        return needToUpdateCacheFlag;
    }
    public static void setNeedToUpdateCacheFlag(String needToUpdateCacheFlag) {
        ReadCacheHandler.needToUpdateCacheFlag = needToUpdateCacheFlag;
    }
    public long getMaxWaitTime() {
        return maxWaitTime;
    }
    public void setMaxWaitTime(long maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
    public long getWaitLockTime() {
        return waitLockTime;
    }
    public void setWaitLockTime(long waitLockTime) {
        this.waitLockTime = waitLockTime;
    }
    protected boolean isReadSeedFeedCache(){
        String hadlerName = getHandlerName();
        if(hadlerName.indexOf("SeedFeed")>0){
            return true;
        }else {
            return false;
        }
    }
    
            
    public ReadCacheHandler(){
    }
    /**
     * note: there should be a limit on the pass in feed ids, too many ids will reduce the performance here
     * although the complexity is o(N)
     * TODO cache handlers can be executed in multithreads?
     * 
     */
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try{
          //request.getTemporaryData().put(getNeedToUpdateCacheFlag(), new Boolean(false));
            logger.debug(idListParamName);
            int[] ids = null;
            if(isReadSeedFeedCache()){//only when reading seed feeds , this id list can be empty sometimes
                ids = getIntListInRequestSliently(request, idListParamName);
            } else if (handlerIndex > 1) {
                ids = getIntListInRequestSliently(request, idListParamName);
            }else {
                ids = getIntListInRequest(request, idListParamName);
            }
            
            if(ids !=null && ids.length >0){
                if(redisCache.isCacheReady()){
                    
                    HashMap<String, HashMap<Integer,DbRecord>> idShards= new HashMap<String, HashMap<Integer, DbRecord>>();
                    for(int i : ids){
                        String redisTargetName = getRedisCache().getRedisTargetName(i);
                        HashMap<Integer, DbRecord> recordGroup = idShards.get(redisTargetName);// records may be stored in different caches
                        if(recordGroup==null){
                            recordGroup = new HashMap<Integer, DbRecord>();
                            idShards.put(redisTargetName, recordGroup);
                        }
                        recordGroup.put(i, null);// set the record to null
                    }
                    
                    Set<Entry<String, HashMap<Integer, DbRecord>>> recordGroups = idShards.entrySet();
                    HashMap<String, List<Integer>> missedRecords = new HashMap<String, List<Integer>>();
                    for(Entry<String, HashMap<Integer, DbRecord>> entry:recordGroups){
                        getCachedResults(entry,missedRecords);// get results from cache one redistarget by one
                        //TODO move this step into a barrier or latch?
                    }
                    Set<Integer> missedIds = new HashSet<Integer>();
                    
                    HashMap<Integer, DbRecord> finalResults = mergeFinalResults(idShards,missedIds);
                    
                    yield(request, finalResults);
                    
                    int[] missedIdsInt = ConversionUtils.integerCollectionToIntArray(missedIds);
                    markDBQueryIdList(request, missedIdsInt);
                    
                    request.putTemporaryData(getNeedToUpdateCacheFlag(), new Boolean(true));
                    /*
                    if(missedIds.size()>0){
                        
                    }else {
                        //request.setProcessStatus(ProcessStatus.Finished);                   
                    }*/
                                
                }else {
                    logger.debug(getHandlerName() + " cache not ready, skipping this handler");
                    request.getCurrentHandleInfo().addMessage(getHandlerName() + " cache not ready, skipping this handler");
                    markDBQueryIdList(request, ids);// pass all ids to the next select db data handler
                   
                }
            }else {
                
                logger.debug(getHandlerName() + "id list is empty , move to next handler");
            }
            
        }catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,getHandlerName(), e);
        }
        
    }
    /**
     * 
     * @param idShards  idShards contains all id and records from different redis targets
     * @param missedIds (as a return value)contains missed ids from all shards
     * @return returns an id-record k-v hashmap, this method never returns null
     */
    public HashMap<Integer, DbRecord> mergeFinalResults(HashMap<String, HashMap<Integer,DbRecord>> idShards,Set<Integer> missedIds){
        HashMap<Integer, DbRecord> finalResult = new HashMap<Integer, DbRecord>();
        Collection<HashMap<Integer,DbRecord>> shards = idShards.values();
        for(HashMap<Integer,DbRecord> values:shards){// still O(N) here
            Set<Entry<Integer,DbRecord>> entries= values.entrySet();
            for(Entry<Integer,DbRecord> entry:entries){
                finalResult.put(entry.getKey(), entry.getValue());
                if(entry.getValue()==null){
                    missedIds.add(entry.getKey());
                }
            }
        }
        return finalResult;
    }
    
    /**
     * get results from one single redis target, TODO this need to be done in multi-thread in the future when targets are many
     * @param recordGroup
     * @param missedRecords store this ids of missed records if we can't find them in cache
     * @throws Exception
     * TODO Add followable domain as id prefix?
     */
    public void getCachedResults(Entry<String, HashMap<Integer, DbRecord>> recordGroup,HashMap<String, List<Integer>> missedRecords) throws Exception{
        String targetName = recordGroup.getKey();
        HashMap<Integer, DbRecord> results = recordGroup.getValue();
        getRedisCache().getJsonObjects(results, getModelClass(getModelClassName()), targetName);
        // we only wait the cache lock for one time for each redis shard
        // the cache mutex design is very simple here
        List<Integer> missedIds = getMissedIds(results);
        SimpleTimer timer = new SimpleTimer();
        if (missedIds.size()>0 && hasLockedKeys(missedIds)) {
            while(hasLockedKeys(missedIds)&&timer.getIntervalMili()<getMaxWaitTime()){
                try {
                    Thread.sleep(getWaitLockTime());
                } catch (InterruptedException e) {
                    
                }
            }
            HashMap<Integer, DbRecord> toRetrieve= createEmptyMap(missedIds);
            redisCache.getJsonObjectsWithKeyPrefix(toRetrieve, getModelClass(getModelClassName()), targetName,getModelClassNameLowerCase());
            appendNewResults(toRetrieve,results);
        }
    }
    
    public void appendNewResults(HashMap<Integer, DbRecord> newRetrieved,HashMap<Integer, DbRecord> results){
        Set<Integer> keys = newRetrieved.keySet();
        for(Integer i: keys){
            results.put(i, newRetrieved.get(i));
        }
    }
    
    //public abstract Class<T> getModelClass();
    
    protected static List<Integer> EMPTY = new ArrayList<Integer>(0);
    
    /**
     * 
     * @param results
     * @return
     */
    public static List<Integer> getMissedIds(HashMap<Integer, DbRecord> results){
        List<Integer> res = null;
        Set<Entry<Integer, DbRecord>> entries = results.entrySet();
        for(Entry<Integer, DbRecord> entry: entries){
            if(entry.getValue()==null){
                if(res==null){
                    res = new ArrayList<Integer>();
                }
                res.add(entry.getKey());
            }
        }
        if(res!=null&&res.size()>0){
            return res;
        }else {
            return EMPTY;
        }
    }
    /**
     * return true if one of the ids is marked as locked
     * @param missedIds
     * @return
     */
    public List<Integer> getLockedKeys(List<Integer> missedIds){
        List<Integer> res = null;
        for(Integer id: missedIds){
            if(getRedisCache().isLocked(id)){
                if(res==null){
                    res = new ArrayList<Integer>();
                }
                res.add(id);
            }
        }
        if(res!=null&&res.size()>0){
            return res;
        }else {
            return EMPTY;
        }
    }
    
    public boolean hasLockedKeys(List<Integer> missedIds){
        boolean res = false;
        for(Integer id: missedIds){
            if(getRedisCache().isLocked(id)){
                res = true;
                break;
            }
        }
        return res;
    }
    
    public HashMap<Integer, DbRecord> createEmptyMap(List<Integer> list){
        HashMap<Integer, DbRecord> emptyMap = new HashMap<Integer, DbRecord>();
        for(Integer k:list){
            emptyMap.put(k, null);
        }
        return emptyMap;
    }
    
    public static boolean isNeedToUpdateCache(CommandRequest request){
        boolean res = false;
        Object bool = request.getTemporaryData().get(getNeedToUpdateCacheFlag());
        if(bool != null){
            res = (Boolean)bool;
        }
        return res;
    }
    
    public static boolean isReadCacheFinished(CommandRequest request){
        Object object = request.getTemporaryData().get(getNeedToUpdateCacheFlag());
        return object != null;
    }
}
