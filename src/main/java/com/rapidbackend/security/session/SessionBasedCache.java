package com.rapidbackend.security.session;

import java.util.Collection;
import java.util.Set;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.security.shiro.PojoPrincipalCollection;
/**
 * A session based key stores authorize and authentic info into a session.
 * @author chiqiu
 *
 * @param <K>
 * @param <V>
 */
public abstract class SessionBasedCache<K,V> implements Cache<K, V>{

    /**
     * Private internal log instance.
     */
    private static final Logger log = LoggerFactory.getLogger(SessionBasedCache.class);
    
    public SessionBasedCache(SessionBase session){
        setSession(session);
    }
    protected String cacheName;
    
    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    protected SessionBase session;
    
    public SessionBase getSession() {
        return session;
    }

    public void setSession(SessionBase session) {
        this.session = session;
    }
    
    /**
     * the key stored in
     * @param key
     * @return
     */
    protected abstract String createStoredKey(String key);
    /**
     * Gets a value of an element which matches the given key.
     *
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if not found or expired
     */
    public V get(K key) throws CacheException {
        try {
            if (log.isTraceEnabled()) {
                log.trace("Getting object from cache ["  + "] for key [" + key + "]");
            }
            if (key == null) {
                return null;
            } else {
                String internalKey = createInternalKey(key);
                return (V)session.getAttribute(createStoredKey(internalKey));
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }
    /**
     * only supports string and PojoPrincipalCollection as key
     * @param key
     * @return
     */
    protected  String createInternalKey(K key){
       
        if(key instanceof String || key instanceof PojoPrincipalCollection){
            return key.toString();
        }else {
            throw new ShiroException("only PojoPrincipalCollection or String key are supported in this cache, not support:"+key.getClass().getSimpleName());
        }
    }
    protected  boolean checkValue(V value){
        if(value instanceof AuthenticationInfo 
                || value instanceof AuthorizationInfo){
            return true;
        }else {
            return false;
        }
    }
    /**
     * Puts an object into the cache.
     *
     * @param key   the key.
     * @param value the value.
     */
    public V put(K key, V value) throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Putting object in cache ["  + "] for key [" + key + "]");
        }
        try {
            checkValue(value);
            V previous = get(key);
            //String internalKey = createInternalKey(key);
            session.setAttribute(createSessionAttributeName(key), value);
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Removes the element which matches the key.
     *
     * <p>If no element matches, nothing is removed and no Exception is thrown.</p>
     *
     * @param key the key of the element to remove
     */
    public V remove(K key) throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Removing object from cache [" + getCacheName() + "] for key [" + key + "]");
        }
        try {
            V previous = get(key);
            //String internalKey = createInternalKey(key);
            session.removeAttribute(createSessionAttributeName(key));
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }
    
    public String createSessionAttributeName(K key){
        String internalKey = createInternalKey(key);
        return createStoredKey(internalKey);
    }
    /**
     * Removes all elements in the cache, but leaves the cache in a useable state.
     * We did nothing in this fucntion because there's no need to remove anything 
     * Cache will be evicted when session expires or session store is full.
     */
    public void clear() throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Clearing all objects from cache [" + getCacheName() + "]");
        }
    }

    public int size() {
        try {
            return 1;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    public Set<K> keys() {
        throw new RuntimeException("SessionBasedCache.keys() should never be called");
    }

    public Collection<V> values() {
        throw new RuntimeException("SessionBasedCache.values() should never be called");
        
    }

    /**
     * Returns &quot;EhCache [&quot; + cache.getName() + &quot;]&quot;
     *
     * @return &quot;EhCache [&quot; + cache.getName() + &quot;]&quot;
     */
    public String toString() {
        return "Sessionbased Cache [" + getCacheName() + "]";
    }
}
