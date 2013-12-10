package com.rapidbackend.cache.local;
/**
 * A local cache means the cache is on the same machine on which the JVM in running
 * @author chiqiu
 *
 */
public abstract class LocalCache {
    public enum State { 
        /** :TODO */
        CREATED, 
        /** :TODO */
        STATICWARMING, 
        /** :TODO */
        AUTOWARMING, 
        /** :TODO */
        LIVE 
      }
}
