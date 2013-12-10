package com.rapidbackend.core.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.process.Pipeline;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

/**
 * @author chiqiu
 */
public class CommandFactory extends AppContextAware{
    
    protected static Logger logger = LoggerFactory.getLogger(CommandFactory.class);
    /**
     * 
     * @param request
     * @param response
     * @return
     * @throws BackendRuntimeException
     */
    public static DefaultCommand createCommand(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        try{
            String command = request.getCommand();
            if(null ==command){
                logger.error("command in request is null");
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"command in request is null");
            }else {
                String pipelineName = getPipelineBeanName(command);
                Pipeline pipeline = (Pipeline)getApplicationContext().getBean(pipelineName);
                DefaultCommand defaultCommand = new DefaultCommand(pipeline.getTimeout(),request,response);
                defaultCommand.setPipeline(pipeline);
                return defaultCommand;
            }
        }catch (Exception e) {
            // TODO: handle exception
            if(e instanceof BackendRuntimeException){
                throw (BackendRuntimeException)e;
            }else{
                throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"error in creating command",e);
            }
        }
    }
    /**
     * 
     * @param command
     * @return
     */
    public static String getPipelineBeanName(String command){
        return command +"Pipeline";
    }
}
