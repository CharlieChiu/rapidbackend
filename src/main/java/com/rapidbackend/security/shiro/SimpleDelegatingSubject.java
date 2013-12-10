package com.rapidbackend.security.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.support.DelegatingSubject;

import com.rapidbackend.core.request.CommandRequest;

/**
 * Writing our own Shiro delegating subject, didn't expect to go this far......
 * Explicitly create all subjects by using SimpleDelegatingSubject(SocialRequest request,SecurityManager securityManager)
 * disable session usage by set the DelegatingSubject.sessionCreationEnabled=false. Then no shiro seesion will be used. Session manager api 
 * are called in DelegatingSubject.getSession(boolean)
 * @author chiqiu
 *
 */
public class SimpleDelegatingSubject extends DelegatingSubject{
    
    protected CommandRequest request;
    
    public CommandRequest getRequest() {
        return request;
    }

    public void setRequest(CommandRequest request) {
        this.request = request;
    }
    
    public SimpleDelegatingSubject(CommandRequest request,SecurityManager securityManager){
        super(null,false,null,null,false,securityManager);
        this.request = request;
    }
    
    /**
     * added setters for super's variable principals, check related source if shiro upgrades
     * @param principals
     */
    public void setPrincipals(PrincipalCollection principals){
        this.principals = principals;
    }
    /**
     * added setters for super's variable authenticated, check related source if shiro upgrades
     * @param authenticated
     */
    public void setAuthenticated(boolean authenticated){
        this.authenticated = authenticated;
    }
    /**
     * added setters for super's variable host, check related source if shiro upgrades
     * @param host
     */
    public void setHost(String host){
        this.host = host;
    }
}
