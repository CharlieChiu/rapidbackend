package com.rapidbackend.socialutil.sharding.defaultmappers;

/**
 * Mapper one user to different redis counters
 * @author chiqiu
 *
 */
public class RedisCounterLocationMapper {
	public final String DEFAULT_COUNTER_TARGETNAME = "metadataCounter";
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	public String getRedisCounterTargetName(Object userId){
        return DEFAULT_COUNTER_TARGETNAME;
    }
	
}
