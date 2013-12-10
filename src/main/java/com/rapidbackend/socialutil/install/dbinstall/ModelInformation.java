package com.rapidbackend.socialutil.install.dbinstall;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.socialutil.model.util.TypeFinder;

public class ModelInformation {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    protected DbConfigParser parser ;
    
    protected static HashMap<Class<?>, List<Field>> requiredFieldsCache =null;
    
    protected static List<Field> Empty = new ArrayList<Field>();
    
    public ModelInformation() throws IOException{
        parser = new DbConfigParser();
    }
    
    /**
     * Note:this method should be called after installation and compile, otherwise the class ModelTypeFinder might be missing or wrong
     *  TODO now it only supports models in followables, should be changed if we supports more models
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private void parseAllUserCustomedRequiredFields() throws Exception{
        HashMap<String, Object> globalTemplateVariables = parser.parseSetting(null);
        List<FollowableConfig> followableConfigs = (List<FollowableConfig>)globalTemplateVariables.get(DbConfigParser.FollowableConfigVariable);
        HashMap<Class<?>, List<Field>> requiredFields = new HashMap<Class<?>, List<Field>>();

        TypeFinder typeFinder = TypeFinder.getDefaultTypeFinderInstance();
        for(FollowableConfig followableConfig : followableConfigs){
            String followableTable = followableConfig.getName();
            String followableClassName = parser.tableNameToModelClassSimpleName(followableTable);
            Class<?> modelClass = typeFinder.getModelClass(followableClassName);
            
            List<Field> requiredFollowbleModelFields = findUserCustomedRequiredFields(modelClass,followableConfig.getCollumnsAppendToFollowableTable());
            requiredFields.put(modelClass, requiredFollowbleModelFields);
            
            if(!followableConfig.isReferenceAnotherFollowablesFeed()){
                String followableFeedContentTable = followableConfig.getFeedContentTableName();
                String followableFeedContentClassName = parser.tableNameToModelClassSimpleName(followableFeedContentTable);
                Class<?> followableFeedContentClass = typeFinder.getModelClass(followableFeedContentClassName);
                List<Field> requiredFollowbleContentModelFields = findUserCustomedRequiredFields(followableFeedContentClass,followableConfig.getCollumnsAppendToFeedContentTable());
                requiredFields.put(followableFeedContentClass, requiredFollowbleContentModelFields);
            }
        }
        requiredFieldsCache = requiredFields;
    }
    
    /**
     * Note:this method should be called after installation and compile, otherwise the class ModelTypeFinder might be missing or wrong
     * @param modeClass
     * @return
     * @throws Exception
     */
    protected List<Field> getUserCustomedRequiredFields(Class<?> modeClass) throws Exception{
        if(null== requiredFieldsCache){
            parseAllUserCustomedRequiredFields();
        }
        List<Field> result = requiredFieldsCache.get(modeClass);
        if(result==null){
            requiredFieldsCache.put(modeClass, Empty);
        }
        return requiredFieldsCache.get(modeClass);
    }
    
    
    private List<Field> findUserCustomedRequiredFields(Class<?> modelClass, List<ModelField> userCustomedFields) {
        
        List<Field> requiredField = new ArrayList<Field>();
        for(ModelField modelField: userCustomedFields){
            if(modelField.isNotNull()){
                String name = modelField.getName();
                Field field = ModelReflectionUtil.getModelField(name, modelClass);
                requiredField.add(field);
            }
        }
        return requiredField;
    }
    protected static String ReservedModelPackage = "com.rapidbackend.socialutil.model.reserved";
    
    protected static String[] CommentBaseRequiredFields = new String[]{"CommentBase","feedId","userId","screenName"};
    protected static String[] FeedsourceRequiredFields = new String[]{"Feedsource","url","name","content"};
    protected static String[] FeedContentBaseRequiredFields = new String[]{"FeedContentBase","userId"};
    protected static String[] FileuploadedRequiredFields = new String[]{"Fileuploaded","userid","fileLocation"};
    protected static String[] FollowabeFeedBaseRequiredFields = new String[]{"FollowabeFeedBase","feedId"};
    protected static String[] FollowableBaseRequiredFields = new String[]{"FollowableBase","createdby"};
    protected static String[] OauthapplicationRequiredFields = new String[]{"Oauthapplication","consumerKey","name","sourceUrl","callbackUrl"};
    protected static String[] OauthapplicationuserRequiredFields = new String[]{"Oauthapplicationuser","userId","applicationId","uid","screenName"};
    protected static String[] OauthtokenassociationRequiredFields = new String[]{"Oauthtokenassociation","userId","applicationId","token"};
    protected static String[] SubscriptionFields = new String[]{"Subscription","followable","follower"};
    protected static String[] UserBaseRequiredFields = new String[]{"UserBase","screenName","password"};
       
    
    protected static String[][] reservedModelRequiredFields = 
            new String[][]{
        CommentBaseRequiredFields,FeedsourceRequiredFields,
        FeedContentBaseRequiredFields,FileuploadedRequiredFields,FollowabeFeedBaseRequiredFields,FollowableBaseRequiredFields,
        OauthapplicationRequiredFields,OauthapplicationuserRequiredFields,OauthtokenassociationRequiredFields,
        SubscriptionFields,UserBaseRequiredFields
    };
    
    protected static HashMap<Class<?>, List<Field>> DefaultRequiredFieldsCache = null;
    /**
     * TODO use one single json file to configure thease default fields in both install and parse phrases
     * @return field bean names which is 
     */
    private HashMap<Class<?>, List<Field>> parseDefaultRequiredFields() throws Exception{
        
        if(DefaultRequiredFieldsCache!=null){
            return DefaultRequiredFieldsCache;
        }
        
        HashMap<Class<?>, List<Field>> result = new HashMap<Class<?>, List<Field>>();
        
        for(String[] requiredFields : reservedModelRequiredFields){
            String className = ReservedModelPackage+"."+requiredFields[0];
            Class<?> clazz = Class.forName(className);
            List<Field> fields = new ArrayList<Field>();
            Field[] allFields = ModelReflectionUtil.getModelFields(clazz);
            //if(clazz==CommentBase.class){
                //System.out.print(0);
            //}
            
            if(requiredFields.length>1){
                for(int i=1;i<requiredFields.length;i++){
                    Field field = null;
                    String fieldName = requiredFields[i];
                    for(Field f: allFields){
                        if(f.getName().equals(fieldName)){
                            field = f;
                            break;
                        }
                    }
                    if(field==null){
                        throw new NoSuchFieldException(className+"."+fieldName);
                    }
                    logger.debug("add field "+field.getName() +" to class "+clazz);
                    fields.add(field);
                }
            }
            
            result.put(clazz, fields);
        }
        DefaultRequiredFieldsCache = result;
        return DefaultRequiredFieldsCache;
    }
    
    protected List<Field> getDefaultRequiredFields(Class<?> clazz) throws Exception{
        
        if(DefaultRequiredFieldsCache == null){
            parseDefaultRequiredFields();
        }
        List<Field> results = DefaultRequiredFieldsCache.get(clazz);
        if(results == null){
            results = new ArrayList<Field>();
        }
        
        return results;
    }
    
    protected static class FieldComparator implements Comparator<Field>{
        @Override
        public int compare(Field f1, Field f2){
            return f1.getName().compareTo(f2.getName());
        }
    }
    
    public Set<Field> getRequiredFields(Class<?> clazz) throws Exception{
        if(clazz.getSimpleName().equalsIgnoreCase("Group")){
            System.out.println();
        }
        
        List<Field> defaultFields = getDefaultRequiredFields(clazz);
        if(defaultFields==null || defaultFields.size()==0){// if it is not a reserved class, try its superclass
            defaultFields = getDefaultRequiredFields(clazz.getSuperclass());
        }
        List<Field> customedFields = getUserCustomedRequiredFields(clazz);
        Set<Field> fields = new TreeSet<Field>(new FieldComparator());
        if(defaultFields== null){
            logger.warn("no default fields for clazz " + clazz);
        }
        for(Field f: defaultFields){
            fields.add(f);
        }
        for(Field f: customedFields){
            fields.add(f);
        }
        return fields;
    }    
    
    
    public static void main(String[] args) throws Exception{
        /*ModelInformation modelInformation = new ModelInformation();
        Set<Field> result = modelInformation.getRequiredFields(User.class);
        result.toArray(new String[0]);*/
    }
}
