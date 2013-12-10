package com.rapidbackend.security.shiro;

import org.apache.shiro.ShiroException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.session.mgt.SessionManager;

/**
 * Disable shiro's session management totally
 * @author chiqiu
 *
 */
public class DisabledSessionManager implements SessionManager{
    @Override
    public Session start(SessionContext context){
        throw new ShiroException("start function in sessionManager should not be called");
    }
    @Override
    public Session getSession(SessionKey key) throws SessionException{
        throw new ShiroException("getSession function in sessionManager should not be called, because sessionkey is always null");
    }
}
