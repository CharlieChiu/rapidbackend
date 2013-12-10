package com.rapidbackend.core.request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.socialutil.model.util.TypeFinder;

/**
 * configurable schema in spring xml. Request parser will use this schema to parse a request.<br>
 * each schema contains one OptionalParamGroup and many MandatoryParamGroup.<br>
 * Note that a bad schema configuration may cause socialutility application exit with an @IllegalArgumentException
 * TODO need to specify and check http method?
 * @author chiqiu
 *
 */

public class RequestSchema extends AppContextAware implements BeanNameAware{
    
    public static String HTTP_GET = "GET";
    public static String HTTP_POST = "POST";
    public static String HTTP_DELETE = "DELETE";
    public static String HTTP_PUT = "PUT";
    
    
	Logger logger = LoggerFactory.getLogger(RequestSchema.class);
	
	protected List<MandatoryParamGroup> mandatorys;
	protected OptionalParamGroup optionals;
	protected String schemaName;
	protected boolean autowireModelPropertyAsParam = false;
    /**
     * short string input name for modeltypeFinder
     */
    protected String modelName;
    protected TypeFinder modelTypeFinder ;
    
	/**
	 * the command name that will be executed in response to the request
	 */
	protected String command;
	
	protected String beanName;
	
	protected boolean configChecked = false;
	
	protected String optionalParams;
	protected String requiredParams;
	
	protected String httpMethod = HTTP_GET;
	
	public String getHttpMethod() {
        return httpMethod;
    }
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    public String getOptionalParams() {
        return optionalParams;
    }
    public void setOptionalParams(String optionalParams) {
        this.optionalParams = optionalParams;
    }
    public String getRequiredParams() {
        return requiredParams;
    }
    public void setRequiredParams(String requiredParams) {
        this.requiredParams = requiredParams;
    }
    public boolean isConfigChecked() {
		return configChecked;
	}
    
    @Override
    public void setBeanName(String beanName) {
        nameCheck(beanName);
        this.beanName = beanName;
    }
    
    public static String beanNameSuffix = "Schema";
    
    private void nameCheck(String beanName){
        if(!StringUtils.endsWith(beanName, beanNameSuffix)){
           throw new RuntimeException("command schema name should end with 'Schema', current bean name is set to "+beanName); 
        }
    }
    
    
    public String commandName(){
        return StringUtils.removeEnd(beanName, beanNameSuffix);
    }
    
    public RequestSchema() throws Exception{
        //modelTypeFinder = (TypeFinder)Class.forName(DaoGenerator.modelTypeFinderClass).newInstance();
    }
	/**
	 * make sure no empty value is in the configuration
	 */
	public void checkConfig(){
	    initParams();
		Assert.notNull(getSchemaName());
		if(null!=mandatorys){
		    Assert.notEmpty(mandatorys,getSchemaName()+" madatory params list has no value");
		    for(MandatoryParamGroup mandatory : mandatorys){
	            Assert.notNull(mandatory, getSchemaName() + "madatory params group configured as null");
	            Assert.notEmpty(mandatory.getParamList(),getSchemaName()+" madatory params group's param list is empty");
	            for(CommandParam param:mandatory.getParamList()){
	                Assert.notNull(param,getSchemaName()+" mandatory param is null");
	                Assert.notNull(param.getName(),getSchemaName()+" mandatory param name is null");
	            }
	        }
		}
		
		if(null != optionals){
			Assert.notEmpty(optionals.getOptionalParamList(),getSchemaName()+" optional params group's paramlist has no value");
			for(CommandParam param : optionals.getOptionalParamList()){
				Assert.notNull(param,getSchemaName()+" optional param is null");
				Assert.notNull(param.getName(),getSchemaName()+" optional param name is null");
			}
		}
		configChecked = true;
	}
	/**
	 * 
	 */
	public void initParams(){
	    try {
	        initOptional();
	        initRequired();
        } catch (NoSuchBeanDefinitionException e) {
            throw new BackendRuntimeException(BackendRuntimeException.InternalServerError,"error during init params",e);
        }
	    
	}
	
	public void initOptional(){
	    if(!StringUtils.isEmpty(optionalParams)){
	        String[] optionalparamNames = optionalParams.split(",");
	        this.optionals = new OptionalParamGroup();
	        for(String optionalBean: optionalparamNames){
	            CommandParam param = (CommandParam)getApplicationContext().getBean(optionalBean);
	            Assert.notNull(param,"param bean "+optionalBean+" is null, please check schema config");
	            optionals.getOptionalParamList().add(param);
	        }
	    }
	}
	public void initRequired(){
	    if(!StringUtils.isEmpty(requiredParams)){
	        mandatorys = new ArrayList<RequestSchema.MandatoryParamGroup>();
	        String[] madantoryGroups = requiredParams.split(",");
	        for(String madantoryGroup :madantoryGroups){
	            MandatoryParamGroup mGroup = new MandatoryParamGroup();
	            String[] params = madantoryGroup.split("\\|");
	            for(String madantoryParamBean : params){
	                CommandParam param = (CommandParam)getApplicationContext().getBean(madantoryParamBean);
	                mGroup.getParamList().add(param);
	            }
	            mandatorys.add(mGroup);
	        }
	    }
	}
	
	public String getModelName() {
        return modelName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    public boolean isAutowireModelPropertyAsParam() {
        return autowireModelPropertyAsParam;
    }
    public void setAutowireModelPropertyAsParam(boolean autowireModelPropertyAsParam) {
        this.autowireModelPropertyAsParam = autowireModelPropertyAsParam;
    }
    public TypeFinder getModelTypeFinder() {
        return modelTypeFinder;
    }
    public void setModelTypeFinder(TypeFinder modelTypeFinder) {
        this.modelTypeFinder = modelTypeFinder;
    }
    public String getCommand() {
        if(StringUtils.isEmpty(command)){
            command = commandName();
        }
        if(StringUtils.isEmpty(command)){
            throw new RuntimeException("command name for schema "+beanName +" is empty!");
        }
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public String getSchemaName() {
		return command+"schema";
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public List<MandatoryParamGroup> getMandatorys() {
		return mandatorys;
	}

	public void setMandatorys(List<MandatoryParamGroup> mandatorys) {
		this.mandatorys = mandatorys;
	}

	public OptionalParamGroup getOptionals() {
		return optionals;
	}

	public void setOptionals(OptionalParamGroup optionals) {
		this.optionals = optionals;
	}

	/**
	 * Contains optional socialparam types
	 * @author chiqiu
	 *
	 */
	public static class OptionalParamGroup{
		protected List<CommandParam> optionalParamList = new ArrayList<CommandParam>();

		public List<CommandParam> getOptionalParamList() {
			return optionalParamList;
		}

		public void setOptionalParamList(List<CommandParam> optionalParamList) {
			this.optionalParamList = optionalParamList;
		}
		public void clear(){
		    optionalParamList = new ArrayList<CommandParam>();
		}
	}
	
	/**
	 * A mandatory group may contain several params or another mandatory group, one of them should be valid against the incoming social request.
	 * This class is useful when you have alternative mandatory params.<br>
	 * valid if:<br>
	 * if one param in paramList is valid<br>
	 * or anotherGroup is valid<br>
	 * note that paramList should never be empty. Or an exception will be thrown.
	 * for example we have a,b,c,d four params, if a request is valid if A exists or BCD all exists then a mandatory group is like:<br>
	 * MandatoryGroup[paramlist(a),MandatoryGroup(paramlist(bcd),null)]<br>
	 * if a request is valid if ab exists or cd exists  then a mandatory group is like:<br>
	 * MandatoryGroup[paramlist(ab),MandatoryGroup(paramlist(cd),null)]<br>
	 *  
	 * @author chiqiu
	 *
	 */
	public static class MandatoryParamGroup{
		protected MandatoryParamGroup anotherGroup;
		
		protected List<CommandParam> paramList = new ArrayList<CommandParam>();
		public MandatoryParamGroup getAnotherGroup() {
			return anotherGroup;
		}
		public void setAnotherGroup(MandatoryParamGroup anotherGroup) {
			this.anotherGroup = anotherGroup;
		}
		public List<CommandParam> getParamList() {
			return paramList;
		}
		@Required
		public void setParamList(List<CommandParam> paramList) {
			this.paramList = paramList;
		}
		
		@Override
		public String toString(){
			StringBuffer sb = new StringBuffer("(");
			if(null!= paramList){
				for(CommandParam param : paramList){
					sb.append(param.getName());
					sb.append(" ");
				}
			}
			if(null != anotherGroup){
				sb.append(anotherGroup.toString());
			}
			sb.append(")");
			return sb.toString();
		}
		/**
		 * return true if one param in paramList exists in the request
		 * @param request
		 * @return
		 */
		public boolean isValidAccordingToParamList(CommandRequest request) throws ParamException{
			boolean res = false;
			for(CommandParam param : paramList){
				if(RequestUtils.isParamExist(param, request)){
					res = true;// do not break here,let's traverse all params in the config file, there won't be many params,just check all of them
				}
			}
			if (!res) {
			    throw new ParamException(BackendRuntimeException.BAD_REQUEST,"Mandatory Param error or missing: one of "+getParamListString()+ " must be specified");
            }
			return res;
		}
		
		protected String paramListString = null;
		
		public String getParamListString(){
		    if(paramListString == null){
		        StringBuffer sb = new StringBuffer("");
		        for(CommandParam param:paramList){
		            sb.append(param.getName());
		            sb.append(" ");
		        }
		        paramListString = sb.toString();
		    }
		    return paramListString;
		}
		
		/**
		 * return true if another group is valid
		 * @param request
		 * @return
		 */
		public boolean isValidAccordingToAnotherGroup(CommandRequest request) throws ParamException{
			if(null == anotherGroup){
				return false;
			}else{
				return anotherGroup.isValidRequest(request);
			}
		}
		/**
		 * validate the request , the group's parm list and the other group's are checked against
		 * @param request
		 * @return
		 */
		public boolean isValidRequest(CommandRequest request) throws ParamException{
			return isValidAccordingToParamList(request) || isValidAccordingToAnotherGroup(request); 
		}
		
		public void clear(){
		    paramList = new ArrayList<CommandParam>();
		    anotherGroup = null;
		}
	}
	/**
	 * validate request using cofigured paramgroup value
	 * @param paramGroups
	 * @param request
	 * @return
	 * @throws ParamException
	 */
	public boolean validateMandatoryParamGroups(CommandRequest request) throws ParamException{
		boolean res = true;
		if(mandatorys!=null){
		    for(MandatoryParamGroup mandatory : mandatorys){
	            if(!mandatory.isValidRequest(request)){
	                res = false;
	            }
	        }
		}
		return res;
	}
	
	/**
	 * 
	 * @param paramGroups
	 * @param request
	 * @return
	 * @throws ParamException
	 */
	public boolean validateOptionalParamGroup(CommandRequest request) throws ParamException{
		boolean res = true;
		
		if(optionals==null|| optionals.optionalParamList==null){
			
		}else{
			for(CommandParam param: optionals.optionalParamList){
				if(!RequestUtils.isParamTypeCorrect(param, request)){
					res = false;
					
				}
			}
		}
			
		return res;
	}
	
	/**
	 * validate the request params according to this schema
	 * @param request
	 * @return
	 * @throws BackendRuntimeException
	 */
	public boolean isValidRequest(CommandRequest request) throws ParamException{
		if(!isConfigChecked()){
			checkConfig();
		}
		return validateMandatoryParamGroups(request) && validateOptionalParamGroup(request);
	}
	
	protected Set<CommandParam> paramNames = null;
	protected Set<String> paramStrings = null;
	
	/**
	 * get all param names configured in this schema
	 * @return
	 */
    public Set<CommandParam> getParamNames() {
        if(paramNames == null){
            paramNames = new TreeSet<CommandParam>();
            getAllParamNames(paramNames);
        }
        return paramNames;
    }
    
    public boolean containsParam(String name){
        if(paramStrings == null){
            Set<CommandParam> allParams = getParamNames();
            for(CommandParam param:allParams){
                paramStrings.add(param.getName());
            }
        }
        return paramStrings.contains(name);
    }
    
    public static StringParam ShowInfo = new StringParam("showInfo",null);
    
    protected Set<CommandParam> createReservedParams(){
        TreeSet<CommandParam> params = new TreeSet<CommandParam>();
        params.add(ShowInfo);
        return params;
    }
    
    public void getAllParamNames(Set<CommandParam> result){
        if(optionals!=null){
            for(CommandParam param : optionals.optionalParamList){
                result.add(param);
            }
        }
        getMadantoryParamNames(result);
        
        if(isAutowireModelPropertyAsParam()){// check autowire model property setting
            if(StringUtils.isEmpty(modelName)){
                throw new RuntimeException(schemaName + ":modelName is not set when AutowireModelPropertyAsParam is set");
            }
            if(null == modelTypeFinder){
                throw new RuntimeException(schemaName + ": modelTypeFinder is null, it must be set if isAutowireModelPropertyAsParam is true");
            }
            Class<?> clazz = modelTypeFinder.getModelClass(modelName);
            List<CommandParam> modelClassParams = createParamsByModelClassFields(clazz);
            for(CommandParam param :modelClassParams){
                result.add(param);
            }
        }
        for(CommandParam param:createReservedParams()){
            result.add(param);
        }
        
    }
    
    public List<CommandParam> createParamsByModelClassFields(Class<?> clazz){
        Field[] fields = ModelReflectionUtil.getModelFields(clazz);
        List<CommandParam> result = new ArrayList<CommandParam>();
        for(Field f:fields){
            CommandParam param = ParamFactory.createParam(f);
            result.add(param);
        }
        return result;
    }
    
    public void getMadantoryParamNames(Set<CommandParam> result){
        if(mandatorys!=null){
            for(MandatoryParamGroup mGroup:mandatorys){
                getMadantoryParamNames(result,mGroup);
            }
        }
    }
    
    public void getMadantoryParamNames(Set<CommandParam> result,MandatoryParamGroup group){
        if(group!=null){
            for(CommandParam p: group.paramList){
                result.add(p);
            }
            if(group.anotherGroup!=null){
                getMadantoryParamNames(result,group.anotherGroup);
            }else {
                return;
            }
        }
    }
    
}
