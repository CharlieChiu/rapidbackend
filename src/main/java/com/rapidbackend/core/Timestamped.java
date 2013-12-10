package com.rapidbackend.core;
/**
 * 
 * @author chiqiu
 *
 */
public interface Timestamped {
	/**
	 * human readable time stamp
	 * @return
	 */
	public String getTimestamp();
	/**
	 * unix time stamp on this object
	 * @return
	 */
	public long getUnixTimestamp();
}
