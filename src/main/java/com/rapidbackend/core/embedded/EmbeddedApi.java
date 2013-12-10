package com.rapidbackend.core.embedded;

import com.rapidbackend.core.command.DefaultCommand;
import com.rapidbackend.core.command.CommandFactory;
import com.rapidbackend.core.command.FailedCommand;
import com.rapidbackend.core.command.UnsupportCommandException;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.CommandRequestFactory;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.request.ParamException;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.core.response.ResponseFactory;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.webserver.netty.request.RequestData;
/**
 * An API class extends this class is an 'embedded' api which runs in jvm without the need to parse http results
 * @author chiqiu
 *
 */
public abstract class EmbeddedApi {
    
    @SuppressWarnings("unchecked")
    public static <T>void handleResult(CommandResult<T> result, DefaultCommand command){
        if(command.getRequest()!=null&&command.getRequest().isShowHandleInfo()){
            result.setHandleInfo(command.toString());
        }
        result.setHandleInfo(command.toString());//TODO remove me
        
        if(command.getResponse()!=null&&command.getResponse().isError()){
            handleException(result,command.getResponse().getException());
        }else {
            result.setResult((T)command.getResponse().getResult());
            result.setSessionId(command.getResponse().getSessionId());
            
            PagingInfo pagingInfo = command.getResponse().getPagingInfo();
            if(pagingInfo !=null){
                result.setPageSize(pagingInfo.getPageSize());
                result.setStart(pagingInfo.getStart());
                result.setNextStart(pagingInfo.getNextStart());
            }
            /*
            Object nextStart = command.getResponse().getAddtionalInfo(ParamNameUtil.NEXT_START);
            if (nextStart !=null) {
                result.setNextStart((Integer)nextStart);
            }else {
                result.setNextStart(null);
            }*/
            
        }
    }
    
    @Deprecated
    public static <T> void setPagingInfo(CommandResult<T> result, DefaultCommand command) {
        CommandRequest request = command.getRequest();
        
        CommandParam start = request.getParam(ParamNameUtil.START);
        if(start != null && start instanceof IntParam){
            Integer startNum = ((IntParam)start).getData();
            result.setStart(startNum);
        }
        CommandResponse response = command.getResponse();
        Object nextStart = response.getAddtionalInfo(ParamNameUtil.NEXT_START);
        if(nextStart !=null && nextStart instanceof Integer){
            Integer nextStartNum = (Integer)nextStart;
            result.setNextStart(nextStartNum);
        }
        
    }
    
    
    public static <T>void handleException(CommandResult<T> result, Exception e){
        result.setException(e);
        result.setError(true);    
    }
    
    public static DefaultCommand handleCommand(RawCommand rawCommand, RequestData requestData){
        DefaultCommand defaultCommand = null;
        if(!rawCommand.isSupported()){
            defaultCommand = new FailedCommand(rawCommand.getCommandPath(),new UnsupportCommandException(rawCommand.getCommandPath()));
        }else {
            try {
                CommandRequest request = CommandRequestFactory.createRequest(requestData, rawCommand.getCommand());
                CommandResponse response = ResponseFactory.createResponse();
                defaultCommand = CommandFactory.createCommand(request, response);
                defaultCommand.execute();
            } catch (ParamException e) {
                defaultCommand = new FailedCommand(rawCommand.getCommandPath(),e);
            }
            
        }
        return defaultCommand;
    }
    
    public static class RawCommand{
        String command;
        String commandPath;
        boolean supported;
        
        public RawCommand(String command,String commandPath,boolean supported){
            this.command = command;
            this.commandPath = commandPath;
            this.supported = supported;
        }
        public String getCommand() {
            return command;
        }
        public void setCommand(String command) {
            this.command = command;
        }
        public boolean isSupported() {
            return supported;
        }
        public void setSupported(boolean supported) {
            this.supported = supported;
        }
        public String getCommandPath() {
            return commandPath;
        }
        public void setCommandPath(String commandPath) {
            this.commandPath = commandPath;
        }
        
    }
}
