package com.rapidbackend.socialutil.feeds;

import com.rapidbackend.core.BackendRuntimeException;
/**
 * This exception hierarchy aims to let user code find and handle the
 * kind of error encountered during using feed service.
 * @author chiqiu
 *
 */
public class FeedException extends BackendRuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6648834941574797080L;
	
	public FeedException(Integer errCode,String err,Throwable throwable){
		super(errCode,err,throwable);
	}
	
	public FeedException(Integer errCode,String err){
		super(errCode,err);
	}
	public FeedException(Integer errCode){
        super(errCode);
    }

}
