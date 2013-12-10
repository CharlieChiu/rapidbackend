package com.rapidbackend.webserver.netty.request;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
/**
 * @author chiqiu
 */
public class MethodValidator implements RequestInterceptor{
    public void intercept(HttpRequest request) throws Exception{
        HttpMethod method = request.getMethod();
        if(!method.getName().equalsIgnoreCase(HttpMethod.GET.getName())
                &&!method.getName().equalsIgnoreCase(HttpMethod.POST.getName())
                &&!method.getName().equalsIgnoreCase(HttpMethod.PUT.getName())
                &&!method.getName().equalsIgnoreCase(HttpMethod.DELETE.getName())){
            throw new RuntimeException("unsupported method:"+method.getName());
        }
    }
}
