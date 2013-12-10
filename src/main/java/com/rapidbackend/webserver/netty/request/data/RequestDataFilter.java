package com.rapidbackend.webserver.netty.request.data;

import com.rapidbackend.webserver.netty.request.RequestData;
/**
 * Filters used to filter received http data,useful in hadle upload img, files....
 * @author chiqiu
 *
 */
public interface RequestDataFilter {
    /**
     * prehandle request data we receive 
     * @param requestData
     * @throws Exception
     */
    public void filter(RequestData requestData) throws Exception;
    
    
    /**
     * renew the requestbody after the requstdata is modified
     * @param requestData
     * @throws Exception
     */
    //public void renewRequestBody(RequestData requestData) throws Exception;
}
