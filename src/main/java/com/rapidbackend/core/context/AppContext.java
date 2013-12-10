package com.rapidbackend.core.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.rapidbackend.core.SpringContextAware;

/**
 * Root context of current app. One and only one Appcontext is initialized everytime the App starts
 * @author chiqiu
 *
 */
public class AppContext extends SpringContextAware{
    static Logger logger = LoggerFactory.getLogger(AppContext.class);
    static String[] defaultApplicationContextConfigFiles = {
    
"src/main/resources/config/daoConfig.xml",
"src/main/resources/config/services.xml",
"src/main/resources/config/jmxConfig.xml",
"src/main/resources/config/redisConf.xml",
//"src/main/resources/config/kestrelConf.xml",
"src/main/resources/config/cacheConf.xml",
"src/main/resources/config/pipelineConf.xml",
"src/main/resources/config/searchConf.xml",
"src/main/resources/config/requestSchema.xml",
"src/main/resources/config/httpComponentConf.xml",
"src/main/resources/config/securityConf.xml"
};    
    static boolean ContextInited = false;
    

    public static boolean isContextInited() {
        return ContextInited;
    }

    public static void setContextInited(boolean contextInited) {
        ContextInited = contextInited;
    }

    
    
    public static void init(String[] xmlFiles) {
        if(ContextInited){
            logger.warn("appcontext has been initialized, this init call will do nothing", new RuntimeException());
            return;
        }
        try {
            applicationContext = new FileSystemXmlApplicationContext(xmlFiles);
        } catch (Exception e) {
            throw new RuntimeException("error during init app context :",e);
        }
        applicationContext.registerShutdownHook();
        ContextInited = true;
    }
    
    public static String[] getApplicationContextConfigFiles() throws IOException{
        String defaultConfigFilePath = "springContexts.ini";
        String defaultConfigFilePath2 = "src/main/resources/springContexts.ini";
        String configFilePath = System.getProperty("rapidbackend.springContexts");
        File configFile = null;
        if(StringUtils.isEmpty(configFilePath)){
            configFile = new File(defaultConfigFilePath);
            if (!configFile.exists()) {
                configFile = new File(defaultConfigFilePath2);
            }
        }else {
            configFile = new File(configFilePath);
        }

        List<String> lines = new ArrayList<String>();
        if(configFile.exists()){
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line = reader.readLine();
            while (!StringUtils.isEmpty(line)) {
                lines.add(line.trim());
                line = reader.readLine();
            }
            reader.close();
        }
        
        if(lines.size() >0){
            return lines.toArray(new String[0]);
        }else {
            return defaultApplicationContextConfigFiles;
        }
    }
    
}
