package com.rapidbackend.core.process;


import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.interceptor.CommandInterceptor;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * each handler is a 'step' of handling a income request.
 * The handles in rapidbackend are stateless handlers. Creating many handler instance from spring at runtime may causing performance problem.
 * So we generate those handlers as singletons.<br/>
 * TODO add getPreviousHandler() function and make the pipeline a doubly linked list.
 * @author chiqiu
 */
public interface RequestHandler {
    
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException;
    
    public RequestHandler getNextHandler();
    
    public void setNextHandler(RequestHandler handler);
    
    public String getHandlerName();
    
    public void modifyHandlerName(String name);
    /**
     * prepare some behaviors before handling the request
     * @param request
     * @param response
     */
    public void prepareHandling(CommandRequest request,CommandResponse response);
    
    /**
     * do some behaviors after handling the request
     * @param request
     * @param response
     */
    public void endHandling(CommandRequest request,CommandResponse response);
    
    public void handleException(Integer errCode, String message,Exception e) throws BackendRuntimeException;
    
    public void cancelCurrentCommand(CommandRequest request,CommandResponse response);
    
    /**
     * return true if the request is accepted by this handler
     * @param request
     * @param response
     */
    public boolean interceptRequest(CommandRequest request,CommandResponse response);
    
    /**
     * check if this request is ready to be handled by the handler
     * @param request
     * @return
     */
    public boolean isRequestReadyForHandling(CommandRequest request);
    
    public CommandInterceptor[] getInterceptors();
    
    public boolean isAbortCurrentCommand(CommandRequest request, CommandResponse response);
    
    public void handleAborting(CommandRequest request,CommandResponse response);
    /**
     * sets the index of the handler indicates which pipeline it is in
     */
    public void setIndex(int idx);
}
