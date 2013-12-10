package com.rapidbackend.core.request;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.process.ProcessStatus;
import com.rapidbackend.webserver.netty.request.PostParameters;
import com.rapidbackend.webserver.netty.request.RequestData;

/**
 * Util class for create requests for all services
 * @author chiqiu
 */
public class CommandRequestFactory extends AppContextAware{
    protected static Logger logger = LoggerFactory.getLogger(CommandRequestFactory.class);
    
    /**
     * parse a request body according to the target command type
     * @param requestData
     * @param command
     * @return
     * @throws ParamException if hit errors in parsing or validating the request
     */
	public static CommandRequest createRequest(RequestData requestData,String command) throws ParamException{
	    CommandRequest request = null;
	    if(command == null){
	        logger.error("command is null");
	        throw new ParamException(BackendRuntimeException.BAD_REQUEST, "command is null");
	    }
	    RequestSchema schema = RequestConfig.getRequestSchema(command);
	    if(null == schema){
	        logger.error("schema is null for command "+command);
	        throw new ParamException(BackendRuntimeException.INTERNAL_SERVER_ERROR, "no schema defined for command "+command);
	    }
	    if(requestData.isEmpty()){
	        logger.error("request is empty");
	        throw new ParamException(BackendRuntimeException.BAD_REQUEST,"request cannot be blank");
	    }
	    try {
	        request = new RequestBase();
	        request.setCommand(command);
	        QueryParameters getParameters;
	        String requestBody = requestData.getRequestBody();
	        Set<CommandParam> predefinedParams = schema.getParamNames();
	        if(!StringUtils.isEmpty(requestBody)){
	            getParameters = new QueryParameters(requestBody);
	            
	            for(CommandParam predefined : predefinedParams){
	                String name = predefined.getName();
	                List<String> values = getParameters.all(name);// this method will never return null
	                if(values.size()==0){
	                    continue;
	                }else {
	                    CommandParam param = ParamFactory.createParam(predefined, values);
	                    request.addParam(param);
	                }
	            }
	        }
	        /**
	         * handle postdata
	         * TODO separate post and get data handling according to the httpmethod ?
	         */
	        if(requestData.hasPostData()){
	            PostParameters postParameters = requestData.getPostParameters();
	            for(CommandParam predefined:schema.getParamNames()){
	                String name = predefined.getName();
	                // look in attributes first
	                List<String> values = postParameters.getAttributes().get(name);
	                if(values!=null && values.size()>0){
	                    CommandParam param = ParamFactory.createParam(predefined, values);
                        request.addParam(param);
                        continue;//igonore next part 
	                }
	                
	                FileUpload fileUpload = postParameters.getFileUploads().get(name);
	                if(fileUpload!=null){
	                    FileParam fileParam = ParamFactory.createFileParam(fileUpload);
	                    request.addParam(fileParam);
	                }
	            }
	        }	        
	        
	        if(isShowInfo(request)){// check if we need to show handle info
	            request.setShowHandleInfo(true);
	        }
	        
	        /**
	         * check if the request is good
	         */
	        schema.isValidRequest(request);
	        
	        initProcessStatus(request);
	        
	        
        }catch (Exception e) {
            logger.error("error in parsing command "+command+", request body " + requestData.getRequestBody(),e);
            if(e instanceof ParamException){
                throw (ParamException)e;
            }else{
                throw new ParamException(BackendRuntimeException.BAD_REQUEST, "error in parsing request body ",e);
            }
        }
	    return request;
	}
		
	public static boolean isShowInfo(CommandRequest request){
	    boolean result = false;
	    CommandParam showInfo = request.getParam(RequestSchema.ShowInfo.getName());
	    
	    if(showInfo !=null && showInfo instanceof StringParam){
	        String val = ((StringParam)showInfo).getData();
	        if(val.equalsIgnoreCase("true")||
                val.equalsIgnoreCase("1") ||
                val.equalsIgnoreCase("yes")){
	            result = true;
	        }
	    }
	    
	    return result;
	}
	
	public static void initProcessStatus(CommandRequest request){
	    request.setProcessStatus(ProcessStatus.Processing);
	}
	
	
}
