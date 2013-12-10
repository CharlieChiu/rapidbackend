package com.rapidbackend.core.process;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.util.general.MsgBuilder;

/**
 * 
 * @author chiqiu
 *
 */
public class StandardPipeline implements Pipeline,BeanNameAware{
    public static long default_time_out = 60 * 1000l;
    Logger logger = LoggerFactory.getLogger(getClass());
    protected String pipelineName;
    protected String beanName;
    protected long timeout = default_time_out;
    @Override
    public void nameCheck(String name){
        if(name!=null &&name.endsWith("Pipeline")){
            // ends with Pipeline
        }else {
            MsgBuilder msgBuilder = new MsgBuilder();
            msgBuilder.$("Wrong pipeline name:").$(name).$(",pipeline name should end with 'Pipeline'");
            logger.error(msgBuilder.toString());
            throw new RuntimeException(msgBuilder.toString());
        }
    }
    @Override
    public String getPipelineName() {
        String result=null;
        if(StringUtils.isEmpty(pipelineName)){
            result =  beanName;
        }else {
            result = pipelineName;
        }
        
        return result;
    }
    
    public void setPipelineName(String pipelineName) {
        nameCheck(pipelineName);
        this.pipelineName = pipelineName;
    }
    public String getBeanName() {
        return beanName;
    }
        
    @Override
    public void setBeanName(String beanName) {
        nameCheck(beanName);
        this.beanName = beanName;
    }
    @Override
    public RequestHandler getFirstHandler() {
        return handlers[0];
    }
    
    protected RequestHandler[] handlers;
    protected StandardPipeline appendedPipeline = null;
    protected StandardPipeline insertedPipeline = null;
    
    protected boolean inited = false;
    
    public boolean isInited() {
        return inited;
    }
    
    private void  init(){
        int idx = 1;
        for(RequestHandler handler : handlers){
            handler.setIndex(idx++);
            String handlerName = handler.getHandlerName();
            String newHandlerName = getPipelineName()+"."+handlerName;
            handler.modifyHandlerName(newHandlerName);
        }
        inited = true;
    }
    
    public StandardPipeline getInsertedPipeline() {
        return insertedPipeline;
    }
    public void setInsertedPipeline(StandardPipeline insertedPipeline) {
        this.insertedPipeline = insertedPipeline;
    }
    public StandardPipeline getAppendedPipeline() {
        return appendedPipeline;
    }
    public void setAppendedPipeline(StandardPipeline appendedPipeline) {
        this.appendedPipeline = appendedPipeline;
    }
    public RequestHandler[] getHandlers() {
        return handlers;
    }
    public void setHandlers(RequestHandler[] handlers) {
        this.handlers = handlers;
    }
    /**
     * 
     * @param request
     * @param response
     * @throws BackendRuntimeException
     */
    public void doHandle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try {
            if(!isInited()){
                init();
            }
            if(insertedPipeline !=null){
                insertedPipeline.doHandle(request, response);
            }
            
            for(RequestHandler handler : handlers){
                if(handler.isAbortCurrentCommand(request, response)){
                    handler.handleAborting(request, response);
                    break;
                }
                handler.prepareHandling(request, response);
                handler.handle(request, response);
                handler.endHandling(request, response);
            }
            if(appendedPipeline !=null){
                appendedPipeline.doHandle(request, response);
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
    }
    
    @Override
    public void handleException(Integer errCode, String message,Exception e) throws BackendRuntimeException{
        if(e instanceof BackendRuntimeException){
            throw (BackendRuntimeException)e;
        }else {
            if(StringUtils.isEmpty(message)){
                throw new BackendRuntimeException(errCode, getPipelineName(), e);
            }else {
                throw new BackendRuntimeException(errCode, message, e);
            }
        }
    }
    public long getTimeout() {
        return timeout;
    }
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
