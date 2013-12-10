package com.rapidbackend.socialutil.subscription;

import java.util.BitSet;

import com.rapidbackend.cache.local.LocalJvmCache;

/**
 * ?? do we need it?Do we need Local jvm cache for friendship?
 * @author chiqiu
 *
 */
public class SubscriptionChangedCache extends LocalJvmCache{
	protected BitSet cache = new BitSet();
	/**
	 * return false by default
	 * @param userId
	 * @return
	 */
	public boolean followerUnchanged(int userId){
		return cache.get(userId);
	}
	/**
	 * after every reading, mark the user's list as unchanged<BR>
	 * Then we can get the only 
	 * @return
	 */
	public void markAsUnchangedAfterReading(int userId){
	    cache.set(userId, true);
	}
	
	public void markAsChangedAfterWriting(int userId){
	    cache.set(userId, false);
	}
	
}
