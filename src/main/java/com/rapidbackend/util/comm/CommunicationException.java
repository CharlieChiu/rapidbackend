package com.rapidbackend.util.comm;

import com.rapidbackend.core.BackendRuntimeException;

public class CommunicationException extends BackendRuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1300098100687090921L;

	public CommunicationException(Integer errCode,String err,Throwable throwable){
		super(errCode,err,throwable);
	}
	
	public CommunicationException(Integer errCode,String err){
		super(errCode,err);
	}
	public CommunicationException(Integer errCode){
        super(errCode);
    }
}
