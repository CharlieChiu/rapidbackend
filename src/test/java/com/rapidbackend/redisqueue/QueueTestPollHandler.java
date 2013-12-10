package com.rapidbackend.redisqueue;

import java.util.ArrayList;
import java.util.List;

import com.rapidbackend.util.io.JsonUtil;

public class QueueTestPollHandler extends RedisQueuePollHandler{
    
    public static List<String> revieved = new ArrayList<String>();
    
    public void handleReturnValue(Object redisqueueItem){
        try{
            logger.info("handleReturnValue  ------>");
            setCurrentState("handling return value");
            setStartHandleTime(System.currentTimeMillis());
            handledRequestNum ++;
            if(null == redisqueueItem || redisqueueItem.equals("")){
                logger.info("null polled");
                logger.info("handleReturnValue  <------");
                return ;
            }
            String s = JsonUtil.readObject(redisqueueItem.toString().getBytes(), String.class);
            revieved.add(s);
            
            logger.info("handling "+redisqueueItem.toString());
            
            logger.info("handleReturnValue  <------");
        }catch(Exception e){
            logger.error("QueueTestPollHandler error:",e);
        }
        
    }
}
