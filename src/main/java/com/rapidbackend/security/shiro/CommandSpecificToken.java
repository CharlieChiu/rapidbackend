package com.rapidbackend.security.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;
/**
 * TODO support multiple commands???? do we need to ??
 * @author chiqiu
 *
 */
public abstract class CommandSpecificToken extends UsernamePasswordToken{
    /**
     * 
     */
    private static final long serialVersionUID = 6129639415104547979L;
    protected String command;
    
    public Object principal;
    @Override
    public Object getPrincipal() {
        return principal;
    }

    protected CommandSpecificToken(String principal,String command){
        this.principal = principal;
        this.command = command;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
        
}
