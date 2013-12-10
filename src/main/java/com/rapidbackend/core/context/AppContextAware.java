package com.rapidbackend.core.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;


/**
 * Abstract class which provides the getApplicationContext() method.
 * This method enables subclasses to access the app context. <br>
 * Most of socialutil's service classes are its subclasses.
 * @author chiqiu
 *
 */
public abstract class AppContextAware {
	Logger logger = LoggerFactory.getLogger(AppContextAware.class);
	public static AbstractApplicationContext getApplicationContext(){
        return AppContext.getApplicationContext();
    }
	
}
