package com.rapidbackend.socialutil.install.extension;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.rapidbackend.extension.ExtensionDescriptor;

public class CreateSocialUtilExtensionDescriptor {
    
    protected ExtensionDescriptor extensionDescriptor;
    
    public ExtensionDescriptor getExtensionDescriptor() {
        return extensionDescriptor;
    }

    public void setExtensionDescriptor(ExtensionDescriptor extensionDescriptor) {
        this.extensionDescriptor = extensionDescriptor;
    }

    public CreateSocialUtilExtensionDescriptor(List<String> followableServiceConfigFiles){
        extensionDescriptor = new ExtensionDescriptor();
        
        Set<String > springContextFiles = new TreeSet<String>();
        //Set<String> requestContextFiles = new TreeSet<String>();
        
        extensionDescriptor.setSpringContextFiles(springContextFiles);
        extensionDescriptor.setExtentionName("SocialUtility");
        extensionDescriptor.setExtentionBean("SocialUtility");
        
        String[] sContextFiles = {
                "src/main/resources/config/socialUtilConfig.xml",
                "src/main/resources/config/requestParams.xml", 
                "src/main/resources/config/reservedParams.xml", 
                "src/main/resources/config/socialUtilCommonRequestInterceptors.xml", 
                "src/main/resources/config/socialUtilCrudPipelineConf.xml",
                "src/main/resources/config/socialUtilDaoConfig.xml", 
                "src/main/resources/config/socialUtilSecurityConf.xml",
                "src/main/resources/config/socialUtilRedisConf.xml"
        };
        
        for(String c: sContextFiles){
            springContextFiles.add(c);
        }
        
        for(String c:followableServiceConfigFiles){
            springContextFiles.add(c);
        }
        
        /*
        String[] rContextFiles ={
                "src/main/resources/config/socialUtilCrudRequestSchema.xml",
                "src/main/resources/config/requestParams.xml"
        };
        for(String c:rContextFiles){
            requestContextFiles.add(c);
        }
        
        //extensionDescriptor.setRequestContextFiles(requestContextFiles);*/
        extensionDescriptor.setSpringContextFiles(springContextFiles);
    }
    
    public static void main(String[] args) throws Exception{
        /*
        ExtensionRepository repository = new ExtensionRepository();
        ExtensionDescriptor extensionDescriptor = new CreateSocialUtilExtensionDescriptor().getExtensionDescriptor();
        
        repository.addExtensionDescriptor(extensionDescriptor);
        repository.save();*/
    }
    
}
