package com.rapidbackend.security.shiro;

import org.apache.shiro.authc.AuthenticationInfo;

import com.rapidbackend.security.session.SessionBase;
import com.rapidbackend.security.session.SessionBasedCache;

public class AuthenticationSessionCache extends SessionBasedCache<Object, AuthenticationInfo>{
    public AuthenticationSessionCache(SessionBase session){
        super(session);
        setCacheName("AuthenticationSessionCache");
    }
    @Override
    protected String createStoredKey(String key){
        return "authenticationkey"+key;
    }
}