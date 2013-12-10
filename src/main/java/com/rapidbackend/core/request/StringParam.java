package com.rapidbackend.core.request;


public class StringParam extends CommandParam{
	
	protected String text;
	
	
	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}
	public StringParam(String paramname,String content){
		text = content;
		name = paramname;
	}
	
	public StringParam(){
		
	}
	
	@Override
	public ParamDataType getParamDataType(){
		return ParamDataType.String;
	}
	
	@Override
	public String getData(){
		return text;
	}
	@Override
    public Class<?> getDataClass(){
        return String.class;
    }
}
