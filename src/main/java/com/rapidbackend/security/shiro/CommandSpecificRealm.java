package com.rapidbackend.security.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.security.session.SessionAware;
import com.rapidbackend.security.session.SessionBase;
import com.rapidbackend.security.session.SessionBasedCache;
/**
 * A realm which is only able to be used in certain commands.
 * By setting supported commands we can use a list of realms to validate incoming commands.
 * Note: Once we get to realms, we are actually validating some user profile based operations.
 * In most of these cases, we should process those operations with a session.
 * @author chiqiu
 *
 */
public abstract class CommandSpecificRealm extends AuthorizingRealm implements SessionAware{
    static ArrayList<String> EmptyCommandSet = new ArrayList<String>();
    
    protected SessionBase session;
    protected Cache<Object, AuthenticationInfo> authenticationCache;  
    protected Cache<Object, AuthorizationInfo> authorizationCache;
    
    @Override
    public Cache<Object, AuthenticationInfo> getAuthenticationCache() {
        return authenticationCache;
    }
    @Override
    public void setAuthenticationCache(
            Cache<Object, AuthenticationInfo> authenticationCache) {
        this.authenticationCache = authenticationCache;
    }
    @Override
    public Cache<Object, AuthorizationInfo> getAuthorizationCache() {
        return authorizationCache;
    }
    @Override
    public void setAuthorizationCache(
            Cache<Object, AuthorizationInfo> authorizationCache) {
        this.authorizationCache = authorizationCache;
    }
    
    @Override
    public SessionBase getSession() {
        return session;
    }
    /**
     * set session and create session based cache, enable caching
     * @param session
     */
    public void setSession(SessionBase session) {
        if(session !=null){
            this.session = session;
            authenticationCache = new AuthenticationSessionCache(session);
            authorizationCache = new AuthorizationSessionCache(session);
            setAuthenticationCachingEnabled(true);//enable caching
            setAuthorizationCachingEnabled(true);
        }
    }
    @Override
    public boolean supports(AuthenticationToken token) {
        boolean supported = false;
        if(token instanceof CommandSpecificToken){
            CommandSpecificToken commandSpecificToken = (CommandSpecificToken)token;
            String command = commandSpecificToken.getCommand();
            supported = supportsCommand(command);
        }
        return supported;
    }
    
    protected List<String> supportedCommands = EmptyCommandSet;

    public List<String> getSupportedCommands() {
        return supportedCommands;
    }
    @Required
    public void setSupportedCommands(List<String> supportedCommands) {
        this.supportedCommands = supportedCommands;
    }
    /**
     * check if the command is supported by this realm
     * @param command
     * @return
     */
    protected boolean supportsCommand(String command){
        boolean result = false;
        if(command != null){
            for(String c :supportedCommands){
                if(c.equalsIgnoreCase(command)){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
