package com.rapidbackend.core.process.handler.cache;

import java.util.Map;

import com.rapidbackend.core.request.CommandRequest;

/**
 * if an handler's action needs we change the cache content, we can implement
 * this interface, pass down handler result to following cache handlers
 * @author chiqiu
 *
 */
public interface CacheTrigger {
    public void passHandlerResultToCacheHandler(CommandRequest request, Map<String, Object> objects);
}
