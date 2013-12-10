package com.rapidbackend.socialutil.security;

import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.codehaus.jackson.annotate.JsonTypeInfo;
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class AuthenticationInfoWrapper extends SimpleAuthenticationInfo{

    /**
     * 
     */
    private static final long serialVersionUID = 8118556347697661992L;    
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    protected SimplePrincipalCollection principals;
    
    @Override
    public PrincipalCollection getPrincipals(){
        return principals;
    }
    
    public AuthenticationInfoWrapper(Object principal, Object credentials, ByteSource source,String realmName) {
        super(principal, credentials, source,realmName);
        this.principals = (SimplePrincipalCollection)super.getPrincipals();
    }
    public AuthenticationInfoWrapper(){
        super();
    }
    
}
