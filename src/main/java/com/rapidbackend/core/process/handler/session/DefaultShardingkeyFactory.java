package com.rapidbackend.core.process.handler.session;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.security.session.SessionBase;

/**
 * Default session sharding key factory, create sharding key by using a incomming
 * CommandParam param value.<br/> 
 * TODO implement a round robin key factory
 * @author chiqiu
 *
 */
public class DefaultShardingkeyFactory implements SessionShardingkeyFactory{
    protected CommandParam keyParam;
    
    public CommandParam getKeyParam() {
        return keyParam;
    }
    @Required
    public void setKeyParam(CommandParam keyParam) {
        this.keyParam = keyParam;
    }
    @Override
    public boolean setShardingkey(CommandRequest request,SessionBase session){
        boolean result = false;
        CommandParam param = request.getParam(keyParam.getName());
        if (param !=null) {
            Object key = param.getData();
            if(key!=null){
                session.setShardingKey(key);
                result = true;
            }
        }else {
			throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST, "no param "+keyParam.getName()+" found, please check your request");
		}
        return result;
    }
}
