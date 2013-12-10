package com.rapidbackend.security.shiro.realms;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.security.shiro.SimpleHashedCredentialsMatcher;

public class PasswordEncrypter extends AppContextAware{
    protected SimpleHashedCredentialsMatcher encrypter;
    

    public SimpleHashedCredentialsMatcher getEncrypter() {
        return encrypter;
    }

    @Required
    public void setEncrypter(SimpleHashedCredentialsMatcher encrypter) {
        this.encrypter = encrypter;
    }


    public String encrypt(String password){
        return encrypter.hashPassword(password);
    }
}
