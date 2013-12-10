package com.rapidbackend.cache;

public class RedisCacheConfig {
    /**
     * if user want to use redis as simple lru cache, user can set this to true.
       Redis cache will not call expire command. This can boost a little performanc
     */
    protected boolean expire = false;
    protected int expireTime = 24*3600;//expire time in seconds, default is 24 hours

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public boolean isExpire() {
        return expire;
    }

    public void setExpire(boolean expire) {
        this.expire = expire;
    }
    
}
