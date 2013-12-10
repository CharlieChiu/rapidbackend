package com.rapidbackend.core.request;

import java.util.Collection;
import java.util.HashMap;

/**
 * SocialParams impl using HashMap
 * @author chiqiu
 *
 */
public class ParamsBase extends Params{
	
	protected HashMap<String, CommandParam> paramMap = new HashMap<String, CommandParam>();
	@Override
	public CommandParam getParam(String paramName){
		return paramMap.get(paramName);
	}
	@Override
	public Params setParam(String name,CommandParam param){
		paramMap.put(name, param);
		return this;
	}
	@Override
	public String[] paramNames(){
		String[] result = (String[])paramMap.keySet().toArray();
		return result;
	}
	@Override
	public Collection<CommandParam> params(){
	    return paramMap.values();
	}
	@Override
	public String toString(){
	    StringBuffer sb = new StringBuffer("");
	    for(CommandParam param: paramMap.values()){
	        sb.append(param.toString());
	        sb.append("&");
	    }
	    return sb.toString();
	}
}
