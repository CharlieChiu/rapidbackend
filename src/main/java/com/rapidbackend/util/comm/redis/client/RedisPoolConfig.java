package com.rapidbackend.util.comm.redis.client;

import org.springframework.beans.factory.annotation.Required;

public class RedisPoolConfig {// TODO rename this class
	public static int DEFAULT_POOL_SIZE = 5;
	public static int DEFAULT_DB_INDEX = 0;
	
	public int port;
	public String hostAddress;
		
	public String targetName;
	/**
	 * one database against one target
	 */
	public int databaseIndex = -1;
	/**
	 * how many 
	 */
	public int poolCapacity = -1;
	
	public String getTargetName() {
		return targetName;
	}
	@Required
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	
	public int getPoolCapacity() {
		return poolCapacity<=0?DEFAULT_POOL_SIZE:poolCapacity;
	}
	public void setPoolCapacity(int poolCapacity) {
		this.poolCapacity = poolCapacity;
	}
	
	public int getDatabaseIndex() {
		
		return databaseIndex<0?DEFAULT_DB_INDEX:databaseIndex;
	}
	public void setDatabaseIndex(int databaseIndex) {
		this.databaseIndex = databaseIndex;
	}
	public int getPort() {
		return port;
	}
	@Required
	public void setPort(int port) {
		this.port = port;
	}
	public String getHostAddress() {
		return hostAddress;
	}
	@Required
	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}
	
	public String getConfig(){
	    return hostAddress+":"+port+":"+databaseIndex;
	}
}
