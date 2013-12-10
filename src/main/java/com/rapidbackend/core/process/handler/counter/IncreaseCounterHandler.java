package com.rapidbackend.core.process.handler.counter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.comm.redis.client.RedisClient;
/**
 * 
 * @author chiqiu
 *
 */
public class IncreaseCounterHandler extends CounterHandler{
    Logger logger = LoggerFactory.getLogger(DecreaseCounterHandler.class);
    

    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        String key = null;
        try {
            Integer id = getIntParamInRequestSilently(request, ParamNameUtil.REPOST_TO_FEED_ID);
            if(id != null && id>0){
                key = counterConfig.getCounterKeyPrefix() + id;
                RedisClient client = null;
                try {
                    client = getRedisPoolContainer().borrowClient(counterConfig.getRedisCounterTarget());
                    client.getJedis().incr(key);
                } finally{
                    getRedisPoolContainer().returnClient(client, counterConfig.getRedisCounterTarget());
                }
            }
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, "error during increasing key:"+key, e);
        }
    }
}
