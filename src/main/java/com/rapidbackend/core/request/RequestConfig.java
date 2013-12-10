package com.rapidbackend.core.request;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.rapidbackend.core.context.AppContextAware;


public class RequestConfig extends AppContextAware{
	public static HashMap<String, RequestSchema> schemaContainer = 
		new HashMap<String, RequestSchema>();
	@Deprecated
	public static String configFile = "src/main/resources/config/requestSchema.xml";
	
	public static void init(){
	    init(true,new String[0]);
	}
	public static boolean locked = false;
	
	/**
	 * @param locked // mark the class as locked to prevent another init
	 * @param configFilePath
	 */
	public static synchronized void init(boolean locked, String... configFilePath){
	    AbstractApplicationContext context = null;
		if(null==configFilePath || configFilePath.length==0){
		    context = getApplicationContext();// use applicationContext to init the request schemas
		}else {
		    context = new FileSystemXmlApplicationContext(configFilePath);
        }
		
		String[] beanNames = context.getBeanDefinitionNames();
		for(String beanName : beanNames){
			if(!StringUtils.isEmpty(beanName)){
				Object bean = context.getBean(beanName);
				if(bean instanceof RequestSchema){
					RequestSchema schema = (RequestSchema)bean;
					schema.initParams();
					String command = schema.getCommand();
					if(schemaContainer.containsKey(command)){
					    throw new IllegalStateException("error initializing bean "+ beanName +". Duplicate command schema! command schema for '"+command+"' has been inited. Please check your schema config file.");
					}
					schemaContainer.put(command, schema);
					//TODO add init statuses to init.log
				}
			}
		}
		RequestConfig.locked = locked;
	}
	
	

    public static RequestSchema getRequestSchema(String schemaName){
		return schemaContainer.get(schemaName);
	}
	public static void clear(){
	    schemaContainer.clear();
	}
}
