package com.rapidbackend.security.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.shiro.session.InvalidSessionException;
import org.codehaus.jackson.annotate.JsonTypeInfo;
/**
 * sessionbase wrapper for shiro session
 * @author chiqiu
 *
 */
@Deprecated
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ShrioSessionWrapper implements Session{
    private static String assertString(Object key) {
        if (!(key instanceof String)) {
            String msg = "HttpSession based implementations of the Shiro Session interface requires attribute keys " +
                    "to be String objects.  The HttpSession class does not support anything other than String keys.";
            throw new IllegalArgumentException(msg);
        }
        return (String) key;
    }
    
    protected SessionBase sessionBase;
    
    protected String id;
    protected Date startTimestamp;
    protected Date lastAccessTime;
    protected long timeout;
    protected String host;
    @Override
    public Serializable getId() {
        return sessionBase.getSessionId();
    }
    @Override
    public Date getStartTimestamp() {
        return new Date(sessionBase.getCreationTime());
    }
    @Override
    public Date getLastAccessTime() {
        return new Date(sessionBase.getLastAccessTime());
    }
    @Override
    public void setTimeout(long timeoutValue) throws InvalidSessionException{
        sessionBase.setTimeoutSeconds((int)timeoutValue/1000);
    }
    @Override
    public long getTimeout() throws InvalidSessionException{
        return sessionBase.getTimeoutSeconds()*1000;
    }
    @Override
    public void setAttribute(Object key, Object value) throws InvalidSessionException {
        try {
            sessionBase.setAttribute(assertString(key), value);
            sessionBase.setLastAccessTime(System.currentTimeMillis());
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }
    @Override
    public Object getAttribute(Object key) throws InvalidSessionException {
        try {
            sessionBase.setLastAccessTime(System.currentTimeMillis());
            return sessionBase.getAttribute(assertString(key));
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }
    @Override
    public Object removeAttribute(Object key) throws InvalidSessionException {
        try {
            String sKey = assertString(key);
            sessionBase.setLastAccessTime(System.currentTimeMillis());
            return sessionBase.removeAttribute(sKey);
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }
    private static final String HOST_SESSION_KEY = ShrioSessionWrapper.class.getName() + ".HOST_SESSION_KEY";
    private static final String TOUCH_OBJECT_SESSION_KEY = ShrioSessionWrapper.class.getName() + ".TOUCH_OBJECT_SESSION_KEY";
    public void stop() throws InvalidSessionException {
        sessionBase.setInvalid(true);
    }
    @Override
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        try {
            Set<String> namesEnum = sessionBase.getContent().keySet();
            Collection<Object> keys = null;
            if (namesEnum != null) {
                keys = new ArrayList<Object>();
                for(String key:namesEnum){
                    keys.add(key);
                }
            }
            return keys;
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }
    @Override
    public void touch() throws InvalidSessionException {
        //just manipulate the session to update the access time:
        try {
            setAttribute(TOUCH_OBJECT_SESSION_KEY, TOUCH_OBJECT_SESSION_KEY);
            removeAttribute(TOUCH_OBJECT_SESSION_KEY);
        } catch (Exception e) {
            throw new InvalidSessionException(e);
        }
    }
    @Override
    public String getHost() {
        return (String) getAttribute(HOST_SESSION_KEY);
    }
    
    public ShrioSessionWrapper(SessionBase sessionBase){
        this.sessionBase = sessionBase;
    }
    @Override
    public SessionBase getSessionBase() {
        return sessionBase;
    }
    @Override
    public void setSessionBase(SessionBase sessionBase) {
        this.sessionBase = sessionBase;
    }
    
}
