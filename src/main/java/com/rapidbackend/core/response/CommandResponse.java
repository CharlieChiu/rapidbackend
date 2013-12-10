package com.rapidbackend.core.response;


import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;

/**
 * Response is the Class contains handling result and information of a request
 * @author chiqiu
 *
 */
public interface CommandResponse {
	
	public void setResult(Object o);
	/**
	 * get the first element of the resultItems list
	 * @return
	 */
	public Object getResult();
	public void setPagingInfo(PagingInfo pagingInfo);
	public PagingInfo getPagingInfo();
	
    public boolean isError() ;
	public void setError(boolean error) ;
	public BackendRuntimeException getException() ;
    public void setException(BackendRuntimeException exception);
    /**
     * 
     * @return the time of handling the request which is against this response
     */
	public long getElapsedTime() ;
	/**
	 * 
	 * @return the sessionId of this response , if any
	 */
	public String getSessionId();
	public void setSessionId(String id);
	/**
	 * 
	 * @param attributeName
	 * @return
	 */
	public Object getAddtionalInfo(String attributeName);
	/**
	 * 
	 * @param attributeName
	 * @param value
	 * @return
	 */
	public Object setAddtionalInfo(String attributeName,Object value);
}
