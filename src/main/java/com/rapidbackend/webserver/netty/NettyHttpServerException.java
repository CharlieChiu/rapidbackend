package com.rapidbackend.webserver.netty;

public class NettyHttpServerException extends RuntimeException{

    private static final long serialVersionUID = -3343249468824279829L;
    
    public NettyHttpServerException(String message) {
        super(message);
    }

    public NettyHttpServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyHttpServerException(Throwable cause) {
        super(cause);
    }
}
