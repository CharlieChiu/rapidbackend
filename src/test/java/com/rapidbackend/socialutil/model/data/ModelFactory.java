package com.rapidbackend.socialutil.model.data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;


import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.socialutil.install.dbinstall.ModelField;
import com.rapidbackend.socialutil.install.dbinstall.DaoGenerator;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.model.util.TypeFinder;
import com.rapidbackend.util.general.ReflectionTools;

/**
 * This class will create models for test purpose.
 * currently , only user, followable, and feed content needs automatical generation
 * @author chiqiu
 *
 */
public class ModelFactory extends AppContextAware{
    
    protected Set<Integer> integers = new TreeSet<Integer>();
    protected Set<Long> longs = new TreeSet<Long>();
    protected Set<Float> floats = new TreeSet<Float>();
    protected TypeFinder typeFinder;
    
    protected static String defaultPassword = "password";
    protected static String defaultUserClassName ="com.rapidbackend.socialutil.model.User";
    protected static String userTableName = "user";
    
    public ModelFactory() throws Exception{
        Class<?> clazz = Class.forName(DaoGenerator.getModelTypeFinderClass());
        typeFinder = (TypeFinder)(clazz.newInstance());
    }
    
    /**
     * we now only have followable and feed to generate
     * 
     */
    
    public UserBase createUser() throws Exception{
        Class<?> clazz = typeFinder.getModelClass(defaultUserClassName);
        Object user = clazz.newInstance();
        if(user instanceof UserBase){
            
            fillInTestValues(user);
            
        }else {
            throw new RuntimeException("user class is not sub class of StandardUser! user class name:"+ user.getClass());
        }
        //UserBase userBase = createUserBase();
        //BeanUtils.copyProperties(user, userBase);
        
        
        return (UserBase)user;
    }
    
    @Deprecated
    public Object createFeed(String feedClassName) throws Exception{
        Class<?> clazz = typeFinder.getModelClass(feedClassName);
        Object feed = clazz.newInstance();
        if(feed instanceof FeedContentBase){
            setCustomCollumnValue(feed,clazz,feedClassName);
        }else {
            throw new RuntimeException("feed class is not sub class of Feed! feed class name:"+ feed.getClass());
        }
        return feed;
    }

    @Deprecated
    public void setCustomCollumnValue(Object obj, Class<?> objClass,String className) throws Exception{
        List<ModelField> customCollums = userConfigCollums.get(className);
        for(ModelField collumn : customCollums){
            String collumnName = collumn.getName();
            Field field = ReflectionTools.getDeclaredField(objClass, collumnName);
            if(field == null){
                throw new RuntimeException("field: "+collumnName+" can't be found in class "+ objClass.getName());
            }
            Object value = null;
            if(collumn.isUnique()){
                
                if(field.getType() == String.class){
                    value = collumnName + ":"+genUniqueString();
                }
                if(field.getType() == Long.class){
                    value = genUniqueLong();
                }
                
                if(field.getType() == Integer.class){
                    value = genUniqueInteger();
                }
                
            }else {
                
                if(field.getType() == String.class){
                    value = collumnName + ":"+"test";
                }
                if(field.getType() == Long.class){
                    value = 10;
                }
                if(field.getType() == Integer.class){
                    value = 10;
                }
                
                
            }
            field.setAccessible(true);
            field.set(obj,value);
        }
    }
    
    public static String ExludedFieldName = "id";
    
    public void fillInTestValues(Object obj) throws Exception{
        Class<?> clazz = obj.getClass();
        Field[] fields = ModelReflectionUtil.getModelFields(clazz);
        for(Field field:fields){
            Object value = generateUniqueFieldValue(field,clazz);
            if(value!=null){
                field.setAccessible(true);
                field.set(obj,value);
            }
        }
    }
        
    public void eraseSpecifiedFileds(Object obj,List<String> fieldName) throws Exception{
        Class<?> clazz = obj.getClass();
        Field[] fields = ModelReflectionUtil.getModelFields(clazz);
        for(Field field:fields){
            for(String name:fieldName){
                if(field.getName().equalsIgnoreCase(name)){
                    field.setAccessible(true);
                    field.set(obj,null);
                }
            }
            
        }
    }
    
    public void eraseSpecifiedFileds(Object obj,String[] fieldName) throws Exception{
        Class<?> clazz = obj.getClass();
        Field[] fields = ModelReflectionUtil.getModelFields(clazz);
        for(Field field:fields){
            for(String name:fieldName){
                if(field.getName().equalsIgnoreCase(name)){
                    field.setAccessible(true);
                    field.set(obj,null);
                }
            }
            
        }
    }
    
    public Object generateUniqueFieldValue(Field field, Class<?> modelClass){
        Object value = null;
        if(!field.getName().equalsIgnoreCase(ExludedFieldName)){
            if(field.getType() == String.class){
                value = modelClass.getSimpleName()+"."+field.getName() + ":"+genUniqueString();
            }
            if(field.getType() == Long.class){
                value = genUniqueLong();
            }
            if(field.getType() == Integer.class){
                value = genUniqueInteger();
            }
            if(field.getType() == Boolean.class){
                value = new Boolean(false);
            }
            if(field.getType() == Float.class){
                value = genUniqueFloat();
            }
        }
        return value;
    }
    
    public UserBase createUserBase(){
        
        UserBase standardUser = new UserBase();
        String unique = genUniqueString();
        standardUser.setScreenName(unique);
                
        DbRecord.initTime(standardUser);
        
        return standardUser;
    }
    
    
    public FeedContentBase createFeedBase(){
        FeedContentBase feedBase = new FeedContentBase();
        return feedBase;
    }
    
    /**
     * create the model with randomized params
     * @param modelClass
     * @return
     * @throws Exception
     */
    public DbRecord createModel(Class<?> modelClass) throws Exception{
        DbRecord model = (DbRecord)modelClass.newInstance();
        fillInTestValues(model);
        DbRecord.initTime(model);
        return model;
    }
    
    public DbRecord createModel(String modeClasslName) throws Exception{
    	Class<?> modelClass = typeFinder.getModelClass(modeClasslName);
    	return createModel(modelClass);
    }
    
    
    protected HashMap<String, List<ModelField>> userConfigCollums = new HashMap<String, List<ModelField>>();
    
    
    public Long genUniqueLong(){
        while (true) {
            Long val = new Random().nextLong();
            if(longs.contains(val)){
                
            }else {
                longs.add(val);
                return val;
            }
        }
    }
    
    public Float genUniqueFloat(){
        while (true) {
            Float val = new Random().nextFloat();
            if(floats.contains(val)){
                
            }else {
                floats.add(val);
                return val;
            }
        }
    }
    
    public Integer genUniqueInteger(){
        while(true){
            Integer value = new Random().nextInt();
            if(integers.contains(value)){
                
            }else {
                integers.add(value);
                return value;
            }
        }
    }
    
    
    public String genUniqueString(){
        return UUID.randomUUID().toString();
    }
    
}
