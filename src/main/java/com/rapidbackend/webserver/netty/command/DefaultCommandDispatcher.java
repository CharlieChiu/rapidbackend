package com.rapidbackend.webserver.netty.command;

import org.apache.commons.lang.StringUtils;

import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.core.embedded.EmbeddedApi.RawCommand;

/**
 * 
 * @author chiqiu
 *
 */
public class DefaultCommandDispatcher implements CommandDispatcher{
    
    protected boolean inited = false;
    
    /*
    protected List<String> config;
    
    public List<String> getConfig() {
        return config;
    }
    @Required
    public void setConfig(List<String> config) {
        this.config = config;
        if(!inited){
            for(String s:config){
                String[] conf = s.split(",");
                if(conf==null || conf.length!=2){
                    throw new IllegalArgumentException("error api path config : "+s);
                }
                if (dispatcherMap.containsKey(conf[0])) {
                    throw new IllegalArgumentException("duplicate api path config : "+conf[0]);
                }else {
                    dispatcherMap.put(conf[0], conf[1]);
                }
            }
            inited = true;
        }
    }*/
    
        
    @Override
    public RawCommand dispatchCommand(String uri){
        String command =null;
        boolean supported = false;
        String commandPath = null;
        if(!StringUtils.isEmpty(uri)){
            int pathEnd = uri.indexOf('?');
            if(pathEnd<0){
                commandPath = uri;
            }else {
                commandPath = uri.substring(0, pathEnd);
            }
            if(commandPath.equalsIgnoreCase("/")){
                commandPath = "";
            }
            /*
            if(!StringUtils.isEmpty(path)){
                if(path.charAt(0)=='/'){
                    path = path.substring(1);
                }
            }*/
            String targetCommand = null;
            if(!StringUtils.isEmpty(commandPath)){
                targetCommand = Rapidbackend.getCore().getCommandMap().get(commandPath);// case sensitive
            }
            if (!StringUtils.isEmpty(targetCommand)) {
                supported = true;
                command = targetCommand;
            }
        }
        return new RawCommand(command, commandPath, supported);
    }    
}
