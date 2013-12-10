package com.rapidbackend.socialutil.process.handler;

import com.rapidbackend.core.request.CommandRequest;

/**
 * This handler handles the request and produces its own result for future handling
 * 
 * 
 * TODO change the param names here: 
 * handlerResultContainerObjectName => yield
 * previousHandlerResultContainerObjectName => grab
 * 
 * @author chiqiu
 *
 */
public abstract class IntermediateDatahandler extends DataHandler{

    protected String handlerResultContainerObjectName;
    protected String dBQueryIdListName;
    protected String previousHandlerResultContainerObjectName;
    
    protected String idListParamName;
    
    
    public void setIdListParamName(String idListParamName) {
        this.idListParamName = idListParamName;
    }
    
    public void setIdListParam(CommandRequest request,int[] idList){
        request.putTemporaryData(idListParamName, idList);
    }
    
    public int[] getIdListParam(CommandRequest request){
        return getIntListInRequest(request, idListParamName);
    }
    public String getPreviousHandlerResultContainerObjectName() {
        return previousHandlerResultContainerObjectName;
    }
    public void setPreviousHandlerResultContainerObjectName(
            String previousHandlerResultContainerObjectName) {
        this.previousHandlerResultContainerObjectName = previousHandlerResultContainerObjectName;
    }
    public void markDBQueryIdList(CommandRequest request,int[] idList){
        request.putTemporaryData(dBQueryIdListName, idList);
    }
    /**
     * use {@link #markDBQueryIdList(CommandRequest, Object)} instead
     * @return
     */
    @Deprecated
    public String getdBQueryIdListName() {
        return dBQueryIdListName;
    }
    public void setdBQueryIdListName(String dBQueryIdListName) {
        this.dBQueryIdListName = dBQueryIdListName;
    }
    
    
    /**
     * use {@link #setHandlerResult(CommandRequest, Object)} instead 
     * @return
     */
    @Deprecated
    public String getHandlerResultContainerObjectName() {
        return handlerResultContainerObjectName;
    }
    //@Required
    public void setHandlerResultContainerObjectName(
            String handlerResultContainerObjectName) {
        this.handlerResultContainerObjectName = handlerResultContainerObjectName;
    }
    public void setHandlerResult(CommandRequest request,Object result){
        request.putTemporaryData(handlerResultContainerObjectName, result);
    }
    /**
     * set the ids of object need to be updated in cache.
     * if there is a updateCacheHandler follows this hanlder, the id list specified here will be used by the next handler
     * @param ids
     */
    
    public void setUpdateIdListForCache(CommandRequest requests,int[] ids,String idListParamName){
        requests.putTemporaryData(idListParamName, ids);
    }
    
    protected String yield;
    protected String grab;
    
    public String getYield() {
        return yield;
    }
    public void setYield(String yield) {
        this.yield = yield;
    }
    
    public void yield(CommandRequest request,Object object){
        request.putTemporaryData(yield,object);
    }
    
    public String getGrab() {
        return grab;
    }
    public void setGrab(String grab) {
        this.grab = grab;
    }
    
    public Object grab(CommandRequest request){
        return request.getTemporaryData(grab);
    }
    
}
