package com.rapidbackend.core.request;

import java.io.File;
/**
 * A File param type which usually used on client side
 * @author chiqiu
 *
 */
public class FileParam extends CommandParam{
    protected File value;
    public FileParam(String paramName,File paramValue){
        value = paramValue;
        name = paramName;
    }
    @Override
    public ParamDataType getParamDataType(){
        return ParamDataType.File;
    }
    
    @Override
    public File getData(){
        return value;
    }
    
    public Class<?> getDataClass(){
        return File.class;
    }
}
