package com.rapidbackend.core.embedded;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.rapidbackend.core.BackendRuntimeException;

@JsonSerialize(include=Inclusion.NON_NULL)
public class CommandResult<T> {
    protected boolean error = false;
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
    protected T result = null;
    protected String errorMessage = null;
    protected String handleInfo = null;
    
    protected Integer start = null;
    protected Integer pageSize = null;
    protected Integer nextStart = null;
    //TODO protected Integer cursor;/// limit may be slow in some case cursor is more reasonable implementation, for now we just use start and page
    protected String sessionId = null;
    
    public String getSessionId() {
        return sessionId;
    }
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public boolean isError() {
        return error;
    }
    public void setError(boolean error) {
        this.error = error;
    }
    public T getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result = result;
    }
    
    public Integer getStart() {
        return start;
    }
    public void setStart(Integer start) {
        this.start = start;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public Integer getNextStart() {
        return nextStart;
    }
    public void setNextStart(Integer nextStart) {
        this.nextStart = nextStart;
    }
    public void setException(Exception exception) {
        if(exception instanceof BackendRuntimeException){
            BackendRuntimeException be = (BackendRuntimeException)exception;
            this.errorMessage = be.getDetailedInfo();
        }else {
            this.errorMessage = BackendRuntimeException.getDetailedInfo(exception);
        }
    }
    
    public String getHandleInfo() {
        return handleInfo;
    }
    public void setHandleInfo(String handleInfo) {
        this.handleInfo = handleInfo;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
