package com.rapidbackend.core.request;

/**
 * @author chiqiu
 *
 */
public class IntParam extends CommandParam{
	
	protected Integer value;
	
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	public IntParam(String paramName,Integer paramValue){
		value = paramValue;
		name = paramName;
	}
	public IntParam(){
		
	}
	@Override
	public ParamDataType getParamDataType(){
		return ParamDataType.Int;
	}
	
	@Override
	public Integer getData(){
		return value;
	}
	@Override
    public Class<?> getDataClass(){
        return Integer.class;
    }
}
