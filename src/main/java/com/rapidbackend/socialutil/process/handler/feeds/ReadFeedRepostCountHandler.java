package com.rapidbackend.socialutil.process.handler.feeds;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.handler.counter.CounterConfig;
import com.rapidbackend.core.process.handler.counter.ReadCounterValueHandler;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;

public class ReadFeedRepostCountHandler extends ReadCounterValueHandler{
    
    protected CounterConfig counterConfig;
    
    
    public CounterConfig getCounterConfig() {
        return counterConfig;
    }

    public void setCounterConfig(CounterConfig counterConfig) {
        this.counterConfig = counterConfig;
    }


    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        
    }
}
