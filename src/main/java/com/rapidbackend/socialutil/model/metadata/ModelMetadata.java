package com.rapidbackend.socialutil.model.metadata;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.springframework.util.ReflectionUtils;

import org.apache.commons.lang.StringUtils;

/**
 * metadata is used for describe one model's counter properties.
 * for example the retweet number, comments number,paging numbers
 * @author chiqiu
 */
public abstract class ModelMetadata {
    protected Integer id;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    public abstract HashMap<String, Method> getMethodCache() ;
    
    public Integer getIntValue(String fieldName) throws InvocationTargetException,IllegalAccessException{
        String getterName = getFieldGetterName(fieldName);
        Method m = getMethodCache().get(getterName);// be careful, case sensitive
        if(m==null){
            m = ReflectionUtils.findMethod(getClass(), getterName);
            if(m==null){
                Throwable e = new NullPointerException("can't find method "+getFieldGetterName(fieldName)+"for field "+fieldName);
                throw new InvocationTargetException(e);
            }else {
                getMethodCache().put(fieldName, m);
            }
        }
        return (Integer)m.invoke(this);
    }
    
    public void setIntValue(String fieldName, Integer val) throws InvocationTargetException,IllegalAccessException{
        String setterName = getFieldSetterName(fieldName);
        Method m = getMethodCache().get(setterName);// be careful, case sensitive
        if(m==null){
            m = ReflectionUtils.findMethod(getClass(), setterName,Integer.class);
            if(m==null){
                Throwable e = new NullPointerException("can't find method "+getFieldGetterName(fieldName)+"for field "+fieldName);
                throw new InvocationTargetException(e);
            }else {
                getMethodCache().put(setterName, m);
            }
        }
        m.invoke(this,val);
    }
    /**
     * follow the getter rule of captalizing the first character of the fieldname string
     * @param fieldName
     * @return
     */
    public static String getFieldGetterName(String fieldName){
        return "get"+StringUtils.capitalize(fieldName);
    }
    public static String getFieldSetterName(String fieldName){
        return "set"+StringUtils.capitalize(fieldName);
    }
    
    
}
