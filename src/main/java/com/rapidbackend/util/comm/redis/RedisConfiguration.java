package com.rapidbackend.util.comm.redis;

import java.util.HashMap;

/**
 * Redis configuration class
 * @author chiqiu
 *
 */
public class RedisConfiguration {
	/**
	 * contains database names and ids
	 */
	public HashMap<String, Integer> databases;
	
	public RedisConfiguration(){
		
	}
	public String address;
	public String port;
	
	
}
