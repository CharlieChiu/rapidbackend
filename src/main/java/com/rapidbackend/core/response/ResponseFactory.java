package com.rapidbackend.core.response;
/**
 * 
 * @author chiqiu
 *
 */
public class ResponseFactory {
    public static CommandResponse createResponse(){
        return new ResponseBase();
    }
}
