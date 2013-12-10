package com.rapidbackend.core.request;

import java.util.Collection;

import com.rapidbackend.core.BackendRuntimeException;

public abstract class Params {
	/**
	 * querytime , record how long a query takes
	 */
	public static final String QUERYTIME = "qt";
	/**
	 * used in paging , like mysql 
	 */
	public static final String START = "start";
	/**
	 * how many rows of result returned
	 */
	public static final String ROWS = "rows";
	/**
	 * used with start, like mysql, maxim of results returned
	 */
	public static final String LIMIT = "limit";
	/**
	 * rreturn all params names
	 * @return
	 */
	public abstract String[] paramNames();
	
	/**
	 * @return all the params
	 */
	public abstract Collection<CommandParam> params();
	
	public abstract CommandParam getParam(String paramName);
	
	/**
	 * 
	 * @param paramName
	 * @param param
	 * @return the socialparams itself
	 */
	public abstract Params setParam(String paramName,CommandParam param);
		
	protected boolean parseBool(String s) {
	    if( s != null ) {
	      if( s.startsWith("true") || s.startsWith("on") || s.startsWith("yes") ) {
	        return true;
	      }
	      if( s.startsWith("false") || s.startsWith("off") || s.equals("no") ) {
	        return false;
	      }
	    }
	    throw new BackendRuntimeException( BackendRuntimeException.INTERNAL_SERVER_ERROR, "invalid boolean value: "+s );
	}
	
	
}
