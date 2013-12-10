package com.rapidbackend.core.request;

import com.rapidbackend.core.BackendRuntimeException;

public class ParamException extends BackendRuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1479774715413127450L;

	public ParamException(Integer errCode,String err,Throwable throwable){
		super(BackendRuntimeException.BAD_REQUEST,err,throwable);
	}
	
	public ParamException(Integer errCode,String err){
		super(BackendRuntimeException.BAD_REQUEST,err);
	}
	
	public ParamException(Integer errCode){
        super(BackendRuntimeException.BAD_REQUEST);
    }
}
