package com.rapidbackend.core.process.handler.counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.util.comm.redis.client.RedisClient;

/**
 * 
 * @author chiqiu
 * handler class to decrease an redis counter
 */
public class DecreaseCounterHandler extends CounterHandler{
    Logger logger = LoggerFactory.getLogger(DecreaseCounterHandler.class);
    
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        String key = null;
        try {
            Integer id = getIntParamInRequestSilently(request, getIntKeyParamName());
            if(id!=null&&id>0){
                key = counterConfig.getCounterKeyPrefix()+id;
                RedisClient client = null;
                try {
                    client = getRedisPoolContainer().borrowClient(counterConfig.getRedisCounterTarget());
                    client.getJedis().decr(key.toString());
                } finally{
                    getRedisPoolContainer().returnClient(client, counterConfig.getRedisCounterTarget());
                }
            }else {
                request.addHandleInformation("Can't find valid key,do nothing");
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, "error during decreasing key:"+key, e);
        }
    }
}
