package com.rapidbackend.socialutil.security;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.socialutil.dao.UserDao;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.user.UserUtils;
import com.rapidbackend.security.shiro.CommandSpecificRealm;
import com.rapidbackend.security.shiro.CommandSpecificToken;
import com.rapidbackend.security.shiro.MoreSimpleByteSource;
import com.rapidbackend.security.shiro.PasswordSaltProvider;
import com.rapidbackend.security.shiro.PojoAuthenticationInfo;

public class UserLoginRealm extends CommandSpecificRealm{
    protected UserDao userDao;
    
    public UserDao getUserDao() {
        return userDao;
    }
    @Required
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        ScreenNamePasswordToken upToken = (ScreenNamePasswordToken) token;
        Integer userId ;
        try {
            UserBase user = (UserBase)userDao.loadByColumn("screenName", upToken.getScreenName(), UserUtils.getUserClass());
            userId = user.getId();
            if(user!=null){
                MoreSimpleByteSource salt = ((PasswordSaltProvider)getCredentialsMatcher()).getPasswordSalt(user);
                String password = user.getPassword();
                return new PojoAuthenticationInfo(user,password.toCharArray(),salt,getName());
            }else {
                throw new AuthenticationException("user not found: "+upToken.getUsername());
            }
        } catch (Exception e) {
            throw new AuthenticationException(e);
        }
        
    }
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Set<String> roleNames = new HashSet<String>();
        roleNames.add("regularUser");
        SimpleAuthorizationInfo simpleAuthorizationInfo = 
                new SimpleAuthorizationInfo(roleNames);
        return simpleAuthorizationInfo;
    }
    
    public static class UseridPasswordToken extends CommandSpecificToken{
        /**
         * 
         */
        private static final long serialVersionUID = 6925777060069761552L;
        protected Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
        /**
         * @param id
         * @param password
         * @param command supported command
         */
        public UseridPasswordToken(Integer id,String password,String command){
            super(id.toString(),command);
            this.id = id;
            setPassword(password.toCharArray());
        }
    }
    
    public static class ScreenNamePasswordToken extends CommandSpecificToken{
        /**
         * 
         */
        private static final long serialVersionUID = -7939053393870750078L;
        
        
        public ScreenNamePasswordToken(String screenName, String password, String command){
            super(screenName,command);
            setPassword(password.toCharArray());
        }
        
        public String getScreenName(){
            return getPrincipal().toString();
        }
    }
    
}
