package com.rapidbackend.security.shiro;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

import com.rapidbackend.socialutil.security.UserLoginRealm.UseridPasswordToken;


/**
 * Test user login with pre-configured user name password
 * @author chiqiu
 *
 */
public class PlainUserNamePasswordRealm extends CommandSpecificRealm{
    /**
     * supports all commands
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return true;
    }
    /**
     * This function gets user's name(principal), password(credentials),salt ,and create an authenticationinfo as the 
     * input of a credentialsMatcher. CredentialsMatcher's doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info);
     * will compare the token's credential with info's.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UseridPasswordToken upToken = (UseridPasswordToken) token;
        Integer userId = upToken.getId();
        String inputPassword = new String(upToken.getPassword());
        
        SimpleHashedCredentialsMatcher credentialsMatcher = (SimpleHashedCredentialsMatcher)getCredentialsMatcher();
        String hashedPass = credentialsMatcher.hashPassword(inputPassword);
        MoreSimpleByteSource salt = credentialsMatcher.getPasswordSalt();
     // this pojoAuthenticationInfo is the user authentication info we stored on server
     // now that we are using the input token's password hash value to create the pojoAuthenticationInfo, we will never fail in CredentialsMatcher's doCredentialsMatch
     // because this is exactly the hashed version of the token
        return new PojoAuthenticationInfo(userId,hashedPass,salt,getName());
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Set<String> roleNames = new HashSet<String>();
        roleNames.add("regularUser");
        SimpleAuthorizationInfo simpleAuthorizationInfo = 
                new SimpleAuthorizationInfo(roleNames);
        return simpleAuthorizationInfo;
    }
}
