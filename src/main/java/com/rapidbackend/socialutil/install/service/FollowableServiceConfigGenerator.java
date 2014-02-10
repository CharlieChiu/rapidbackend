package com.rapidbackend.socialutil.install.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.install.dbinstall.FollowableConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class FollowableServiceConfigGenerator {
    protected static final String templateFoder = "src/main/resources/dbInstall/model/templates/services";
    protected static final String tempalteFile = "followableServices.xml";
    protected static final String serviceCmdMappingTemplateFile = "serviceCommandMap.js";
    //protected static String outPutFile = "src/main/resources/config/groupService.xml";
    protected static final String outputDir = "src/main/resources/config/";
    protected static final String serviceCmdMappingOutputFile = "src/main/resources/config/serviceCommandMap.js";
    //protected static String followable = "Group";
    protected Configuration configuration =  new Configuration();
    
    public FollowableServiceConfigGenerator() throws Exception{
        configuration.setDirectoryForTemplateLoading(new File(templateFoder));
    }
    
    public String createService(String followableName) throws Exception{
        Template template = configuration.getTemplate(tempalteFile);
        String outputFile = getOutPutConfigFileName(followableName);
        BufferedWriter output = new BufferedWriter(new FileWriter(new File(outputFile)));
        HashMap<String, String> variables = new HashMap<String, String>();
        variables.put("followableName",followableName);
        template.process(variables, output);
        
        /*
         * create service command mappings
         */
        //TODO when fork followable is enabled, we should add new content into the template
        
        Template commandMappingTemplate = configuration.getTemplate(serviceCmdMappingTemplateFile);
        BufferedWriter serviceCmdMappingWriter = new BufferedWriter(new FileWriter(new File(serviceCmdMappingOutputFile)));
        commandMappingTemplate.process(variables, serviceCmdMappingWriter);
        return getOutPutConfigFileName(followableName);
        
    }
    
    public List<String> createServices() throws Exception{
        List<String> configfiles = new ArrayList<String>();
        DbConfigParser dbConfigParser = new DbConfigParser();
        List<FollowableConfig> followableConfigs = (List<FollowableConfig>)dbConfigParser.parseSetting(null).get(DbConfigParser.FollowableConfigVariable);
        for(FollowableConfig followableConfig:followableConfigs){
            configfiles.add(createService(StringUtils.capitalize(followableConfig.getName())));
        }
        return configfiles;
    }
    
    private String getOutPutConfigFileName(String followableName){
        return outputDir + followableName+"Service.xml";
    }
    
    public static void main(String[] args) throws Exception{
        FollowableServiceConfigGenerator serviceConfigGenerator = new FollowableServiceConfigGenerator();
    }
}
