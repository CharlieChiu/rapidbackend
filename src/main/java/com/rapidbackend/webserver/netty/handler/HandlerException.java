package com.rapidbackend.webserver.netty.handler;

public class HandlerException extends RuntimeException{
    /**
     * 
     */
    private static final long serialVersionUID = 5324840915153618588L;

    public HandlerException(String msg,Throwable e){
        super(msg, e);
    }
    public HandlerException(Throwable e){
        super(e);
    }
    public HandlerException(String msg){
        super(msg);
    }
}
