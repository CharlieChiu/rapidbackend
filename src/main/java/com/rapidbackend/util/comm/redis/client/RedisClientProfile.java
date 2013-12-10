package com.rapidbackend.util.comm.redis.client;

public class RedisClientProfile {
	public int id;
	public String targetName;
	/**
	 * 
	 */
	public String hostaddress;
	public int port;
	public int databaseIndex;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}
	public String getHostaddress() {
		return hostaddress;
	}
	public void setHostaddress(String hostaddress) {
		this.hostaddress = hostaddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getDatabaseIndex() {
		return databaseIndex;
	}
	public void setDatabaseIndex(int databaseIndex) {
		this.databaseIndex = databaseIndex;
	}
	public RedisClientProfile(RedisPoolConfig poolConfig, int idx){
		this.targetName = poolConfig.getTargetName();
		this.port = poolConfig.getPort();
		this.hostaddress = poolConfig.getHostAddress();
		this.databaseIndex = poolConfig.getDatabaseIndex();
		this.id = idx;
	}
	@Override
	public String toString(){
	    return "targetName:"+targetName+","+"port:"+port+","+
	    "hostaddress:"+hostaddress+","+"databaseIndex:"+databaseIndex+","+"id:"+id;
	}
}
