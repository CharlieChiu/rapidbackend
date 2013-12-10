package com.rapidbackend.core.model.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;


import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.util.general.ReflectionTools;


public class ModelReflectionUtil {
    
    protected static HashMap<String, Field> modelFieldsCache = new HashMap<String, Field>();
    protected static HashMap<Class<?>, Field[]> modleFieldListCache = new HashMap<Class<?>, Field[]>();
    protected static HashMap<String, Method> modelMethodCache = new HashMap<String, Method>();
    protected static HashMap<String, Class<?>> modelClassCache = new HashMap<String, Class<?>>();
    
    public static String DefaultUserClassName = "com.rapidbackend.socialutil.model.User";
    
    
    public static String IS_PREFIX = "is";
    public static String GET_PREFIX = "get";
    public static String SET_PREFIX = "set";
    
    
    public static Field getModelField(String fieldName , Class<?> modelClazz){
        String fieldKeyInCache = fieldKeyInFieldCache(fieldName,modelClazz);
        
        Field res = null;
        if(modelFieldsCache.containsKey(fieldKeyInCache)){
            res = modelFieldsCache.get(fieldKeyInCache);
        }else {
            res = ReflectionTools.findField(modelClazz, fieldName);
            modelFieldsCache.put(fieldKeyInCache, res);
        }
        if(res== null){
            throw new RuntimeException("field '"+fieldName+"' can't be found in model "+modelClazz);
        }
        return res;
    }
    
    public static boolean containsField(String fieldName,Class<?> fieldType, Class<?> modelClazz){
        String fieldKeyInCache = fieldKeyInFieldCache(fieldName,modelClazz);
        Field res = null;
        if(modelFieldsCache.containsKey(fieldKeyInCache)){
            res = modelFieldsCache.get(fieldKeyInCache);
        }else {
            res = ReflectionTools.findField(modelClazz, fieldName);
            modelFieldsCache.put(fieldKeyInCache, res);
        }
        if(res == null){
            return false;
        }else {
            return res.getType() == fieldType;
        }
    }
    
    public static String fieldKeyInFieldCache(String fieldName,Class<?> modelClazz){
        return modelClazz.getName()+"."+fieldName;
    }
    
    public static String methodKeyInMethodCache(String methodName,Class<?> modelClazz){
        return modelClazz.getName()+"."+methodName;
    }
    
    public static Field[] getModelFields(Class<?> modelClass){
        Field[] result = modleFieldListCache.get(modelClass);
        if(result==null){
            Field[] fields = ReflectionTools.getAllFields(modelClass,Object.class);
            modleFieldListCache.put(modelClass, fields);
        }
        return modleFieldListCache.get(modelClass);
    }
    
    public static Method getSetterMethod(Class<?> modelClass,String fieldName){
        String setMethodName = getFieldSetterName(fieldName);
        String methodKeyName = methodKeyInMethodCache(setMethodName,modelClass);
        Method method = modelMethodCache.get(methodKeyName);
        if(method==null){
            Field field = getModelField(fieldName, modelClass);
            Method setter = ReflectionTools.findMethod(modelClass, setMethodName, field.getType());
            if(setter!=null){
                modelMethodCache.put(methodKeyName, setter);
            }else {
                throw new RuntimeException("method '"+setMethodName+"' can't be found in model "+ modelClass);
            }
            return setter;
        }else {
            return method;
        }
    }
    /**
     * set a field by the field's settter method. Note the setter method should
     * follow the setter rule of capitalizing the first character of the fieldname string.
     * @param clazz
     * @param fieldName
     * @param value
     * @param target
     */
    public static void setPropertyValue(Class<?> clazz,String fieldName,Object value,Object target){
        Method setter = getSetterMethod(clazz, fieldName);
        try {
            if(setter==null){
                throw new RuntimeException("Field '"+fieldName+"' is not accessible for class "+clazz);
            }
            setter.invoke(target, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Field '"+fieldName+"' is not accessible for class "+clazz, ex);
        }catch (IllegalArgumentException ex) {
            throw new RuntimeException("Field '"+fieldName+"' 's type mismatches value "+value, ex);
        }catch (InvocationTargetException ex) {
            throw new RuntimeException("Error in setting Field '"+fieldName+"' with value "+value, ex);
        }
        /*Field field = getModelField(fieldName, clazz);
        try{
            field.set(target, value);
        }catch (IllegalAccessException ex) {
            throw new RuntimeException("Field '"+fieldName+"' is not accessible for class "+clazz, ex);
        }
        catch (IllegalArgumentException ex) {
            throw new RuntimeException("Field '"+fieldName+"' 's type mismatches value "+value, ex);
        }*/
    }
    
    
    public static Object getPropertyValue(Class<?> modelClazz,String fieldName,Object target){
        Method getter = getGetterMethod(modelClazz,fieldName);
        try {
            if(getter==null){
                throw new RuntimeException("Field '"+fieldName+"' is not accessible for class "+modelClazz);
            }
            return getter.invoke(target);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Field '"+fieldName+"' is not accessible for class "+modelClazz, ex);
        }catch (IllegalArgumentException ex) {
            throw new RuntimeException("Field '"+fieldName+"' 's type mismatches value "+modelClazz, ex);
        }catch (InvocationTargetException ex) {
            throw new RuntimeException("Error in getting Field '"+fieldName, ex);
        }
    }
    
    public static Method getGetterMethod(Class<?> modelClazz, String fieldName){
        
        Field field =  getModelField(fieldName, modelClazz);
        if(field == null){
            throw new RuntimeException("field "+ fieldName+" can't be found in class"+modelClazz);
        }
        String getMethodName;
        if(field.getType()==boolean.class){//Boolean property still uses "get" as getter prefixes
            getMethodName = IS_PREFIX+StringUtils.capitalize(fieldName);
        }else {
            getMethodName = GET_PREFIX+StringUtils.capitalize(fieldName);
        }
        String methodKeyName = methodKeyInMethodCache(getMethodName,modelClazz);
        Method method = modelMethodCache.get(methodKeyName);
        if(method ==null){
            method = ReflectionTools.findMethod(modelClazz, getMethodName);// getter method contains no parameters
            if(method!=null){
                modelMethodCache.put(methodKeyName, method);
            }
        }
        return method;
    }
   
    /**
     * follow the setter rule of capitalizing the first character of the fieldname string
     * @param fieldName
     * @return
     */
    public static String getFieldSetterName(String fieldName){
        return SET_PREFIX+StringUtils.capitalize(fieldName);
    }
    
    public static Class<?> getUserClass(){
        return getModelClass(DefaultUserClassName);
    }
        
    public static Class<?> getModelClass(String className){
        try {
            Class<?> c = modelClassCache.get(className);
            if(null == c){
                c = Class.forName(className);
                modelClassCache.put(className, c);
            }
            return c;
        }catch (ClassNotFoundException e) {
            throw new BackendRuntimeException(BackendRuntimeException.InternalServerError,"class " + className + " not found");
        }
    }
}
