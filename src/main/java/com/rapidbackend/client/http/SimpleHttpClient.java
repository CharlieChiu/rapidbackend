package com.rapidbackend.client.http;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.util.io.CharsetUtil;


/**
 * This client is more close to a web browser than netty's client framework
 * @author chiqiu
 * TODO add spring config support,add custome header injector
 */
public class SimpleHttpClient extends AppContextAware{
    private HttpParams params;
    private SchemeRegistry schemeRegistry;
    private ClientConnectionManager cm;
    private DefaultHttpClient httpClient;
    private static Logger logger = LoggerFactory.getLogger(SimpleHttpClient.class);
    
    public SimpleHttpClient() throws Exception{
        HttpClientConf conf = (HttpClientConf)getApplicationContext().getBean("HttpClientConf");
        
        this.params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
                
        HttpConnectionParams.setSoTimeout(params, conf.getSocketTimeout());
        HttpConnectionParams.setSocketBufferSize(params, conf.getSocketBufferSize());
        HttpConnectionParams.setConnectionTimeout(params, conf.getConnectionTimeout());
        params.setBooleanParameter(CookieSpecPNames.SINGLE_COOKIE_HEADER, true);
        //don need to set the BROWSER_COMPATIBILITY, it is set by the defaultHttpClient
        SSLContext ctx = SSLContext.getInstance("TLS");
        //HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        //HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
        
        X509TrustManager tm = new X509TrustManager() {
            
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                // TODO Auto-generated method stub
                return null;
            }
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                // TODO Auto-generated method stub   
            }
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                // TODO Auto-generated method stub
            }
        };
        ctx.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory sslSocketFactory = new SSLSocketFactory(ctx);
        this.schemeRegistry = new SchemeRegistry();
        this.schemeRegistry.register(
            new Scheme("http", conf.getHttpPort(),PlainSocketFactory.getSocketFactory()));
        this.schemeRegistry.register(
            new Scheme("https", conf.getSslPort(),sslSocketFactory));
        this.cm = new ThreadSafeClientConnManager(this.schemeRegistry);
        this.httpClient = new DefaultHttpClient(this.cm, this.params);
        BasicCookieStore cookieStore = new BasicCookieStore();
        httpClient.setCookieStore(cookieStore);
        /**
         * diable retry
         */
        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
    }
    public DefaultHttpClient gethttpClient(){
        return this.httpClient;
    }
    public static boolean isGzipped(Header header){
        boolean result = false;
        if(null == header){
            return result;
        }
        String value = header.getValue();
        value.toLowerCase();
        if(value.indexOf("gzip")>=0){
            result = true;
        }
        return result;
    }
    protected HttpResponse executeCommand(HttpUriRequest requestCommand) throws Exception{
        return gethttpClient().execute(requestCommand, new BasicHttpContext());
    }
    
    
    
    public String getCommandResult(HttpUriRequest requestCommand) throws Exception{
        HttpResponse response = executeCommand(requestCommand);
        return getResponseAsString(response);
    }
    
    public HttpResponse getCommandHttpResponse(HttpUriRequest requestCommand) throws Exception{
        return executeCommand(requestCommand);
    }
    
    public static String getResponseAsString(HttpResponse res) throws IOException{
        HttpEntity entity = res.getEntity();
        long contentLength = entity.getContentLength();
        logger.debug("content length receieved is: "+contentLength);
        Header contentEncoding = entity.getContentEncoding();
        InputStream inStream = null;
        if(contentLength>0){//determin whether the image is gzipped
            if(isGzipped(contentEncoding)){
                inStream = new GZIPInputStream(entity.getContent());
            }else{
                inStream = entity.getContent();
            }
        }
        inStream = new DataInputStream(inStream);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2*1024];
        int bufferFilled = 0;
        while((bufferFilled = inStream.read(buffer, 0, buffer.length))!=-1){
            arrayOutputStream.write(buffer, 0, bufferFilled);
        }
        byte[] inputdatabytes = arrayOutputStream.toByteArray();
        String result = new String(inputdatabytes, CharsetUtil.UTF8);
        return result;
    }
    
    public  void getStaticFile(HttpGet fileRequest, File writeTo) throws Exception{
        HttpResponse response = executeCommand(fileRequest);
        HttpEntity entity = response.getEntity();
        int code = response.getStatusLine().getStatusCode();
        if(code != 200){
            throw new RuntimeException("get file failed :"+fileRequest.toString());
        }else {
            long contentLength = entity.getContentLength();
            Header contentEncoding = entity.getContentEncoding();
            InputStream inStream = null;
            if(contentLength>0){//determin whether the image is gzipped
                if(isGzipped(contentEncoding)){
                    inStream = new GZIPInputStream(entity.getContent());
                }else{
                    inStream = entity.getContent();
                }
            }
            byte[] buffer = new byte[2*1024];
            DataInputStream in = new DataInputStream(inStream);
            FileOutputStream outputStream = new FileOutputStream(writeTo);
            int bufferFilled = 0;
            while((bufferFilled = in.read(buffer, 0, buffer.length))!=-1){
                outputStream.write(buffer, 0,bufferFilled);
            }
            outputStream.flush();
            outputStream.close();
        }
    }
    
    public static class Connector implements Runnable{
        protected DefaultHttpClient client;
        protected HttpUriRequest request;
        protected HttpContext httpContext;
        public Connector(DefaultHttpClient httpClient,HttpUriRequest request, HttpContext httpContext){
            this.client = httpClient;
            this.request = request;
            this.httpContext = httpContext;
        }
        
        public void run(){
            try {
                getAndPrint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void getAndPrint() throws Exception{
            
        }
    }
    public static void main(String[] args) throws Exception{
        /*SimpleHttpClient client = new SimpleHttpClient();
        
        Connector connector = new Connector(client.gethttpClient());
        connector.run();*/
    }
}
