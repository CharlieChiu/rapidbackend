package com.rapidbackend.core.request;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.rapidbackend.util.general.ConversionUtils;

/**
 * 
 * @author chiqiu
 *
 */
public class IntListParam extends SeqCommandParam{
    protected int[] values;
    
    public int[] getValues() {
        return values;
    }
    public void setValues(int[] values) {
        this.values = values;
    }
    public IntListParam(String paramName,int[] paramValue){
        values = paramValue;
        name = paramName;
    }
    public IntListParam(){
        
    }
    @Override
    public ParamDataType getParamDataType(){
        return ParamDataType.IntList;
    }
    
    @Override
    public int[] getData(){
        return values;
    }
    @Override
    public String toString(){
        if (values!=null && values.length>0) {
            String sepr = "&"+name+"=";
            return name+"="+ConversionUtils.join(values, sepr);
        }else {
            return "";
        }
    }
    @Override
    public String toStringEncoded() throws UnsupportedEncodingException{
        String encodedName = URLEncoder.encode(name,"UTF-8");
        if (values!=null && values.length>0) {
            String sepr = "&"+encodedName+"=";
            return encodedName+"="+ConversionUtils.join(values, sepr);
        }else {
            return "";
        }
    }
    @Override
    public Class<?> getDataClass(){
        return int[].class;
    }
}
