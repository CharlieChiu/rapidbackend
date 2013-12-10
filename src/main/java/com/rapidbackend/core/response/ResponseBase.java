package com.rapidbackend.core.response;

import java.util.HashMap;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.dao.BaseDao.PagingInfo;
import com.rapidbackend.util.time.SimpleTimer;

public class ResponseBase implements CommandResponse{
    protected String jsonResult;
    protected Object result = null;
    protected boolean error = false;
    protected BackendRuntimeException exception;
    protected SimpleTimer timer;
    protected String sessionId;
    protected PagingInfo pagingInfo;
    
    private HashMap<String, Object> additionalData = new HashMap<String, Object>(3);
    @Override
    public Object getAddtionalInfo(String attributeName){
        return additionalData.get(attributeName);
    }
    @Override
    public Object setAddtionalInfo(String attributeName,Object value){
        return additionalData.put(attributeName, value);
    }
    
    
    public PagingInfo getPagingInfo() {
        return pagingInfo;
    }
    public void setPagingInfo(PagingInfo pagingInfo) {
        this.pagingInfo = pagingInfo;
    }
    @Override
    public Object getResult() {
        return result;
    }
    @Override
    public void setResult(Object result) {
        this.result = result;
    }
    @Override
    public boolean isError() {
        return error;
    }
    @Override
    public void setError(boolean error) {
        this.error = error;
    }
    @Override
    public BackendRuntimeException getException() {
        return exception;
    }
    @Override
    public void setException(BackendRuntimeException exception) {
        this.exception = exception;
    }
    @Override
    public long getElapsedTime() {
        return timer.getIntervalMili();
    }
    public ResponseBase(){
        timer = new SimpleTimer();
    }
    @Override
    public String getSessionId() {
        return sessionId;
    }
    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
}
