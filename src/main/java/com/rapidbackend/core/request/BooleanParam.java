package com.rapidbackend.core.request;


public class BooleanParam extends CommandParam{
    
    private Boolean value;
    
    public BooleanParam(String name,Boolean value){
        this.value = value;
        this.name = name;
    }
    
    
    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
    
    public BooleanParam(){
        
    }
    @Override
    public ParamDataType getParamDataType(){
        return ParamDataType.Boolean;
    }
    
    @Override
    public Boolean getData(){
        return value;
    }
    
    @Override
    public Class<?> getDataClass(){
        return Boolean.class;
    }
}
