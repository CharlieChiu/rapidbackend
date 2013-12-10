package com.rapidbackend.security.shiro;

import org.apache.shiro.util.SimpleByteSource;

/**
 * Use this type of bytesource to avoid json deserializing errors.
 * Make sure to use this class as salt when you have shiro session based cache enabled.  
 * @author chiqiu
 *
 */
public class MoreSimpleByteSource extends SimpleByteSource{
    static byte[] EmptyBytes = "".getBytes(); 
    public MoreSimpleByteSource(){
        super(EmptyBytes);
    }
    public MoreSimpleByteSource(String value){
        super(value);
    }
    protected boolean empty = true;
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    
    
}
