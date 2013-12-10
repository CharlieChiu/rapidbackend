package com.rapidbackend.cache;

import java.util.Set;

/**
 * A cache mapper used for sharding redis.<br/>
 * user can extend this class and implement own sharding policy.<br/>
 * For now, the design is to use  redis as the only cache layer for all the 
 * services . So sharding method will all ways return a redis target name string which can be configured in spring xml<br/>
 * 
 * @author chiqiu
 *
 */
public interface CacheMapper {
    /**
     * implemention of this method will direct a redis operation to different
     * redis targets according to the return target name
     * @param input
     * @return
     */
    public String getRedisTargetName(Object input);
    
    /***
     * 
     * @return all the redis instances this mapper may point to
     */
    public Set<String> getAllRedisTargetNames();
    
}
