package com.rapidbackend.security.shiro;
/**
 * 
 * @author chiqiu
 *
 */
public interface PasswordSaltProvider {
    /**
     * @return password salt
     */
    MoreSimpleByteSource getPasswordSalt();
    /**
     * 
     * @param input
     * @return password salt
     */
    MoreSimpleByteSource getPasswordSalt(Object input);
}
