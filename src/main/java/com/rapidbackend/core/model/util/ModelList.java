package com.rapidbackend.core.model.util;

import java.util.ArrayList;

/**
 * util class to represent a list of models and paging info
 * @author chiqiu
 *
 */
public class ModelList<T> extends ArrayList<T>{
    /**
     * 
     */
    private static final long serialVersionUID = 275188183824507010L;
    protected long count;
    protected long start;
    protected int limit;
    protected String additionalInfo;
    
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }
    public long getStart() {
        return start;
    }
    public void setStart(long start) {
        this.start = start;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    
}
