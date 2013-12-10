package com.rapidbackend.core.request;

import com.rapidbackend.core.BackendRuntimeException;



/**
 * Utility class for requests
 * @author chiqiu
 *
 */
public abstract class RequestUtils {
	
	/**
	 * check if certain type of param exists in the request.Both param data and param type are checked.<br>
	 * Return false if param type does not match or param data is null.
	 * @param param
	 * @param request
	 * @return
	 */
	public static boolean isParamExist(CommandParam param , CommandRequest request){
		//Assert.notNull(param); we check this in each schema's checkConfig
		String name = param.getName();
		//Assert.notNull(name); we check this in each schema's checkConfig
		boolean res = false;
		CommandParam target = request.getRequestParams().getParam(name);
		if(target!=null && target.getParamDataType() == param.getParamDataType()
				&& target.getData()!=null){
			res = true;
		}
		return res;
	}
	/**
	 * Check the param type in request when the target param exists in request.
	 * @param param
	 * @param request
	 * @return true if param doesn't exist in request, <br>ture if the param exists in requst and type is correct. <br>false if param type is incorrect
	 * 
	 */
	public static boolean isParamTypeCorrect(CommandParam param , CommandRequest request) throws ParamException{
		//Assert.notNull(param); we check this in each schema's checkConfig
		String name = param.getName();
		//Assert.notNull(name); we check this in each schema's checkConfig
		CommandParam targetParam = request.getRequestParams().getParam(name);
		boolean res = true;
		if(targetParam!=null){
			if(targetParam.getParamDataType() != param.getParamDataType()){
				res = false;
				throw new ParamException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"Optional param data type error:"+param.getName());
			}
		}
		return res;
	}
	
}
