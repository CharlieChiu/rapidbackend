package com.rapidbackend.webserver.netty.request.data;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.context.AppContextAware;

public class RequestDataFilterFactory extends AppContextAware{
    protected ConcurrentHashMap<String, RequestDataFilter> filterCache;
    public ConcurrentHashMap<String, RequestDataFilter> getFilterCache() {
        return filterCache;
    }
    @Required
    public void setFilterCache(
            ConcurrentHashMap<String, RequestDataFilter> filterCache) {
        this.filterCache = filterCache;
    }
    public RequestDataFilter getRequestDataFilter(String command){
        return filterCache.get(command);
    }
}
