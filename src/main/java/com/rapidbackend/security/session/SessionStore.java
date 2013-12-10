package com.rapidbackend.security.session;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.cache.RedisCache;
import com.rapidbackend.core.context.AppContextAware;

/**
 * Redis session store
 * @author chiqiu
 *
 */
public class SessionStore extends AppContextAware{
    protected RedisCache redisCache;
    public RedisCache getRedisCache() {
        return redisCache;
    }
    @Required
    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
    public String redisKey(String sessionId){
        return "session"+sessionId;
    }
    /**
     * @param sessionBase 
     * @return
     * @throws SessionException
     */
    public SessionBase load(SessionBase sessionBase) throws SessionException{
        try {
            return  redisCache.getJsonObject(redisKey(sessionBase.getSessionId()), sessionBase.getShardingKey(), SessionBase.class);
        } catch (Exception e) {
            throw new SessionException("error loading session:" + sessionBase.getSessionId(), e);
        }
    }
    
      
    /**
     * 
     * @param session
     * @return
     * @throws SessionException
     */
    public boolean store(SessionBase session) throws SessionException{
        try {
            return  redisCache.setJsonObjectAndExpire(redisKey(session.getSessionId()), session, session.getShardingKey(), session.getTimeoutSeconds());
        } catch (Exception e) {
            throw new SessionException("error storing session:" + session, e);
        }
    }
        
    public boolean exists(SessionBase session){
        try {
            return  redisCache.exists(redisKey(session.getSessionId()), session.getShardingKey());
        } catch (Exception e) {
            throw new SessionException("error testing session:" + session, e);
        }
    }
    
    public void delete(SessionBase session){
        try {
            redisCache.delete(redisKey(session.getSessionId()), session.getShardingKey());
        } catch (Exception e) {
            throw new SessionException("error deleting session:" + session, e);
        }
    }
}
