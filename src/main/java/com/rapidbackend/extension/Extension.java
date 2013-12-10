package com.rapidbackend.extension;


import java.util.HashMap;

import com.rapidbackend.core.ServiceContainer;

/**
 * A kind of service container that contains certain group of services which is inited by the 
 * rapidbackend.initExtensions function.
 * @author chiqiu
 *
 */
public abstract class Extension extends ServiceContainer{
    private ExtensionDescriptor extensionDescriptor;
    /**
     * init all services related to this extension
     */
    public abstract void initServices();
    
    /**
     * 
     * @return the request-> commmand mapping
     */
    public abstract HashMap<String, String> getCommandMapping();
    
    /**
     * 
     * @return ExtensionDescriptor for this extention
     */
    public ExtensionDescriptor getExtensionDescriptor(){
        return extensionDescriptor;
    }

    public void setExtensionDescriptor(ExtensionDescriptor extensionDescriptor) {
        this.extensionDescriptor = extensionDescriptor;
    }
    
}
