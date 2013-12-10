package com.rapidbackend.security.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.MergableAuthenticationInfo;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class PojoAuthenticationInfo implements MergableAuthenticationInfo, SaltedAuthenticationInfo{
    /**
     * 
     */
    private static final long serialVersionUID = -4090213286796567405L;
    /**
     * used for deserializing
     */
    public PojoAuthenticationInfo(){
    }
    public PojoAuthenticationInfo(Object principal, Object hashedCredentials, MoreSimpleByteSource credentialsSalt, String realmName) {
        this.principals = new PojoPrincipalCollection(principal, realmName);
        this.credentials = hashedCredentials;
        this.credentialsSalt = credentialsSalt;
    }
    protected MoreSimpleByteSource credentialsSalt;
    @Override
    public ByteSource getCredentialsSalt() {
        return credentialsSalt;
    }
    public void setCredentialsSalt(MoreSimpleByteSource credentialsSalt) {
        this.credentialsSalt = credentialsSalt;
    }
    
    /**
     * The principals identifying the account associated with this AuthenticationInfo instance.
     */
    @JsonDeserialize(as=PojoPrincipalCollection.class)
    protected PrincipalCollection principals;
    protected Object credentials;
    @Override
    public Object getCredentials() {
        return credentials;
    }
    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }
    @Override
    public PrincipalCollection getPrincipals() {
        return principals;
    }
    public void setPrincipals(PrincipalCollection principals) {
        this.principals = principals;
    }
    @Override
    public void merge(AuthenticationInfo info){
        
    }
}
