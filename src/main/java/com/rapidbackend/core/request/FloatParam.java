package com.rapidbackend.core.request;


public class FloatParam extends CommandParam{
    
    protected Float value;
    
    public Float getValue() {
        return value;
    }
    public void setValue(Float value) {
        this.value = value;
    }
    public FloatParam(String paramName,Float paramValue){
        value = paramValue;
        name = paramName;
    }
    public FloatParam(){
        
    }
    @Override
    public ParamDataType getParamDataType(){
        return ParamDataType.Float;
    }
    
    @Override
    public Float getData(){
        return value;
    }
    
    public Class<?> getDataClass(){
        return Float.class;
    }
}
