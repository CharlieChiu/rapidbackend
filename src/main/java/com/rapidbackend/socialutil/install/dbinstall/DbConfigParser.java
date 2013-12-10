package com.rapidbackend.socialutil.install.dbinstall;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.rapidbackend.socialutil.install.webserver.InstallServlet;
import com.rapidbackend.socialutil.install.webserver.FollowableWebConfig;
import com.rapidbackend.socialutil.install.webserver.UserDefinedModelWebConfig;
import com.rapidbackend.socialutil.install.webserver.WebConfig;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * @author chiqiu
 * TODO add foreign key constraints??
 */
public class DbConfigParser {
	
	protected  Configuration  configuration =  new Configuration();
	protected static String templateFoder = "src/main/resources/dbInstall/model/templates";
	public static String FollowableConfigVariable = "followableConfigs";
	public static String UserDefinedModelConfigVariable = "userDefinedModelConfigs";
	
	public static String customCollumnsNode = "customCollumns";
	public static String collumnNode  = "collumn";
	public static String collumnIndexNode = "collumnIndex";
	public static String createIndexNode = "create";
	public static String createUniqueIndexNode = "unique";
	public static String collumnNameNode = "name";
	public static String collumnDatatypeNode = "type";
	

	public static String feedContentTableNode = "feedContentTable";
	public static String followableTableNode = "followableTable";
	
    static XPathFactory xPathFactory = XPathFactory.newInstance();
	@Deprecated
	protected static HashMap<String, String> typeMapping = new HashMap<String, String>();
	
	public DbConfigParser(String templateFolerPath) throws IOException{
		if(StringUtils.isEmpty(templateFolerPath)){
			configuration.setDirectoryForTemplateLoading( new File(templateFoder));
		}else {
			configuration.setDirectoryForTemplateLoading(new File(templateFolerPath));
		}
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		
		//readTypeSetting();
	}
	
	public DbConfigParser() throws IOException{
		this(null);
	}
	
	public void createDbInstallScript(){
	}
		
	
	/**
	 * Parse and 
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 * @throws XPathException
	 */
	public  HashMap<String, Object> parseSettingAndCreateDbScript() throws IOException,TemplateException{

        
        HashMap<String, Object> globalTemplateVariables = parseSetting();
        
        createInstallScripts(globalTemplateVariables);
        
        return globalTemplateVariables;
        
	}
	
	public HashMap<String, Object> parseSetting() throws IOException{
	    return parseSetting(null);
	}
	
	/**
	 * 
	 * @param config if null then read the stored config on disk
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, Object> parseSetting(WebConfig config) throws IOException{
	    WebConfig webConfig;
	    if(null != config){
	        webConfig = config;
	    }else {
	        webConfig = InstallServlet.getCurrentWebConfig();
        }
	    
        
        HashMap<String, Object> globalTemplateVariables = new HashMap<String, Object>();
        
        String dbName = webConfig.getDbName();
        Assert.isTrue(!StringUtils.isEmpty(dbName), "dbName cannot be null");
        
        String userName = webConfig.getUsername();
        Assert.isTrue(!StringUtils.isEmpty(userName), "userName cannot be null");
        
        String password = webConfig.getPassword();
        Assert.isTrue(!StringUtils.isEmpty(password), "password cannot be null");
        
        String dbUri = webConfig.getDbUri();
        Assert.isTrue(!StringUtils.isEmpty(dbUri), "dbUri cannot be null");
        
        globalTemplateVariables.put("dbName", dbName);
        globalTemplateVariables.put("userName", userName);
        globalTemplateVariables.put("password", password);
        globalTemplateVariables.put("dbUri", dbUri);
        
        List<FollowableWebConfig> followableWebConfigs = webConfig.getFollowableConfigs();
        
        List<FollowableConfig> followableConfigs = new ArrayList<FollowableConfig>();
        
        for(FollowableWebConfig followableWebConfig: followableWebConfigs){
            FollowableConfig followableConfig = followableWebConfig.toFollowableConfig();
            followableConfigs.add(followableConfig);
        }
        
        if(followableConfigs.size()<1){
            throw new RuntimeException("no followable configured ! Exit installation");
        }
        
        checkFollowableConfig(followableConfigs);
        
        followableConfigs = sortConfigs(followableConfigs);
        
        HashMap<String, FollowableConfig> configMap = new HashMap<String, FollowableConfig>();
        
        for (FollowableConfig followableConfig :followableConfigs) {
            configMap.put(followableConfig.getName(), followableConfig);
        }
        
        globalTemplateVariables.put(FollowableConfigVariable, followableConfigs);
        
        
        List<UserDefinedModelWebConfig> userDefinedWebConfigs = webConfig.getUserDefinedModels();
        globalTemplateVariables.put(UserDefinedModelConfigVariable, userDefinedWebConfigs);
        
        return globalTemplateVariables;
	}
	
	/**
	 * This method returns userCustomed field's "param bean" names, this is a helper method in crud schema generation after app installation
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
    public  HashMap<String, List<String>> getUserCustomedRequiredParams() throws IOException{
	    
	    HashMap<String, Object> globalTemplateVariables = parseSetting(null);
	    List<FollowableConfig> followableConfigs = (List<FollowableConfig>)globalTemplateVariables.get(FollowableConfigVariable);
	    
	    HashMap<String, List<String>> requiredParams = new HashMap<String, List<String>>();
	    for(FollowableConfig followableConfig : followableConfigs){
	        String followableTable = followableConfig.getName();
	        String followableClass = tableNameToModelClassSimpleName(followableTable);
	        
	        List<String> requiredFollowbleModelParams = getRequiredParamBeanNames(followableClass,followableConfig.getCollumnsAppendToFollowableTable());
	        requiredParams.put(followableClass, requiredFollowbleModelParams);
	        
	        if(!followableConfig.isReferenceAnotherFollowablesFeed()){
	            String followableFeedContentTable = followableConfig.getFeedContentTableName();
	            String followableFeedContentClass = tableNameToModelClassSimpleName(followableFeedContentTable);
	            
	            List<String> requiredFollowbleContentModelParams = getRequiredParamBeanNames(followableFeedContentClass,followableConfig.getCollumnsAppendToFeedContentTable());
	            requiredParams.put(followableFeedContentClass, requiredFollowbleContentModelParams);
	        }
	    }
	    
	    return requiredParams;
	}
	
	
	
	protected String tableNameToModelClassSimpleName(String tableName){
	    return StringUtils.capitalize(tableName);
	}
	
	protected String modelFieldToCommpandParamBeanName(String className,String fieldName){
	    return className+"."+fieldName;
	}
	
	private List<ModelField> getRequiredFields(List<ModelField> fields){
	    List<ModelField> result = new ArrayList<ModelField>();
	    for(ModelField field:fields){
	        if(field.isNotNull()){
	            result.add(field);
	        }
	    }
	    return result;
	}
	
	private List<String> getRequiredParamBeanNames(String className,List<ModelField> fields){
	    List<String> result = new ArrayList<String>();
	    
	    List<ModelField> requiredFields = getRequiredFields(fields);
	    
	    for(ModelField field: requiredFields){
	        result.add(modelFieldToCommpandParamBeanName(className,field.getName()));
	    }
	    return result;
	}
	
	
	@SuppressWarnings("unchecked")
    public  void createInstallScripts(HashMap<String, Object> globalTemplateVariables) throws IOException,TemplateException{
	    Collection<FollowableConfig> configs = (Collection<FollowableConfig>)globalTemplateVariables.get(FollowableConfigVariable);
	    HashMap<String, FollowableConfig> configMap = new HashMap<String, FollowableConfig>();
	    
	    for (FollowableConfig followableConfig :configs) {
            configMap.put(followableConfig.getName(), followableConfig);
        }
	    
	    for(FollowableConfig followableConfig:configs){
	        Template followableTableTemplate = configuration.getTemplate("followable.ftl");
	        Template userTemplate = configuration.getTemplate("user.ftl");
	        
	        HashMap<String, Object>  followableVariables = new HashMap<String, Object>(globalTemplateVariables);
	        followableVariables.put("followableConfig", followableConfig);
	        StringWriter writer = new StringWriter();
	        
	        /**
	         * create followable table
	         * **/
	        if(followableConfig.getName().equalsIgnoreCase("user")){ // be aware of we have a default table implementation for user
	            //List<Collumn> collumnsToAppend = readUserTableConfig();// add customed collums
	            
	            //followableConfig.setCollumnsToAppend(collumnsToAppend);
	            userTemplate.process(followableVariables, writer);
	            followableConfig.setFollowableTableSql(writer.toString());
	        }else{
	            followableVariables.put("followableName", followableConfig.getName());
                followableTableTemplate.process(followableVariables, writer);
                followableConfig.setFollowableTableSql(writer.toString());
	        }
	        	        
	        writer.getBuffer().setLength(0);
	        
	        /**
	         * create feedContentTable and feedcomment table
	         */
	        if(!followableConfig.isReferenceAnotherFollowablesFeed()){
	            Template feedContentTable = configuration.getTemplate("feedcontent.ftl");
	            feedContentTable.process(followableVariables, writer);
	            followableConfig.setFeedContentTableSql(writer.toString());
	            writer.getBuffer().setLength(0);
	            
	            Template feedCommentTable = configuration.getTemplate("feedComment.ftl");
	            feedCommentTable.process(followableVariables, writer);
	            followableConfig.setFeedCommentTableSql(writer.toString());
	            writer.getBuffer().setLength(0);
	        }
	        
	        /**
	         * create subscription table
	         */
	        Template subsctiptionTemplate = configuration.getTemplate("subscription.ftl");
	        subsctiptionTemplate.process(followableVariables, writer);
	        followableConfig.setSubscriptionTableSql(writer.toString());
	        writer.getBuffer().setLength(0);
	        
	        /**
	         * create feedbyfollowable table
	         */
	        Template feedbyfollowableTemplate = configuration.getTemplate("feedbyfollowable.ftl");
	        feedbyfollowableTemplate.process(followableVariables, writer);
            followableConfig.setFeedByFollowableTableSql(writer.toString());
            writer.getBuffer().setLength(0);
	    }
	    
	    
	    // now processing the userDefinedModels
	    
	    List<UserDefinedModelWebConfig> userDefinedModelWebConfigs = (List<UserDefinedModelWebConfig>)globalTemplateVariables.get(DbConfigParser.UserDefinedModelConfigVariable);
	    
	    for(UserDefinedModelWebConfig userDefinedModelWebConfig:userDefinedModelWebConfigs){
	        userDefinedModelWebConfig.mapModelFieldTypes();
	        HashMap<String, Object > vars = (HashMap<String, Object >)globalTemplateVariables.clone();
	        
	        vars.put("model", userDefinedModelWebConfig);
	        StringWriter writer = new StringWriter();
	        Template template = configuration.getTemplate("userDedefinedModel.ftl");
	        template.process(vars, writer);
	        userDefinedModelWebConfig.setInstallSql(writer.toString());
	    }
	    
	    
	    StringWriter writer = new StringWriter();
	    //TODO change it here, 
        //make createInstallScripts(HashMap<String, FollowableConfig> configMap,HashMap<String, Object> globalTemplateVariables) has only one function
	    Template dbinstallTemplate = configuration.getTemplate("dbinstallsql.ftl");
	    globalTemplateVariables.put("modelConfigs", configs);// TODO, should this line be removed?
	    dbinstallTemplate.process(globalTemplateVariables, writer);
	    
	    BufferedWriter output = new BufferedWriter(new FileWriter(new File(DbInstaller.installSqlFilePath)));
	    output.write(writer.toString());
	    output.flush();output.close();
	}
	
	/**
	 * move followable which doesn't have independent feed content table to the tail, to avoid db istall error
	 * @param modelConfigs
	 * @return
	 */
	public static List<FollowableConfig> sortConfigs(List<FollowableConfig> modelConfigs){
	    List<FollowableConfig> configs = new ArrayList<FollowableConfig>();
	    List<FollowableConfig> configs2 = new ArrayList<FollowableConfig>();
	    
	    for(FollowableConfig config :modelConfigs){
	        if(!config.isReferenceAnotherFollowablesFeed()){
	           configs.add(config); 
	        }else {
                configs2.add(config);
            }
	    }
	    for(FollowableConfig config:configs2){
	        configs.add(config);
	    }
	    return configs;
	    
	}
	
	/**
	 * check if followable is duplicate to another, check if feed content table exists for every followable
	 * @param configs
	 */
	public static void checkFollowableConfig(List<FollowableConfig> configs){
	    HashMap<String, String> configMap = new HashMap<String, String>();
	    
	    for(FollowableConfig config:configs){
	        if(configMap.containsKey(config.getName())){
	            throw new RuntimeException("duplicate followable name:"+config.getName());
	        }else {
	            if(config.isReferenceAnotherFollowablesFeed()){
	                configMap.put(config.getName(), "");
	            }else{
	                configMap.put(config.getName(), config.getFeedContentTableName());
	                configMap.put(config.getFeedContentTableName(), config.getName());
	            }
            }
	    }
	    
	    for(FollowableConfig config:configs){
	        if(config.isReferenceAnotherFollowablesFeed()){
	            String referenceFollowableName = configMap.get(config.getReferenceFollowableName());
	            
	            if(StringUtils.isEmpty(referenceFollowableName) || referenceFollowableName.equalsIgnoreCase(config.getName())){
	                throw new RuntimeException("no valid reference folloable found for followable:"+config.getName());
	            }
	        }
	    }
	    
	}
	
	
	
	
	
}
