package com.rapidbackend.core.process;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * Abstract all process for each service into a pipeline to make the process clearer.<br>
 * Pipelines can be configured in spring's xml, so that we can do automation without changing codes<br>
 * Each pipeline contains a pipeline of stateless request handlers. 
 * @author chiqiu
 *
 */
public interface Pipeline {
	/**
	 * Check if the pipeline name is valid.
	 * Pipeline name shoud be like xxxxPipeline, otherwise, the command->pipeline mapping will fail.
	 * @param name
	 */
    public void nameCheck(String name);
    
    
	public RequestHandler getFirstHandler();
	/**
	 * 
	 * @return name of this pipeline
	 */
	public String getPipelineName();
	
	/**
	 * handle requests
	 * @param request
	 * @param response
	 * @throws BackendRuntimeException
	 */
	public void doHandle(CommandRequest request,CommandResponse response) throws BackendRuntimeException;
	
	/**
	 * get the timeout value for each pipeline, default set to 30 seconds
	 * @return
	 */
	public long getTimeout();
	
	public void handleException(Integer errCode, String message,Exception e) throws BackendRuntimeException;
}
