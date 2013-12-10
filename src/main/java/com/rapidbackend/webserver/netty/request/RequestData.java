package com.rapidbackend.webserver.netty.request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;

import com.rapidbackend.core.BackendRuntimeException;
/**
 * 
 * @author chiqiu
 *
 */
public class RequestData {    
    
    protected HttpRequest request;
    protected String requestIp;
    
    protected HashMap<String, Object> data = new HashMap<String, Object>();
    
    protected HashMap<String, List<String>> params = new HashMap<String, List<String>>();
    /**
     * the actual requestbody which is submitted into the backend process.
     */
    protected String requestBody;
    protected boolean ready = false;
    
    public RequestData(HttpRequest request){
        this.request = request;
    }
    public HashMap<String, Object> getData() {
        return data;
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
    /**
     * @return
     */
    public String getRequestBody() {
        return requestBody;
    }
    /**
     * 
     * @param requestBody
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }
    
    public String getRequestIp() {
        return requestIp;
    }
    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }
    public boolean isReady() {
        return ready;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }
    public HttpRequest getRequest() {
        return request;
    }
    public void setRequest(HttpRequest request) {
        this.request = request;
    }
    public void setCookies(Set<Cookie> cookies){
        data.put("cookies", cookies);
    }
    
    public Set<Cookie> getCookies(){
        return (Set<Cookie>)data.get("cookies");
    }
    public void setPostDatas(List<InterfaceHttpData> datas){
        data.put("postData", datas);
    }
    public List<InterfaceHttpData> getPostDatas(){
        return (List<InterfaceHttpData>)data.get("postData");
    }
    
    @Deprecated
    public void addPostData(InterfaceHttpData httpData){/*
        List<InterfaceHttpData> datas = (List<InterfaceHttpData>)data.get("postData");
        boolean setdata=false;
        if(datas==null){
            datas = new ArrayList<InterfaceHttpData>();
            setdata = true;
        }
        datas.add(httpData);
        if (setdata) {
            setPostDatas(datas);
        }*/
    }
    
    public boolean hasPostData(){
        return getPostDatas()!=null && getPostDatas().size()!=0;
    }
    
    public boolean isEmpty(){
        boolean result = true;
        if(StringUtils.isEmpty(requestBody) 
                && !hasPostData()){
            
        }else {
            result = false;
        }
        return result;
    }
    
    public PostParameters getPostParameters(){
        try {
            PostParameters parameters = null;
            if(hasPostData()){
                parameters = new PostParameters(getPostDatas());
            }
            return parameters;
        } catch (IOException e) {
            throw new BackendRuntimeException(BackendRuntimeException.InternalServerError,"error parsing post data");
        }
        
    }
    
}
