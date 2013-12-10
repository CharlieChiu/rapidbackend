package com.rapidbackend.security.session;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.security.shiro.PojoAuthenticationInfo;
import com.rapidbackend.security.shiro.PojoPrincipalCollection;
import com.rapidbackend.socialutil.model.reserved.UserBase;

/**
 * Default uuid based session implementation.
 * @author chiqiu
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SessionBase {
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    protected HashMap<String, Object> content = new HashMap<String, Object>();
    
    protected String sessionId;
    protected long creationTime;
    protected long lastAccessTime;
    protected long maxInactiveInterval;
    protected int timeoutSeconds;// timeout value in seconds
    protected boolean invalid = false;
    protected static String randomUUID = UUID.randomUUID().toString();
    protected static Object defaultShardingKey = new Integer(0);
    protected SessionBase(){}
        
    protected Object shardingKey = defaultShardingKey;
    protected static String delimiter = ":";
    public Object getShardingKey() {
        return shardingKey;
    }
    /**
     * the sharding key is used for sharding redis session to different redis instances.
     * User need to set it to a proper value and handle it with a key->redis instance mapper if we want to use sharding.
     * @param shardingKey
     */
    public void setShardingKey(Object shardingKey) {
        this.shardingKey = shardingKey;
    }
    /**
     * 
     * @param sessionId which should contain sharding key and a uuid
     */
    protected SessionBase(String sessionId){
        setSessionId(sessionId);
        creationTime = System.currentTimeMillis();
    }
    
    public boolean isInvalid() {
        return invalid;
    }
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    public long getCreationTime() {
        return creationTime;
    }
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
    public long getLastAccessTime() {
        return lastAccessTime;
    }
    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }
    public String getSessionId() {
        if(sessionId==null){
            sessionId = generateSessionId();
        }
        return sessionId;
    }
    /**
     * @param sessionId which should contain sharding key and a uuid
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        checkSessionId();
    }
    public long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }
    public void setMaxInactiveInterval(long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }
    public Object getAttribute(String key){
        setLastAccessTime(System.currentTimeMillis());
        return content.get(key);
    }
    public void setAttribute(String key,Object val){
        setLastAccessTime(System.currentTimeMillis());
        content.put(key, val);
    }
    public Object removeAttribute(String key){
        setLastAccessTime(System.currentTimeMillis());
        return content.remove(key);
    }
    
    protected String generateSessionId(){
        return shardingKey.toString()+delimiter+ UUID.randomUUID().toString();
    }
    
    @Override
    public String toString(){
        return getSessionId();
    }
    public HashMap<String, Object> getContent() {
        return content;
    }
    public void setContent(HashMap<String, Object> content) {
        this.content = content;
    }
    /**
     * check if the session id is a uuid and set the sharding key
     * @return
     */
    @JsonIgnore
    private boolean checkSessionId(){
        boolean result = false;
        if(!StringUtils.isEmpty(sessionId)){
            String[] parts = StringUtils.split(sessionId,delimiter);
            if(parts!=null&& parts.length==2){
                shardingKey = parts[0];
                String uuid = parts[1];
                if(isValidUUID(uuid)){
                    result = true;
                }
            }
        }
        if(!result)
            throw new SessionException("session id is not valid : "+sessionId);
        return result;
    }
    
    private boolean isValidUUID(String uuid){
        boolean result = false;
        if(!StringUtils.isEmpty(uuid)){
            if(uuid.length()==36 &&
                    uuid.charAt(8)=='-' &&
                    uuid.charAt(13)=='-' &&
                    uuid.charAt(18)=='-' &&
                    uuid.charAt(23)=='-'){
                result = true;
            }
        }
        return result;
    }
    
    public UserBase getUser(){
        PojoAuthenticationInfo authenticationInfo = null;
        HashMap<String, Object> sessionContent = getContent();
     // we cann't know the username here, so we loop the values
        for(Object o: sessionContent.values()){
            if(o instanceof PojoAuthenticationInfo){
                authenticationInfo = (PojoAuthenticationInfo)o;
            }
        }
        if(authenticationInfo == null|| authenticationInfo.getPrincipals().isEmpty()){
            throw new BackendRuntimeException(BackendRuntimeException.NON_AUTHORITATIVE_INFORMATION,"can'f find authenticate info in session");
        }
        PojoPrincipalCollection pojoPrincipalCollection = (PojoPrincipalCollection)authenticationInfo.getPrincipals();
        Object principalObject = pojoPrincipalCollection.getPrimaryPrincipal();
        if(principalObject !=null && principalObject instanceof UserBase){
            UserBase user = (UserBase) principalObject;
            return user;
        }else {
            throw new BackendRuntimeException(BackendRuntimeException.NON_AUTHORITATIVE_INFORMATION,"can'f find authenticate info in session");
        }
    }
}
