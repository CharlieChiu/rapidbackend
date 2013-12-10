package com.rapidbackend.security.shiro;

import org.apache.shiro.authz.AuthorizationInfo;

import com.rapidbackend.security.session.SessionBase;
import com.rapidbackend.security.session.SessionBasedCache;

public class AuthorizationSessionCache extends SessionBasedCache<Object, AuthorizationInfo>{
    public AuthorizationSessionCache(SessionBase session){
        super(session);
        setCacheName("AuthorizationSessionCache");
    }
    @Override
    protected String createStoredKey(String key){
        return "authorizationkey"+key;
    }
}