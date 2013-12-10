package com.rapidbackend.security.session;

import com.rapidbackend.core.context.AppContextAware;
/**
 * State less implementation of session factory.
 * User can implement new session factory with session init handler and session store handlers to change.
 * session behavior.
 * @author chiqiu
 *
 */
public class SessionFactory extends AppContextAware{
    protected static SessionConf sessionConf;
    public static Class<?> getSessionClass(){
        return SessionBase.class;
    }
    
    /**
     * create a session with a session id.This is usually used when server handles an incoming request.
     * Where request may contain an session id. The incoming sessionid should be checked by the session implementation to reduce cache load.
     * @param sessionId
     * @return
     */
    public static SessionBase createSession(String sessionId){
        SessionBase sessionBase = new SessionBase(sessionId);
        sessionBase.setTimeoutSeconds(getTimeoutSeconds());
        return sessionBase;
    }
    /**
     * We only create empty sessions when we don't store and manage them.
     * Those sessions are used for shiro compatible only.
     * @return
     */
    /*public static Session createSession(){
        SessionBase sessionBase = new SessionBase();
        sessionBase.setTimeoutSeconds(getTimeoutSeconds());
        ShrioSessionWrapper session = new ShrioSessionWrapper(sessionBase);
        return session;
    }
    /**
     * return a shiro session wrapper of existing session base
     * @param sessionBase
     * @return
     */
    /*public static Session createSession(SessionBase sessionBase){
        sessionBase.setTimeoutSeconds(getTimeoutSeconds());
        ShrioSessionWrapper session = new ShrioSessionWrapper(sessionBase);
        return session;
    }*/
    
    public static SessionBase createSession(){
        SessionBase sessionBase = new SessionBase();
        sessionBase.setTimeoutSeconds(getTimeoutSeconds());
        return sessionBase;
    }
    
    
    public static int getTimeoutSeconds(){
        if(null== sessionConf){
            sessionConf = (SessionConf)getApplicationContext().getBean("SessionConf");
        }
        return sessionConf.getSessionExpireSeconds();
    }
}
