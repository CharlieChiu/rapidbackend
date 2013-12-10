package com.rapidbackend.socialutil.install.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.type.TypeReference;

import com.rapidbackend.socialutil.install.service.Generator;
import com.rapidbackend.util.io.JsonUtil;

import freemarker.template.Template;
import freemarker.template.TemplateException;


public abstract class JavaModelGenerator extends Generator{
    
    public static String javaTypeMappingFile = "src/main/resources/install/web/js/javaTypeMapping.js";
    
    public static String modelPackage = "com.rapidbackend.socialutil.model";
    
    public static String javaModelTemplateFolder = "src/main/resources/dbInstall/model/templates/javamodel" ;
    
    public static String javaclassFolder = "src/main/java/com/rapidbackend/socialutil/model/";
    
    
    public static String templateFile = "javaBean.ftl";
    
    private static HashMap<String, String> javaTypeMapper;
    
    private Template template;
    
    protected final ClassDescriptor classDescriptor;
    
    /**
     * map field type to basic type classes in package java.lang 
     * @return
     */
    public static HashMap<String, String> getJavaTypeMapper(){
        
        try {
            HashMap<String, String> mapper = JsonUtil.readObject(new File(javaTypeMappingFile), new TypeReference<HashMap<String, String>>() {
            });
            javaTypeMapper = mapper;
        } catch (IOException e) {
            throw new RuntimeException("error parsing java type mapping",e);
        }
        return javaTypeMapper;
    }
    
    public JavaModelGenerator(ClassDescriptor classDescriptor){
        try {
            init(javaModelTemplateFolder);
            template = configuration.getTemplate(templateFile);
            
            this.classDescriptor = classDescriptor;
            
            this.classDescriptor.setRootClass(getRootClass());
            
        } catch (IOException e) {
            throw new RuntimeException("error init modelgenerator", e);
        }
        
    }
    
    public abstract String getRootClass();
    
    public static String userBaseClass = "com.rapidbackend.socialutil.model.reserved.UserBase";
    
    
    
    public static String followableBaseClass = "com.rapidbackend.socialutil.model.reserved.FollowableBase";
    public static String followableFeedBaseClass = "com.rapidbackend.socialutil.model.reserved.FollowabeFeedBase";
    public static String subscriptionBaseClass = "com.rapidbackend.socialutil.model.reserved.Subscription";
    public static String feedContentBaseClass = "com.rapidbackend.socialutil.model.reserved.FeedContentBase";
    public static String feedCommentBaseClass = "com.rapidbackend.socialutil.model.reserved.CommentBase";
    public static String reservedClassPackage = "com.rapidbackend.socialutil.model.reserved";
    
    public static String userDefinedModelBaseClass = "com.rapidbackend.core.model.DbRecord";
    public static String userDefinedModelClassPackageString = "com.rapidbackend.socialutil.model.userdefined";
    
    
    public static JavaModelGenerator createGenerator(ClassDescriptor classDescriptor){
        
        ModelType type = classDescriptor.getModelType();
        
        switch (type) {
        case user:
            return new JavaModelGenerator(classDescriptor){
                @Override
                public String getRootClass(){
                    return userBaseClass;
                }
            };
        case feedContent:
            return new JavaModelGenerator(classDescriptor) {
                
                @Override
                public String getRootClass() {
                    return feedContentBaseClass;
                }
            };
        case followable:
            return new JavaModelGenerator(classDescriptor) {
                
                @Override
                public String getRootClass() {
                    return followableBaseClass;
                }
            };
        case comment:
            return new JavaModelGenerator(classDescriptor) {
                
                @Override
                public String getRootClass() {
                    return feedCommentBaseClass;
                }
            };
        case subscription:
            return new JavaModelGenerator(classDescriptor) {
                
                @Override
                public String getRootClass() {
                    return subscriptionBaseClass;
                }
            };
        case followableFeed:
            return new JavaModelGenerator(classDescriptor) {
                
                @Override
                public String getRootClass() {
                    return followableFeedBaseClass;
                }
            };
        case reservedModel:
            return new JavaModelGenerator(classDescriptor) {
                
                @Override
                public String getRootClass() {
                    return reservedClassPackage + "."+ this.classDescriptor.getName();
                }
            };
        case userDefinedModel:
            return new JavaModelGenerator(classDescriptor) {
                
                @Override
                public String getRootClass() {
                    return JavaModelGenerator.userDefinedModelBaseClass;
                }
            };
        default:
            throw new UnsupportedOperationException("The current modeltype is not supported:"+type);
        }
        
    }
    
    public void genModelClass() throws IOException,TemplateException{
        String classFile = javaclassFolder+classDescriptor.getName()+".java";
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(classFile)));
        HashMap<String, Object> variable = new HashMap<String, Object>();
        variable.put("classDescriptor", classDescriptor);
        template.process(variable, writer);
        writer.flush();
        writer.close();
    }
    
}
