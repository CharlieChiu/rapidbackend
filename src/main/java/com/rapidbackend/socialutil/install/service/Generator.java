package com.rapidbackend.socialutil.install.service;

import java.io.File;
import java.io.IOException;

import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.model.util.TypeFinder;

import freemarker.template.Configuration;

public abstract class Generator {
    
    private TypeFinder typeFinder;
    
    protected Configuration configuration =  new Configuration();
    
    protected DbConfigParser dbConfigParser;
    
    public TypeFinder getTypeFinder(){
        
        try {
            if(typeFinder==null){
                typeFinder = TypeFinder.getDefaultTypeFinderInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("error creating typeFinder",e);
        }
        return typeFinder;
    }
    
    protected void initFreemarkerTemplateFolder(String templateFolder) throws IOException {
        configuration.setDirectoryForTemplateLoading( new File(templateFolder));
    }
    
    protected void initDbConfigParser() throws IOException{
        dbConfigParser = new DbConfigParser();
    }
    
    protected void init(String templateFolder) throws IOException{
        initFreemarkerTemplateFolder(templateFolder);
        initDbConfigParser();
    }
    
}
