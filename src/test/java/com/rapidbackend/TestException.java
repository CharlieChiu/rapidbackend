package com.rapidbackend;
/**
 * 
 * @author chiqiu
 *
 */
public class TestException extends RuntimeException{

    /**
     * 
     */
    private static final long serialVersionUID = -6951688553048996959L;
    
    public TestException(String msg,Throwable t){
        super(msg, t);
    }
    public TestException(String msg){
        super(msg);
    }
}
