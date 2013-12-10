package com.rapidbackend.core.request;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Params which build up a request
 * @author chiqiu
 *
 */
public abstract class CommandParam implements Comparable<CommandParam>,BeanNameAware{
	/**
	 * name is the actual param name used in a request, if not set,
	 * the bean name will be used as param name
	 */
	protected String name;
	
	/**
	 * get the actual param name used in a request
	 * @return the actual param name used in a request, if not set,
     * the bean name will be used as param name
	 */
	public String getName() {
		if(StringUtils.isEmpty(name)){
		    return beanName;
		}else {
            return name;
        }
	}
	/**
	 * set the actual param name used in a request
	 * @param name 
	 */
	//@Required
	public void setName(String name) {
		this.name = name;
	}

	public static enum ParamDataType{
		Long,String,Int,IntList,StringList,Float,File,Boolean
	}
	/**
	 * 
	 * @return
	 */
	public abstract ParamDataType getParamDataType();
	
	public abstract Object getData();
	
	public abstract Class<?> getDataClass();
	
	@Override
	public int compareTo(CommandParam another){
	    return this.getName().compareTo(another.getName());
	}
	
	@Override
	public boolean equals(Object obj){
	    if(obj instanceof CommandParam){
	        CommandParam param = (CommandParam) obj;
	        return param.name.equals(this.name) && param.getParamDataType() == this.getParamDataType();
	    }else {
            return false;
        }
	}
	@Override
	public String toString(){
	    return getName()+"="+getData();
	}
	protected String beanName;
	@Override
	public void setBeanName(String name){
	    this.beanName = name;
	}
	
	public String getBeanName() {
        return beanName;
    }
    @Override
	public int hashCode(){
	    return this.name.hashCode()+this.getClass().hashCode();
	}
	
	private boolean skipInSchemaGenerate;

    public boolean isSkipInSchemaGenerate() {
        return skipInSchemaGenerate;
    }
    /**
     * Set this to true will comment out this param in request param generation because a param with the same name and type has already been generated
     * @param skipInSchemaGenerate
     */
    public void setSkipInSchemaGenerate(boolean skipInSchemaGenerate) {
        this.skipInSchemaGenerate = skipInSchemaGenerate;
    }
	
	
}
