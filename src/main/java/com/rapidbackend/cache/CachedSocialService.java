package com.rapidbackend.cache;

import com.rapidbackend.core.ClusterableService;

/**
 * A service with cacheable functions
 * @author chiqiu
 *
 */
public abstract class CachedSocialService extends ClusterableService{
	protected abstract void updateCacheOnReading(Object k, Object v);
	protected abstract void updateCacheOnWriting(Object k, Object v);
}
