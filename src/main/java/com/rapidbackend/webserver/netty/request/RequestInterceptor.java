package com.rapidbackend.webserver.netty.request;

import org.jboss.netty.handler.codec.http.HttpRequest;

public interface RequestInterceptor {
    public void intercept(HttpRequest request) throws Exception;
}
