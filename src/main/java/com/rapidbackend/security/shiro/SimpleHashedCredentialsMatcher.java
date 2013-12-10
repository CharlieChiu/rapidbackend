package com.rapidbackend.security.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
/**
 * This class is used to hash user's password. It Extends @HashedCredentialsMatcher to expose @HashedCredentialsMatcher's hash functions.
 * for each user.This class is used in Default shiro Realm implementation {@CommandSpecificRealm}s<br/>
 * you can override its getPasswordSalt methods to implement your own salt management.
 * Note: The password salt should be configured only once in this implementation, otherwise, all users will be blocked from login.
 * @author chiqiu
 *
 */
public class SimpleHashedCredentialsMatcher extends HashedCredentialsMatcher implements PasswordSaltProvider{
    
    
    protected MoreSimpleByteSource passwordSalt;
    /**
     * Please rewrite this method if you wanna provide each login subject a separated salt
     * @return the salt for login subject
     */
    @Override
    public MoreSimpleByteSource getPasswordSalt() {
        return passwordSalt;
    }
    @Override
    public MoreSimpleByteSource getPasswordSalt(Object input) {
        return passwordSalt;
    }
    
    public void setPasswordSalt(MoreSimpleByteSource passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
    
    public String hashPassword(String password){
        if(isStoredCredentialsHexEncoded()){
            return hashProvidedCredentials(password,passwordSalt,getHashIterations())
                    .toHex();
        }else {
            return hashProvidedCredentials(password,passwordSalt,getHashIterations())
            .toBase64();
        }
    }
    
}
