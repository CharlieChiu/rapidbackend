package com.rapidbackend.cache;

import java.util.Collection;

import com.rapidbackend.core.context.AppContextAware;


/**
 * Cache extends this class should be able to lock its content by 'key'
 * @author chiqiu
 *
 */
public abstract class KeyLockable<T> extends AppContextAware{
        
    public abstract void lock(T key);
    
    public abstract void lock(Collection<T> keys);

    public abstract void unlock(T key);

    public abstract void unlock(Collection<T> keys);

    public abstract boolean isLocked(T key);
}
