package com.rapidbackend.socialutil.install.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.ParamFactory;
import com.rapidbackend.socialutil.install.dbinstall.DaoGenerator;
import com.rapidbackend.socialutil.install.dbinstall.ModelField;
import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.install.dbinstall.FollowableConfig;
import com.rapidbackend.socialutil.install.dbinstall.ModelInformation;
import com.rapidbackend.socialutil.install.webserver.FollowableWebConfig;
import com.rapidbackend.socialutil.install.webserver.InstallServlet;
import com.rapidbackend.socialutil.install.webserver.UserDefinedModelWebConfig;
import com.rapidbackend.socialutil.model.util.TypeFinder;
import com.rapidbackend.util.io.CharsetUtil;
import com.rapidbackend.util.io.JsonUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ServiceGenerator {
    
    Logger logger = LoggerFactory.getLogger(ServiceGenerator.class);
    
    protected static String templateFoder = "src/main/resources/dbInstall/model/templates/services";
    protected static String paramConfigFile = "src/main/resources/config/requestParams.xml";
    protected static String paramTemplate = "requestParams.ftl";
    protected static String requestSchemaTemplate = "requestSchema.ftl";
    
    protected static String requestSchemaFile = "src/main/resources/config/requestSchema.xml";
    
    public static String ModelCrudPipelineConfigFile = "src/main/resources/config/socialUtilCrudPipelineConf.xml";
    @Deprecated
    public static String ModelCrudRequestSchemaConfigFile = "src/main/resources/config/socialUtilCrudRequestSchema.xml";
    
    protected static String modelParamsListVariableName = "modelParamsList";
    protected static String requestSchemaConfigListVariableName = "requestSchemaConfigs";
    
    protected Configuration configuration =  new Configuration();
    protected DbConfigParser dbConfigParser;
    TypeFinder typeFinder ;
    

    public static String getSubscriptionServiceBeanName(String followableName){
        return followableName + "SubscriptionService";
    }

    public ServiceGenerator() throws Exception{
        configuration.setDirectoryForTemplateLoading( new File(templateFoder));
        dbConfigParser = new DbConfigParser();
        typeFinder = TypeFinder.getDefaultTypeFinderInstance();
    }     
    
    /**
     * map all models' fields to supported CommandParams
     * @throws Exception
     */
    public void genParams() throws Exception{
        
        HashMap<String, Object> variables = new HashMap<String, Object>();
        Template freemarkerTemplate = configuration.getTemplate(paramTemplate);
        
        BufferedWriter output = new BufferedWriter(new FileWriter(new File(paramConfigFile)));
        
        variables.put(modelParamsListVariableName, getAllModelClassParams());
        freemarkerTemplate.process(variables, output);
    }
    
    public void genSocialUtilConfigs() throws Exception{
        SocialUtilConfigGenerator socialUtilConfigGenerator = new SocialUtilConfigGenerator();
        socialUtilConfigGenerator.genSocialUtilConfiguration();
    }
    
    private List<ModelParams> getAllModelClassParams(){
        Class<?>[] modelClasses = typeFinder.getKnownModelClasses();
        List<ModelParams> modelParamsList = new ArrayList<ModelParams>();
        for (Class<?> clazz :modelClasses) {
            modelParamsList.add(generateModelParams(clazz));
        }
        
        setParamBeanNames(modelParamsList);
        
        return modelParamsList;
    }
    
    
    /**
     * generate params for all classes without settting bean name
     * @param clazz
     * @return
     */
    private ModelParams generateModelParams(Class<?> clazz){
        //Field[] fields = ReflectionTools.getAllFields(clazz,Object.class);
        Field[] fields = ModelReflectionUtil.getModelFields(clazz);
        ModelParams modelParams = new ModelParams();
        for(Field f:fields){
            
            modelParams.setModelName(clazz.getSimpleName());
            try {
                CommandParam param = ParamFactory.createParam(f);// this means we only gen param for supported types
                //param.setBeanName(clazz.getSimpleName()+"."+param.getName());
                modelParams.getParams().add(param);
            } catch (UnsupportedOperationException e) {
                logger.warn("unsupported params in class:"+ clazz.getName(),e);
            }
        }
        
        return modelParams;
    }
    
    private void setParamBeanNames(List<ModelParams> modelParamsList){
        HashMap<String, CommandParam> allparams = new HashMap<String, CommandParam>();
        for(ModelParams modelParams:modelParamsList){
            for(CommandParam param:modelParams.params){
                if(allparams.get(param.getName())==null){
                    allparams.put(param.getName(), param); 
                    param.setBeanName(param.getName());// do not contain the param yet, we use the field name as the param's bean name
                }else {
                    CommandParam exists = allparams.get(param.getName());
                    if(exists.equals(param)){// same name, same type, we set this param marked as skip generating
                        param.setSkipInSchemaGenerate(true);
                        param.setBeanName(param.getName());
                    }else {// same name, not the same type, we set the param's bean name with a prefix of its class name
                        param.setBeanName(modelParams.getModelName()+"."+param.getName());
                    }
                }
            }
        }
    }
    
    private void genCommandDispatchMap() throws Exception{
        genModelCrudCommandMap();
    }
    /**
     * generate the command mapping js file 
     * @throws IOException
     */
    public void genCommandMappings() throws Exception{
        genCommandDispatchMap();
    }
    
    public static String CrudCommadMapFile = "src/main/resources/config/crudCommandMap.js";
    public static String ServiceCommandMapFile = "src/main/resources/config/serviceCommandMap.js";
    
    private void genModelCrudCommandMap() throws Exception{
        HashMap<String, String> crudCommandDispatchMap = new HashMap<String, String>();
        
        for(Class<?> model:modelsExposetoCRUDCommands()){
            String modelName = model.getSimpleName();
            String createCommand = "Create"+modelName;
            String updateCommand = "Update"+modelName;
            String readCommand = "Read"+modelName;
            String deleteCommand = "Delete"+modelName;
            
            crudCommandDispatchMap.put("/"+createCommand, createCommand);
            crudCommandDispatchMap.put("/"+updateCommand, updateCommand);
            crudCommandDispatchMap.put("/"+readCommand, readCommand);
            crudCommandDispatchMap.put("/"+deleteCommand, deleteCommand);
        }
        
        String json = JsonUtil.writeObjectPretty(crudCommandDispatchMap);
        saveConfigFile(json, CrudCommadMapFile);
    }
    
    public static HashMap<String, String> readCrudCommandMap() throws IOException{
        return  JsonUtil.readObject(new File(CrudCommadMapFile), new TypeReference<HashMap<String, String>>() {
        });
    }
    
    public static HashMap<String, String> readServiceCommandMap() throws IOException{
        return JsonUtil.readObject(new File(ServiceCommandMapFile), new TypeReference<HashMap<String, String>>() {
        });
        
    }
    
    public static void saveConfigFile(String content, String filename) throws IOException{
        FileUtils.write(new File(filename), content, CharsetUtil.UTF8);        
    }
    
    public void genRequestSchemas() throws Exception{
        HashMap<String, Object> configVariables = dbConfigParser.parseSettingAndCreateDbScript();
        List<FollowableConfig> followableConfigs = (List<FollowableConfig>)configVariables.get(DbConfigParser.FollowableConfigVariable);
        
        Class<?>[] modelClasses = typeFinder.getKnownModelClasses();
        
        List<RequestSchemaConfig> requestSchemaConfigs = new ArrayList<ServiceGenerator.RequestSchemaConfig>();
        
        for(Class<?> modelClass:modelClasses){
            ModelParams modelParams = generateModelParams(modelClass);
            RequestSchemaConfig requestSchemaConfig = new RequestSchemaConfig();
            for(FollowableConfig followableConfig:followableConfigs){
                
                if(followableConfig.getName().equalsIgnoreCase(modelClass.getSimpleName())){
                    List<ModelField> appendedCollumns = followableConfig.getCollumnsAppendToFollowableTable();
                    for(ModelField collumn :appendedCollumns){
                        if (collumn.isNotNull()) {
                            String generatedParamBeanName = requestSchemaConfig.createParamBeanName(collumn.getName(), modelClass);
                            requestSchemaConfig.getRequriedParamBeanNames().add(generatedParamBeanName);
                        }
                    }
                }
            }
            requestSchemaConfig.setModelName(modelParams.getModelName());
            requestSchemaConfigs.add(requestSchemaConfig);
        }
        
        HashMap<String, Object> variables = new HashMap<String, Object>();
        
        Template freemarkerTemplate = configuration.getTemplate(requestSchemaTemplate);
        BufferedWriter output = new BufferedWriter(new FileWriter(new File(requestSchemaFile)));
        
        variables.put(requestSchemaConfigListVariableName, variables);
        
        freemarkerTemplate.process(variables, output);
    }
    
    public static String RequiredFieldNamesForCreate = "requiredFieldNamesForCreate";
    public static String RequiredFieldNames = "RequiredFieldNames";
    public static String ModelCrudSchemaTemplate = "crud/modelCrudSchema.ftl";
    public static String AllModelCrudSchemaTemplate = "crud/ModelCrudCommandSchema.ftl";
    public static String ModelCrudPipelineTemplate = "crud/modelCrudPipeline.ftl";
    
    public static String createModelPipelineTemplate = "crud/createModelPipeline.ftl";
    public static String readModelPipelineTemplate = "crud/readModelPipeline.ftl";
    public static String deleteModelPipelineTemplate = "crud/deleteModelPipeline.ftl";
    public static String updateModelPipelineTemplate = "crud/updateModelPipeline.ftl";
    public static String queryModelPipelineTemplate = "crud/queryModelPipeline.ftl";
    
    @Deprecated
    public static String ModelCrudRequestSchemaFile = "src/main/resources/config/socialUtilCrudRequestSchema.xml";
    
    public static String ModelCrudPipelineFile = "src/main/resources/config/socialUtilCrudPipelineConf.xml";
    /**
     * gen the schema and pipeline in the same file now, this function is deprecated
     * @throws Exception
     */
    @Deprecated
    public void genCrudRequestSchemas() throws Exception{
        Class<?>[] modelClasses = typeFinder.getKnownModelClasses();
        ModelInformation modelInformation = new ModelInformation();
        
        List<String> modelCrudSchemas = new ArrayList<String>();
        
        for(Class<?> modelClass:modelClasses){
            String modelName = modelClass.getSimpleName();
            Set<Field> requiredFields = modelInformation.getRequiredFields(modelClass);
            //List<String> requiredFieldNames = new ArrayList<String>();
            List<String> requiredFieldNamesForCreate = new ArrayList<String>();
            List<String> requiredFieldNames = new ArrayList<String>();
            for(Field field:requiredFields){
                if(!field.getName().equalsIgnoreCase("id")){
                    requiredFieldNamesForCreate.add(field.getName());
                    
                }
                requiredFieldNames.add(field.getName());
            }
            if(requiredFieldNamesForCreate.size()<1){
                logger.warn("required fields for creating "+modelClass.getSimpleName() + "is empty");
            }
            HashMap<Object, Object> variables = new HashMap<Object, Object>();
            variables.put(RequiredFieldNamesForCreate, StringUtils.join(requiredFieldNamesForCreate, ","));
            variables.put("modelName", modelName);
            
            Template freemarkerTemplate = configuration.getTemplate(ModelCrudSchemaTemplate);
            StringWriter writer = new StringWriter();
            freemarkerTemplate.process(variables, writer);
            modelCrudSchemas.add(writer.toString());
        }
        
        Template template = configuration.getTemplate(AllModelCrudSchemaTemplate);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(ModelCrudRequestSchemaFile)));
        HashMap<Object, Object> variables= new HashMap<Object, Object>();
        variables.put("modelCrudSchemas", modelCrudSchemas);
        
        template.process(variables, writer);
        
    }
    
    
    private String createSchema(Class<?> modelClass) throws Exception{
        StringWriter writer = new StringWriter();
        ModelInformation modelInformation = new ModelInformation();
        String modelName = modelClass.getSimpleName();
        Set<Field> requiredFields = modelInformation.getRequiredFields(modelClass);
        //List<String> requiredFieldNames = new ArrayList<String>();
        List<String> requiredFieldNamesForCreate = new ArrayList<String>();
        List<String> requiredFieldNames = new ArrayList<String>();
        for(Field field:requiredFields){
            if(!field.getName().equalsIgnoreCase("id")){
                requiredFieldNamesForCreate.add(field.getName());
                
            }
            requiredFieldNames.add(field.getName());
        }
        if(requiredFieldNamesForCreate.size()<1){
            logger.warn("required fields for creating "+modelClass.getSimpleName() + "is empty");
        }
        HashMap<Object, Object> variables = new HashMap<Object, Object>();
        variables.put(RequiredFieldNamesForCreate, StringUtils.join(requiredFieldNamesForCreate, ","));
        variables.put("modelName", modelName);
        
        Template crudSchemaTemplate = configuration.getTemplate(ModelCrudSchemaTemplate);
        crudSchemaTemplate.process(variables, writer);
        return writer.toString();
    }
    
    public List<Class<?>> modelsExposetoCRUDCommands() throws Exception{
        List<UserDefinedModelWebConfig> userDefinedModelWebConfigs = InstallServlet.getCurrentWebConfig().getUserDefinedModels();
        List<Class<?>> models = new ArrayList<Class<?>>();
        
        for(UserDefinedModelWebConfig m:userDefinedModelWebConfigs){
            Class<?> c = typeFinder.getModelClass(m.getModelName());
            models.add(c);
        }
        
        DaoGenerator daoGenerator = new DaoGenerator();
        for(String s:daoGenerator.getReservedModels()){
            Class<?> c = typeFinder.getModelClass(s);
            models.add(c);
        }
        
        List<FollowableWebConfig> followableWebConfigs =InstallServlet.getCurrentWebConfig().getFollowableConfigs();
        for(FollowableWebConfig f:followableWebConfigs){
            Class<?> c = typeFinder.getModelClass(f.getFollowableName());
            models.add(c);
        }
        return models;
    }
    
    public void genCrudRequestPipelines() throws Exception{
        List<Class<?>> modelClasses = modelsExposetoCRUDCommands();
        genCrudRequestPipelines(modelClasses,ModelCrudPipelineFile);
    }
    
    /**
     * TODO create different pipelines
     * @throws Exception
     */
    public void genCrudRequestPipelines(List<Class<?>> modelClasses,String outputFile) throws Exception{
        //Class<?>[] modelClasses = typeFinder.getKnownModelClasses();
        
        
        List<ModelCrudPipeline> modelCrudPipelines = new ArrayList<ServiceGenerator.ModelCrudPipeline>();
        
        Template readTemplate = configuration.getTemplate(readModelPipelineTemplate);
        Template createTemplate = configuration.getTemplate(createModelPipelineTemplate);
        Template updateTemplate = configuration.getTemplate(updateModelPipelineTemplate);
        Template deleteTemplate = configuration.getTemplate(deleteModelPipelineTemplate);
        Template queryTemplate = configuration.getTemplate(queryModelPipelineTemplate);
        
        for(Class<?> modelClass:modelClasses){
            StringWriter writer = new StringWriter();
            String modelName = modelClass.getSimpleName();
            
            
            writer.getBuffer().setLength(0);
            ModelCrudPipeline modelCrudPipeline = new ModelCrudPipeline(modelName);
            
            HashMap<Object, Object> modelVariable = new HashMap<Object, Object>();
            modelVariable.put("modelName", modelName);
            
            if(modelName.equalsIgnoreCase("User")){
                List<String> createInterceptors = new ArrayList<String>();
                createInterceptors.add("PasswordEncryptor");
                modelVariable.put("createInterceptors", createInterceptors);
            }
            
            createTemplate.process(modelVariable, writer);
            String createPipeline = writer.toString();
            modelCrudPipeline.setCreatePipeline(createPipeline);
            writer.getBuffer().setLength(0);
            
            readTemplate.process(modelVariable, writer);
            String readPipeline = writer.toString();
            modelCrudPipeline.setReadPipeline(readPipeline);
            writer.getBuffer().setLength(0);
                        
            updateTemplate.process(modelVariable, writer);
            String updatePipeline = writer.toString();
            modelCrudPipeline.setUpdatePipeline(updatePipeline);
            writer.getBuffer().setLength(0);
            
            deleteTemplate.process(modelVariable, writer);
            String deletePipeline = writer.toString();
            modelCrudPipeline.setDeletePipeline(deletePipeline);
            writer.getBuffer().setLength(0);
            
            queryTemplate.process(modelVariable,writer);
            String queryPipeline = writer.toString();
            modelCrudPipeline.setQueryPipeline(queryPipeline);
            
            String requestSchemas = createSchema(modelClass);
            modelCrudPipeline.setSchemas(requestSchemas);
            
            modelCrudPipelines.add(modelCrudPipeline);
        }
        Template template = configuration.getTemplate(ModelCrudPipelineTemplate);
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFile)));
        HashMap<Object, Object> variables= new HashMap<Object, Object>();
        variables.put("modelCrudPipelines", modelCrudPipelines);
        
        template.process(variables, writer);
    }
    
    public static String SocialUtilConfigFile = "src/main/resources/config/socialUtilConfig.xml";
    public static String socialUtilConfigTemplate = "socialUtilConfig.ftl"; 
    public void genSocialUtilServiceConfig() throws Exception{
        Template template = configuration.getTemplate(socialUtilConfigTemplate);
        List<FollowableWebConfig> followableWebConfigs = InstallServlet.getCurrentWebConfig().getFollowableConfigs();
        HashMap<String, Object> variables = new HashMap<String, Object>();
        
        List<String> serviceBeans = new ArrayList<String>();
        
        for(FollowableWebConfig f:followableWebConfigs){
            String name = f.getFollowableName();
            String inboxServiceName = StringUtils.capitalize(name)+"InboxService";
            serviceBeans.add(inboxServiceName);
        }
        variables.put("serviceBeans", serviceBeans);
        StringWriter writer = new StringWriter();
        template.process(variables, writer);
        FileUtils.write(new File(SocialUtilConfigFile), writer.toString());
    }
    
    public static String SocialUtilSecurityConfigFile = "src/main/resources/config/socialUtilSecurityConf.xml";
    public static String socialUtilSecurityConfigTemplate = "socialUtilSecurityConf.ftl"; 
    public void genSocialUtilSecurityConfig() throws Exception{
        Template template = configuration.getTemplate(socialUtilSecurityConfigTemplate);
        List<FollowableWebConfig> followableWebConfigs = InstallServlet.getCurrentWebConfig().getFollowableConfigs();
        HashMap<String, Object> variables = new HashMap<String, Object>();
        
        List<String> serviceBeans = new ArrayList<String>();
        
        for(FollowableWebConfig f:followableWebConfigs){
            String name = f.getFollowableName();
            String inboxServiceName = StringUtils.capitalize(name)+"InboxService";
            serviceBeans.add(inboxServiceName);
        }
        variables.put("inboxServiceBeans", serviceBeans);
        StringWriter writer = new StringWriter();
        template.process(variables, writer);
        FileUtils.write(new File(SocialUtilSecurityConfigFile), writer.toString());
    }
    
    
    public void genRedisConfig() throws Exception{
        RedisConfigGenerator redisConfigGenerator = new RedisConfigGenerator();
        redisConfigGenerator.createRedisConfigs();
    }
    
    
    public static class RequestSchemaConfig{
        protected String modelName;

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }
        
        protected List<String> requriedParamBeanNames;

        public List<String> getRequriedParamBeanNames() {
            return requriedParamBeanNames;
        }

        public void setRequriedParamBeanNames(List<String> requriedParamBeanNames) {
            this.requriedParamBeanNames = requriedParamBeanNames;
        }

        /**
         * create the param bean which is used to represent a param
         * @param fieldName
         * @param clazz
         * @return
         */
        public String createParamBeanName(String fieldName,Class<?> clazz){
            return clazz.getSimpleName()+"."+fieldName;
        }
        
    }
    
    public static class ModelCrudPipeline{
        
        private String schemas;
        private String readPipeline;
        private String deletePipeline;
        private String updatePipeline;
        private String createPipeline;
        private String queryPipeline;
        private String modelName;
        
        public ModelCrudPipeline(String modelName){
            this.modelName = modelName;
        }
        public String getModelName() {
            return modelName;
        }
        public void setModelName(String modelName) {
            this.modelName = modelName;
        }
        public String getSchemas() {
            return schemas;
        }
        public void setSchemas(String schemas) {
            this.schemas = schemas;
        }
        public String getReadPipeline() {
            return readPipeline;
        }
        public void setReadPipeline(String readPipeline) {
            this.readPipeline = readPipeline;
        }
        public String getDeletePipeline() {
            return deletePipeline;
        }
        public void setDeletePipeline(String deletePipeline) {
            this.deletePipeline = deletePipeline;
        }
        public String getUpdatePipeline() {
            return updatePipeline;
        }
        public void setUpdatePipeline(String updatePipeline) {
            this.updatePipeline = updatePipeline;
        }
        public String getCreatePipeline() {
            return createPipeline;
        }
        public void setCreatePipeline(String createPipeline) {
            this.createPipeline = createPipeline;
        }
        public String getQueryPipeline() {
            return queryPipeline;
        }
        public void setQueryPipeline(String queryPipeline) {
            this.queryPipeline = queryPipeline;
        }
        
    }
    
    
}
