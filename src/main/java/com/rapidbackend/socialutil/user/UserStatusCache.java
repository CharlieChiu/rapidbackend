package com.rapidbackend.socialutil.user;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.rapidbackend.cache.local.LocalJvmCache;

/**
 * cache the status of user for each type of 'followable'.
 * Including if 
 * @author chiqiu
 *
 */
public class UserStatusCache extends LocalJvmCache implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7306893119087437850L;
	protected BitSet initializationStatus = new BitSet();
	protected ConcurrentHashMap<Integer, Long> userInboxExpireTime = new ConcurrentHashMap<Integer, Long>();
	protected BitSet onlineUsers = new BitSet();// 100 times faster than hash
	protected final ReentrantLock sweepLock = new ReentrantLock(true);
	/**
	 * 
	 * @param inboxId
	 * @return
	 */
	@Deprecated
	public void markAsInitialized(int inboxId){
		initializationStatus.set(inboxId);
	}
	/**
	 * 
	 * @param inboxKey
	 */
	public boolean isInitialized(int inboxId){
		return initializationStatus.get(inboxId);
	}
	public BitSet getInitializationStatus() {
		return initializationStatus;
	}
	public void setInitializationStatus(BitSet initializationStatus) {
		this.initializationStatus = initializationStatus;
	}
	
	protected boolean isCleaning = false;
	
	public boolean isCleaning() {
        return isCleaning;
    }
    public void setCleaning(boolean isCleaning) {
        this.isCleaning = isCleaning;
    }
    /**
     * @param userId
     * @param ttl time to leave,in milliseconds
     */
    public void expireOnlineUser(Integer userId,Long ttl){
        Long expiretime = System.currentTimeMillis()+ttl;
        userInboxExpireTime.put(userId, expiretime);
        onlineUsers.set(userId);
    }
    
    public boolean isUserOnline(Integer userId){
        return onlineUsers.get(userId);
    }
    
    public long onlineUserNumber(){
        return onlineUsers.cardinality();
    }
    
    public ConcurrentHashMap<Integer, Long> getUserInboxExpireTime() {
        return userInboxExpireTime;
    }
    public void setUserInboxExpireTime(
            ConcurrentHashMap<Integer, Long> userInboxExpireTime) {
        this.userInboxExpireTime = userInboxExpireTime;
    }
    public BitSet getOnlineUsers() {
        return onlineUsers;
    }
    public void setOnlineUsers(BitSet onlineUsers) {
        this.onlineUsers = onlineUsers;
    }
    protected void sweep(){
	    if(!sweepLock.tryLock()) return;
	    try {
	        long currentTime = System.currentTimeMillis();
	        isCleaning = true;
	        for(Map.Entry<Integer, Long> entry:userInboxExpireTime.entrySet()){
	            if(entry.getValue()<currentTime){
	                userInboxExpireTime.remove(entry.getKey());
	                onlineUsers.set(entry.getKey(),false);
	            }
	        }
        } finally{
            isCleaning = false;
            sweepLock.unlock();
        }
	}
	/**
	 * 
	 * @author chiqiu
	 *
	 */
	public static class OnlineUserSweeper implements Runnable{
	    private WeakReference<UserStatusCache> expireData;// we may not need weak reference here, but it is a good manner
	    public OnlineUserSweeper(UserStatusCache data){
	        expireData = new WeakReference<UserStatusCache>(data);
	    }
	    public void run(){
	        UserStatusCache data = expireData.get();
	        if(data!=null){
	            data.sweep();
	        }
	    }
	}
	
}
