package com.rapidbackend.socialutil.user;

import com.rapidbackend.core.BackendRuntimeException;

public class UserUtils {
    
    private static Class<?> userClass;
    public static String userClassName = "com.rapidbackend.socialutil.model.User";
    
    public static Class<?> getUserClass(){
        try {
            if(userClass==null){
                userClass = Class.forName(userClassName);
            }
            return userClass;
        } catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.InternalServerError,"can't find userclass "+userClassName);
        }
    }
}
