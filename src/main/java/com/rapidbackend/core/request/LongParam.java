package com.rapidbackend.core.request;

public class LongParam extends CommandParam{
	protected Long value;
	
	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public LongParam(String paramName,Long val){
		value = val;
		name = paramName;
	}
	
	public LongParam(){
		
	}

	@Override
	public ParamDataType getParamDataType(){
		return ParamDataType.Long;
	}
	
	@Override
	public Long getData(){
		return value;
	}
	
	@Override
    public Class<?> getDataClass(){
        return Long.class;
    }
}
