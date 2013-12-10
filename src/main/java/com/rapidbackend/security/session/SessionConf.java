package com.rapidbackend.security.session;
/**
 * configs if the session is set or not
 * @author chiqiu
 *
 */
public class SessionConf {
    protected boolean sessionEnabled = true;
    protected int sessionExpireSeconds = 3600*24; //one day as default value
    
    public boolean isSessionEnabled() {
        return sessionEnabled;
    }
    public void setSessionEnabled(boolean sessionEnabled) {
        this.sessionEnabled = sessionEnabled;
    }
    public int getSessionExpireSeconds() {
        return sessionExpireSeconds;
    }
    public void setSessionExpireSeconds(int sessionExpireSeconds) {
        this.sessionExpireSeconds = sessionExpireSeconds;
    }
    
}
