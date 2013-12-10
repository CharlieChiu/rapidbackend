package com.rapidbackend.socialutil.install.dbinstall;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.rapidbackend.socialutil.install.dbinstall.FollowableConfig;
import com.rapidbackend.socialutil.install.model.ClassDescriptor;
import com.rapidbackend.socialutil.install.model.JavaModelGenerator;
import com.rapidbackend.socialutil.install.model.ModelType;
import com.rapidbackend.socialutil.install.webserver.UserDefinedModelWebConfig;

import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * 
 * @author chiqiu
 *
 */
public class DaoGenerator {
    
    protected Configuration configuration =  new Configuration();
    
    public static String templateFoder = "src/main/resources/dbInstall/model/templates";
    
    public static String daoConfigFile = "src/main/resources/config/socialUtilDaoConfig.xml";
    public static String daoPackage = "com.rapidbackend.socialutil.dao.mysql.gen";
    public static String modelPackage = "com.rapidbackend.socialutil.model";
    public static String modelTypeFinderClass = "com.rapidbackend.socialutil.model.util.ModelTypeFinder"; 
    public static String projectPath = "src/main/java/";
    
    /**
     * variables used in freemarker templates
     */
    protected static String daoUnitVariable = "tableDaos";
    protected static String modelClassNameCollectionVariable = "classNames";
    protected static String modelClassSimpleNameCollectionVariable = "simpleclassNames";
    
    protected List<String> reservedModels = new ArrayList<String>();
    
    
    public List<String> getReservedModels() {
        return reservedModels;
    }
    
    public DaoGenerator() throws Exception{
        configuration.setDirectoryForTemplateLoading( new File(templateFoder));
        initDefaultTables();
        
    }
    public void initDefaultTables(){
        //defaultTables.add("user"); we now put user into the webconfig as the default followable
        reservedModels.add("oauthapplication");
        reservedModels.add("oauthapplicationuser");
        reservedModels.add("oauthtokenassociation");
        reservedModels.add("fileuploaded");
        reservedModels.add("feedsource");
    }
    
    
    public static String getModelTypeFinderClass() {
        return modelTypeFinderClass;
    }
    
    public void genModels() throws Exception{
        genReservedModels();
        
        genFollowableModels();
        
        genUserDefinedModels();
    }
    
    private void genReservedModels() throws Exception{
        for(String model:reservedModels){
            JavaModelGenerator.createGenerator(new ClassDescriptor(model, ModelType.reservedModel,null)).genModelClass();
        }
    }
    
    private void genUserDefinedModels() throws Exception{
        DbConfigParser dbConfigParser = new DbConfigParser();
        List<UserDefinedModelWebConfig> userDefinedModelWebConfigs = (List<UserDefinedModelWebConfig>)dbConfigParser.parseSetting(null).get(DbConfigParser.UserDefinedModelConfigVariable);
        for(UserDefinedModelWebConfig modelConfig : userDefinedModelWebConfigs){
            String modelName = modelConfig.getModelName();
            JavaModelGenerator.createGenerator(new ClassDescriptor(modelName, ModelType.userDefinedModel,modelConfig.getModelConfig()) ).genModelClass();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void genFollowableModels() throws Exception{
        DbConfigParser dbConfigParser = new DbConfigParser();
        
        List<FollowableConfig> followableConfigs = (List<FollowableConfig>)dbConfigParser.parseSetting(null).get(DbConfigParser.FollowableConfigVariable);
        for(FollowableConfig followableConfig : followableConfigs){
            String name = followableConfig.getName();
            if(name.equalsIgnoreCase("user")){
                JavaModelGenerator.createGenerator(new ClassDescriptor(name, ModelType.user,followableConfig.getCollumnsAppendToFollowableTable())).genModelClass();
            }else {
                JavaModelGenerator.createGenerator(new ClassDescriptor(name, ModelType.followable,followableConfig.getCollumnsAppendToFollowableTable())).genModelClass();
            }
            String feedByFollowable = followableConfig.getFeedByFollowableTableName();
            JavaModelGenerator.createGenerator(new ClassDescriptor(feedByFollowable, ModelType.followableFeed,null)).genModelClass();
            
            String subscription = followableConfig.getSubscriptionTableName();
            JavaModelGenerator.createGenerator(new ClassDescriptor(subscription, ModelType.subscription,null)).genModelClass();
            
            if(!followableConfig.isReferenceAnotherFollowablesFeed()){
                String feedComment = followableConfig.getFeedCommentTableName();
                JavaModelGenerator.createGenerator(new ClassDescriptor(feedComment, ModelType.comment,followableConfig.getCollumnsAppendToCommentTable())).genModelClass();
                
                String feedContent = followableConfig.getFeedContentTableName();
                JavaModelGenerator.createGenerator(new ClassDescriptor(feedContent, ModelType.feedContent,followableConfig.getCollumnsAppendToFeedContentTable())).genModelClass();
            }
            
        }
    }
    
    
    
    
    
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> genDao() throws Exception{
        DbConfigParser dbConfigParser = new DbConfigParser();
        HashMap<String, Object> variables = dbConfigParser.parseSettingAndCreateDbScript();
        Template springDaoConfig = configuration.getTemplate("dao/daoConfig.ftl");
        
        List<FollowableConfig> followableConfigs = (List<FollowableConfig>)variables.get(DbConfigParser.FollowableConfigVariable);
        
        Set<DaoUnit> tableDaos = new TreeSet<DaoGenerator.DaoUnit>();
        
        for(FollowableConfig followableConfig:followableConfigs){// we init user config first to over write
            DaoUnit followable = createDaoUnit(followableConfig.getName());
            followable.isFollowableTable = true;
            tableDaos.add(followable);
            
            DaoUnit feeds = createDaoUnit(followableConfig.getFeedByFollowableTableName());
            feeds.isFeedTable = true;
            tableDaos.add(feeds);
            
            DaoUnit subscription = createDaoUnit(followableConfig.getSubscriptionTableName());
            subscription.isSubscriptionTable = true;
            tableDaos.add(subscription);
                        
            if(!followableConfig.isReferenceAnotherFollowablesFeed()){
                DaoUnit feedContent = createDaoUnit(followableConfig.getFeedContentTableName());
                feedContent.isFeedContentTable = true;
                tableDaos.add(feedContent);
                
                DaoUnit feedComment = createDaoUnit(followableConfig.getFeedCommentTableName());
                feedComment.isCommentTable = true;
                tableDaos.add(feedComment);
            }
            //TODO WARN: don't forget to add more supported table here
        }
        List<UserDefinedModelWebConfig> userDefinedModelWebConfigs = (List<UserDefinedModelWebConfig>)dbConfigParser.parseSetting(null).get(DbConfigParser.UserDefinedModelConfigVariable);
        for(UserDefinedModelWebConfig userDefinedModelWebConfig:userDefinedModelWebConfigs){
            DaoUnit daoUnit = createDaoUnit(userDefinedModelWebConfig.getModelName());
            tableDaos.add(daoUnit);
        }
        
        for(String s:reservedModels){
            DaoUnit daoUnit = createDaoUnit(s);
            tableDaos.add(daoUnit);
        }
        
        
        
        variables.put(daoUnitVariable, tableDaos);
        
        BufferedWriter output = new BufferedWriter(new FileWriter(new File(daoConfigFile)));
        springDaoConfig.process(variables, output);
        
        genDaoClass(tableDaos);
        return variables;
        
    }
    
    public void genModelTypeFinder() throws Exception{
        Template typeFinderTemplate = configuration.getTemplate("ModelTypeFinder.ftl");
        HashMap<String, Object> variables = genDao();
        @SuppressWarnings("unchecked")
        Collection<DaoUnit> tableDaos = (Collection<DaoGenerator.DaoUnit>)variables.get(daoUnitVariable);
        List<String> classNames = new ArrayList<String>();
        List<String> simpleClassNames = new ArrayList<String>();
        
        for(DaoUnit daoUnit:tableDaos){
            classNames.add(tableToClassName(daoUnit.tableName));
            simpleClassNames.add(tableToClassSimpleName(daoUnit.tableName));
        }
        
        variables.put(modelClassNameCollectionVariable, classNames);
        variables.put(modelClassSimpleNameCollectionVariable, simpleClassNames);
        variables.put("modelPackage", modelPackage);
        
        String dirPath = projectPath + modelTypeFinderClass.replace('.', '/');
        String fileName = dirPath + ".java";
        fileName = fileName.replaceAll("\\/\\/", "/");
        BufferedWriter output = new BufferedWriter(new FileWriter(new File(fileName)));
        typeFinderTemplate.process(variables, output);
    }
    
    public static String tableToClassName(String tableName) {
        return DaoGenerator.modelPackage+"."+StringUtils.capitalize(tableName);
    }
    
    public static String tableToClassSimpleName(String tableName){
        return StringUtils.capitalize(tableName);
    }
    
    public void genDaoClass(Collection<DaoUnit> tableDaos) throws Exception{
        Template daoclassTemplate = configuration.getTemplate("dao/daoClass.ftl");
        for(DaoUnit daoUnit:tableDaos){
            HashMap<String, String> variables = new HashMap<String, String>();
            variables.put("daoName", daoUnit.daoName);
            //if(daoUnit.daoName.equalsIgnoreCase("GroupDao")){
                //System.out.print("a");
            //}
            variables.put("daoPackage", daoPackage);
            variables.put("baseDaoClass", getBaseDaoName(daoUnit));
            String dirPath = projectPath + daoPackage.replace('.', '/');
            String fileName = dirPath + '/'+daoUnit.daoName+".java";
            fileName = fileName.replaceAll("\\/\\/", "/");
            StringWriter stringWriter = new StringWriter();
            daoclassTemplate.process(variables, stringWriter);
            FileUtils.write(new File(fileName), stringWriter.toString());
        }
    }
    /**
     * 
     * @param tableName
     * @return
     */
    public String getBaseDaoName(DaoUnit daoUnit){
        if(daoUnit.isFeedContentTable){
            return "FeedContentDao";
        }else if(daoUnit.isFeedTable){
            return "FeedDao";
        }else if(daoUnit.isFollowableTable){
            if(daoUnit.tableName.equalsIgnoreCase("User")){
                return "UserDao";
            }else {
                return "FollowableDao";
            }
        }else if(daoUnit.isSubscriptionTable){
            return "SubscriptionDao";
        }else if(daoUnit.isCommentTable){
             return "FeedCommentDao";
        }else {
            return "BaseDao";
        }
    }
    
    public DaoUnit createDaoUnit(String tableName){
        DaoUnit daoUnit = new DaoUnit();
        String className = StringUtils.capitalize(tableName);
        daoUnit.setClassName(className);
        daoUnit.setTableName(tableName);
        daoUnit.setInsertName("insert"+className);
        daoUnit.setTemplateName("jdbcTemplate"+className);
        daoUnit.setDaoName(className+"Dao");
        daoUnit.setDaoClassName(daoPackage+"."+daoUnit.daoName);
        return daoUnit;
    }
    
    public static class DaoUnit implements Comparable<DaoUnit>{
        protected String className;
        protected String tableName;
        protected String insertName;
        protected String templateName;
        protected String daoName;
        protected String daoClassName;
        
        protected boolean isSubscriptionTable = false;
        protected boolean isFollowableTable  = false;
        protected boolean isFeedContentTable  = false;
        protected boolean isFeedTable = false;
        protected boolean isCommentTable = false;
        
        
        public boolean isCommentTable() {
            return isCommentTable;
        }
        public void setCommentTable(boolean isCommentTable) {
            this.isCommentTable = isCommentTable;
        }
        public String getClassName() {
            return className;
        }
        public void setClassName(String className) {
            this.className = className;
        }
        public String getTableName() {
            return tableName;
        }
        public void setTableName(String tableName) {
            this.tableName = tableName;
        }
        public String getInsertName() {
            return insertName;
        }
        public void setInsertName(String insertName) {
            this.insertName = insertName;
        }
        public String getTemplateName() {
            return templateName;
        }
        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }
        public String getDaoName() {
            return daoName;
        }
        public void setDaoName(String daoName) {
            this.daoName = daoName;
        }
        public String getDaoClassName() {
            return daoClassName;
        }
        public void setDaoClassName(String daoClassName) {
            this.daoClassName = daoClassName;
        }
        public boolean isSubscriptionTable() {
            return isSubscriptionTable;
        }
        public void setSubscriptionTable(boolean isSubscriptionTable) {
            this.isSubscriptionTable = isSubscriptionTable;
        }
        public boolean isFollowableTable() {
            return isFollowableTable;
        }
        public void setFollowableTable(boolean isFollowableTable) {
            this.isFollowableTable = isFollowableTable;
        }
        public boolean isFeedContentTable() {
            return isFeedContentTable;
        }
        public void setFeedContentTable(boolean isFeedContentTable) {
            this.isFeedContentTable = isFeedContentTable;
        }
        public boolean isFeedTable() {
            return isFeedTable;
        }
        public void setFeedTable(boolean isFeedTable) {
            this.isFeedTable = isFeedTable;
        }
        @Override
        public int compareTo(DaoUnit o){
            return this.tableName.compareTo(o.tableName);
        }
        @Override
        public boolean equals(Object o){
            if (this == o) {
                return true;
            }
            if(o == null ||  !(o instanceof DaoUnit)){
                return false;
            }
            return this.tableName.equals(((DaoUnit)o).tableName);
        }
    }
    
}
