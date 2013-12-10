package com.rapidbackend.client.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.request.FileParam;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.ParamFactory;
import com.rapidbackend.core.request.SeqCommandParam;
import com.rapidbackend.socialutil.model.extension.FeedContext;
import com.rapidbackend.util.io.JsonUtil;
/**
 * helper class to create httpclient  http requests
 * 
 * TODO this class is now used in test only, will be re-implemented for HttpUrlConnection
 * @author chiqiu
 */
public class HttpCommandHelper {
    
    public static String LocalHost = "localhost";
    
    static Logger logger = LoggerFactory.getLogger(HttpCommandHelper.class);
        
    /**
     * create an httpget request
     * @param host
     * @param command
     * @param params
     * @return
     */
    public static HttpGet createHttpGet(WebProtocol protocol,String host,String path,List<CommandParam> params){
        assertUriAndHost(host,path);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol.name()).append("://").append(host).append("/").append(path);
        String urlparams = createGetUri(params);
        if(!StringUtils.isEmpty(urlparams)){
            sb.append("?");
            sb.append(urlparams);
        }
        
        HttpGet request = new HttpGet(sb.toString());
        return request;
    }
    
    
    public static HttpGet createHttpGet(WebProtocol protocol,String host,String path,CommandParam... params){
        assertUriAndHost(host,path);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol.name()).append("://").append(host).append("/").append(path);
        String urlparams = createGetUri(params);
        if (!StringUtils.isEmpty(urlparams)) {
            sb.append("?").append(urlparams);
        }
        
        HttpGet request = new HttpGet(sb.toString());
        return request;
    }
    
    public static HttpPost createHttpPostWithoutFile(WebProtocol protocol,String host,String path,List<CommandParam> params) throws UnsupportedEncodingException{
        assertUriAndHost(host,path);
        assertPostParam(params);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol.name()).append("://").append(host).append("/").append(path);
        HttpPost httpPost = new HttpPost(sb.toString());
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (CommandParam param: params) {
            nameValuePairs.add(new BasicNameValuePair(param.getName(), param.getData().toString()));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs,"UTF8");
        httpPost.setEntity(entity);
        return httpPost;
    }
    
    
    
    public static HttpPost createHttpPostWithoutFile(WebProtocol protocol,String host,String path,DbRecord model) throws UnsupportedEncodingException{
        List<CommandParam> params = ParamFactory.convertModelToParams(model);
        return createHttpPostWithoutFile(protocol, host, path, params);
    }
    
    public static HttpPost createHttpPostWithFile(WebProtocol protocol,String host,String path,List<CommandParam> params,FileParam fileParam) throws UnsupportedEncodingException{
        assertUriAndHost(host,path);
        assertPostParam(params,fileParam);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol.name()).append("://").append(host).append("/").append(path);
        HttpPost httpPost = new HttpPost(sb.toString());
        MultipartEntity entity = new MultipartEntity();
        for(CommandParam param: params){
            entity.addPart(param.getName(), new StringBody(param.getData().toString()));
        }
        FileBody media = new FileBody(fileParam.getData(),"binary/octet-stream");
        entity.addPart(fileParam.getName(),media);
        httpPost.setEntity(entity);
        return httpPost;
    }
    
    
    public static HttpPut createHttpPutWithFile(WebProtocol protocol,String host,String path,List<CommandParam> params,FileParam fileParam) throws UnsupportedEncodingException{
        assertUriAndHost(host,path);
        assertPostParam(params,fileParam);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol.name()).append("://").append(host).append("/").append(path);
        HttpPut httpput = new HttpPut(sb.toString());
        MultipartEntity entity = new MultipartEntity();
        for(CommandParam param: params){
            entity.addPart(param.getName(), new StringBody(param.getData().toString()));
        }
        FileBody media = new FileBody(fileParam.getData(),"binary/octet-stream");
        entity.addPart(fileParam.getName(),media);
        httpput.setEntity(entity);
        return httpput;
    }
    
    public static HttpPut createHttpPutWithoutFile(WebProtocol protocol,String host,String path,List<CommandParam> params) throws UnsupportedEncodingException{
        assertUriAndHost(host,path);
        assertPostParam(params);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol.name()).append("://").append(host).append("/").append(path);
        HttpPut httpPut = new HttpPut(sb.toString());
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (CommandParam param: params) {
            nameValuePairs.add(new BasicNameValuePair(param.getName(), param.getData().toString()));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
        httpPut.setEntity(entity);
        return httpPut;
    }
    
    public static HttpPut createHttpPutWithoutFile(WebProtocol protocol,String host,String path,DbRecord model) throws UnsupportedEncodingException{
        List<CommandParam> params = ParamFactory.convertModelToParams(model);
        return createHttpPutWithoutFile(protocol, host, path, params);
    }
    
    public static HttpDelete createHttpDelete(WebProtocol protocol,String host,String path,List<CommandParam> params){
        assertUriAndHost(host,path);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol.name()).append("://").append(host).append("/").append(path);
        String urlparams = createGetUri(params);
        if(!StringUtils.isEmpty(urlparams)){
            sb.append("?");
            sb.append(urlparams);
        }
        
        HttpDelete delete = new HttpDelete(sb.toString());
        return delete;
    }
    
    public static HttpDelete createHttpDelete(WebProtocol protocol,String host,String path,CommandParam... params){
        assertUriAndHost(host,path);
        StringBuilder sb = new StringBuilder();
        sb.append(protocol.name()).append("://").append(host).append("/").append(path);
        String urlparams = createGetUri(params);
        if (!StringUtils.isEmpty(urlparams)) {
            sb.append("?").append(urlparams);
        }
        
        HttpDelete delete = new HttpDelete(sb.toString());
        return delete;
    }
    protected static String empty = "";
    
    public static String createGetUri(List<CommandParam> params){
        if(params == null || params.size()==0){
            return empty;
        }
        CommandParam[] array = params.toArray(new CommandParam[0]);
        try {
            //return URLEncoder.encode(StringUtils.join(encodedParams(array), "&"),"UTF-8");
            return StringUtils.join(encodedParams(array), "&");
        } catch (UnsupportedEncodingException e) {
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"UnsupportedEncodingException in HttpCommandHelper createGetUrl",e);
        }
    }
    private static String[] encodedParams(CommandParam[] params) throws UnsupportedEncodingException{
        if(null == params || params.length ==0){
            return new String[0];
        }else {
            
            String[] result = new String[params.length];
            int i=0;
            for(CommandParam p:params){
                String val = null;
                if(p instanceof SeqCommandParam){
                    val = ((SeqCommandParam) p).toStringEncoded();
                }else {
                    val = URLEncoder.encode(p.getName(),"UTF-8")+"="+URLEncoder.encode(p.getData().toString(),"UTF-8");
                }
                result[i++] = val;
            }
            return result;
        }
        
    }
    
    public static String createGetUri(CommandParam[] params){
        if(params == null || params.length==0){
            return empty;
        }
        try {
            //return URLEncoder.encode(StringUtils.join(encodedParams(params), "&"),"UTF-8");
            return StringUtils.join(encodedParams(params), "&");
        } catch (UnsupportedEncodingException e) {
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"UnsupportedEncodingException in HttpCommandHelper createGetUrl",e);
        }
    }
    
    protected static void assertUriAndHost(String host,String uri){
        if(!(host!=null && 
                host.length()>0 && 
                !host.trim().equalsIgnoreCase(""))){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"request host is empty ");
        }
        if(!(uri!=null && 
                uri.length()>0 && 
                !uri.trim().equalsIgnoreCase(""))){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"request uri is empty ");
        }
    }
    
    protected static void assertPostParam(List<CommandParam> params){
        if(params == null || params.size() == 0){
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"post request has no params ");
        }
    }
    
    protected static void assertPostParam(List<CommandParam> params,FileParam fileParam){
        if((params == null || params.size() == 0)&&fileParam==null){// if no params will be posted
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"post request has no params ");
        }
    }
    
    public static <T> CommandResult<T> parseResult(String result, Class<T> clazz) throws Exception{
        result = URLDecoder.decode(result, "UTF-8");
        logger.debug(result);
        return JsonUtil.readObject(result, new TypeReference<CommandResult<T>>() {
        });
    }
    
    public static <T> CommandResult<?> parseListResult(String result, Class<T> listMemberClass) throws Exception{
        
        CommandResult commandResult = JsonUtil.readObject(result, new TypeReference<CommandResult>() {
        });
        List<?> objects = (List<?>)commandResult.getResult();
        if(objects != null && objects instanceof List){
            List<T> newList = new ArrayList<T>();
            
            for(Object pojo:objects){
                T converted = JsonUtil.getMapper().convertValue(pojo, listMemberClass);
                newList.add(converted);
            }
            if(newList.size()>0){
                commandResult.setResult(newList);
            }
        }
        return commandResult;
    }
    
    public static <T> CommandResult<?> parseTimelineResult(String result, Class<T> feedclass) throws Exception{
        CommandResult commandResult = parseListResult(result,FeedContext.class);
        List<FeedContext> objects = (List<FeedContext>)commandResult.getResult();
        if(objects != null && objects instanceof List){
                        
            for(FeedContext feedContext:objects){
                Object seedfeed = feedContext.getSeedFeed();
                Object feed = feedContext.getFeed();
                if(seedfeed != null){
                    T convertedSeedFeed = JsonUtil.getMapper().convertValue(seedfeed, feedclass);
                    feedContext.setSeedFeed(convertedSeedFeed);
                }
                if( feed !=null){
                    T convertedFeed = JsonUtil.getMapper().convertValue(feed, feedclass);
                    feedContext.setFeed(convertedFeed);
                }
                
            }
        }
        return commandResult;
    }
    
    
    public static enum WebProtocol{
        http,https
    }
}
