package com.rapidbackend.socialutil.model.util;

import com.rapidbackend.socialutil.model.*;

/**
 * Helper class to find model class type by String
 * TODO use class loader to load the model classes, not in such stupid manner
 * @author chiqiu
 *@genereated by DaoGenerator
 */
public class ModelTypeFinder extends TypeFinder {

    protected Class<?>[] knownModelClasses = {
        <#list classNames as className>
        ${className}.class,
        </#list>
    };
    
    public Class<?>[] getKnownModelClasses() {
        return knownModelClasses;
    }

    public void setKnownModelClasses(Class<?>[] knownModelClasses) {
        this.knownModelClasses = knownModelClasses;
    }
    
    /**
     * Find class by className string.
     * Override this method if you have different rules.
     * It is now called in handlers and request schema.
     * We can reconfig it in spring xmls.
     * @param className full class name of this class
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Class getModelClass(String className){
        Class clazz = null;
        <#list classNames as className>
        if (className.equalsIgnoreCase("${className}")) {
            clazz = ${className}.class;
        }
        </#list>
        <#list simpleclassNames as simpleclassName>
        if (className.equalsIgnoreCase("${simpleclassName}")) {
            clazz = ${modelPackage}.${simpleclassName}.class;
        }
        </#list>
        if(null == clazz)
            throw new UnsupportedOperationException("unsupported model class, name:"+className);
        return clazz;
    }
}