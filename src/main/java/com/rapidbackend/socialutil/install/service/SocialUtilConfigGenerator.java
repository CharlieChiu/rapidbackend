package com.rapidbackend.socialutil.install.service;

import java.util.List;

import com.rapidbackend.extension.ExtensionDescriptor;
import com.rapidbackend.extension.ExtensionRepository;
import com.rapidbackend.socialutil.install.extension.CreateSocialUtilExtensionDescriptor;

public class SocialUtilConfigGenerator {
    
    public void genSocialUtilConfiguration() throws Exception{
        FollowableServiceConfigGenerator followableServiceConfigGenerator = new FollowableServiceConfigGenerator();
        List<String> followableServiceConfigs = followableServiceConfigGenerator.createServices();
        
        ExtensionRepository repository = new ExtensionRepository();
        ExtensionDescriptor extensionDescriptor = new CreateSocialUtilExtensionDescriptor(followableServiceConfigs).getExtensionDescriptor();
        
        repository.addExtensionDescriptor(extensionDescriptor);
        repository.save();
    }
}
