package com.rapidbackend.core;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.context.AppContextAware;

public abstract class ServiceContainer extends AppContextAware{
    protected Logger logger = LoggerFactory.getLogger(ServiceContainer.class);
    
    protected boolean inited = false;
    protected List<ClusterableService> services = new ArrayList<ClusterableService>();
    
    public boolean isInited() {
        return inited;
    }
    
    public void setInited(boolean inited) {
        this.inited = inited;
    }
    
    public void initClusterableService(ClusterableService service,String name) throws Exception{
        if(service.readyForInit()){
            System.out.println("init "+name+" service");
            service.tryToStart();
            service.setRunning(true);
            if(!services.contains(service)){
                services.add(service);
            }
        }
    }
    
    public void forceInitClusterableService(ClusterableService service,String serviceName) throws Exception{
        if(!service.isRunning()){
            System.out.println("init "+serviceName+" service");
            service.setRunAllowed(true);
            service.tryToStart();
            service.setRunning(true);
            if(!services.contains(service)){
                services.add(service);
            }
        }
    }
    
    public void destroy(){
        for(ClusterableService service : services){
            if(service !=null && service.isRunning()){
                try {
                    service.doStop();
                } catch (Exception e) {
                    logger.error("error stopping service: "+service.toString(),e);
                }
                
            }
        }
    }
}
