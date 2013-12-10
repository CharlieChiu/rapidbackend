package com.rapidbackend.extension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.util.io.JsonUtil;
/**
 * 
 * @author chiqiu
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class ExtensionRepository {
    
    public static String extensionConfig = "src/main/resources/config/ExtensionRepository.js";
    
    private List<ExtensionDescriptor> extensionDescriptors = new ArrayList<ExtensionDescriptor>();
    
    private HashMap<String, Extension> extensionMap = new HashMap<String, Extension>();
    
    public void addExtension(Extension extension){
        String extentionName = extension.getExtensionDescriptor().getExtentionName();
        if(StringUtils.isEmpty(extentionName)){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"extension name cann't be empty");
        }
        if(extensionMap.containsKey(extentionName)){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"extension name "+extentionName + " already exists");
        }
        extensionMap.put(extentionName, extension);
    }
        
    public HashMap<String, Extension> getExtensionMap() {
        return extensionMap;
    }

    public void setExtensionMap(HashMap<String, Extension> extensionMap) {
        this.extensionMap = extensionMap;
    }

    @JsonIgnore
    public Collection<Extension> getExtensions() {
        return extensionMap.values();
    }

    public List<ExtensionDescriptor> getExtensionDescriptors() {
        return extensionDescriptors;
    }

    public void setExtensionDescriptors(
            List<ExtensionDescriptor> extensionDescriptors) {
        this.extensionDescriptors = extensionDescriptors;
    }
    
    public void addExtensionDescriptor(ExtensionDescriptor descriptor){
        this.extensionDescriptors.add(descriptor);
    }
    
    public Extension getExtension(String extensionName){
        return extensionMap.get(extensionName);
    }
    
    public void save() throws IOException{
        JsonUtil.writeObjectPretty(this, new File(extensionConfig));
    }
    
    public void save(String fileName) throws IOException{
        JsonUtil.writeObjectPretty(this, new File(fileName));
    }
    
}
