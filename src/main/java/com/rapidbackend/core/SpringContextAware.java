package com.rapidbackend.core;

import org.springframework.context.support.AbstractApplicationContext;

/**
 * Base class to handle Spring Context, All service classes which needs to be configured by Spring should inherit it
 * @author chiqiu
 */
public abstract class SpringContextAware {
    protected static AbstractApplicationContext applicationContext ;
    
    public SpringContextAware(AbstractApplicationContext context){
        setApplicationContext(context);
    }

    /**
     * @return the applicationContext
     */
    public static AbstractApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * @param applicationContext the applicationContext to set
     */
    public static void setApplicationContext(AbstractApplicationContext applicationContext) {
        SpringContextAware.applicationContext = applicationContext;
    }
    
    public SpringContextAware() {
        
    }
    
}
