package com.rapidbackend.core;


/**
 * Abstract class which contains common functions for a service configuration
 * @author chiqiu
 *
 */
public abstract class ServiceConf {
    protected boolean runAllowed = true;

    /**
     * @return the runAllowed
     */
    public boolean isRunAllowed() {
        return runAllowed;
    }

    /**
     * @param runAllowed the runAllowed to set
     */
    public void setRunAllowed(boolean runAllowed) {
        this.runAllowed = runAllowed;
    }
    
}
