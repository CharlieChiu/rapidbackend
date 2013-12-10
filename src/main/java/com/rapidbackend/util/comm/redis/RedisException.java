package com.rapidbackend.util.comm.redis;

import com.rapidbackend.util.comm.CommunicationException;

public class RedisException extends CommunicationException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5842223021687001638L;

	public RedisException(Integer errCode,String err,Throwable throwable){
		super(errCode,err,throwable);
	}
	
	public RedisException(Integer errCode,String err){
		super(errCode,err);
	}
	
	public RedisException(Integer errCode){
        super(errCode);
    }
}
