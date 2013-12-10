package com.rapidbackend.webserver.netty.handler;

import static org.jboss.netty.channel.Channels.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CACHE_CONTROL;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.DATE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.EXPIRES;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.LAST_MODIFIED;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.webserver.netty.NettyConf;
import com.rapidbackend.webserver.netty.NettyHttpServer;
import com.rapidbackend.webserver.netty.request.MethodValidator;
import com.rapidbackend.webserver.netty.request.RequestData;
import com.rapidbackend.webserver.netty.request.RequestInterceptor;

/**
 * The default implementation only supports GET and POST method,
 * change interceptors and method messageReceived() if you need to support more methods
 * @author chiqiu
 */
public abstract class HttpHandlerBase extends SimpleChannelUpstreamHandler {
    protected static final Logger logger = LoggerFactory.getLogger(HttpHandlerBase.class);
    private static final ChannelBuffer CONTINUE = ChannelBuffers.copiedBuffer(
            "HTTP/1.1 100 Continue\r\n\r\n", CharsetUtil.US_ASCII);
    protected ExecutorService executor;
    protected String serverName;
    protected NettyConf nettyConf;
    protected boolean readingChunks;
    protected HttpPostRequestDecoder postDecoder;
    protected RequestData requestData ;
    protected RequestInterceptor[] interceptors ={new MethodValidator()};
    public RequestInterceptor[] getInterceptors() {
        return interceptors;
    }
    public void setInterceptors(RequestInterceptor[] interceptors) {
        this.interceptors = interceptors;
    }
    public HttpPostRequestDecoder getPostDecoder() {
        return postDecoder;
    }
    public void setPostDecoder(HttpPostRequestDecoder postDecoder) {
        this.postDecoder = postDecoder;
    }
    public ExecutorService getExecutor() {
        return executor;
    }
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public NettyConf getNettyConf() {
        return nettyConf;
    }
    @Required
    public void setNettyConf(NettyConf nettyConf) {
        this.nettyConf = nettyConf;
    }
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception{
        Object msg = e.getMessage();
        if(msg instanceof HttpMessage){
            HttpMessage m = (HttpMessage) msg;
            if (is100ContinueExpected(m)) {// handle 100-continue
                write(ctx, succeededFuture(ctx.getChannel()), CONTINUE.duplicate());
            }
        }
        
        if(!readingChunks){
            if(msg instanceof HttpRequest){
                HttpRequest request = (HttpRequest)msg;
                for(RequestInterceptor interceptor : interceptors){// prepare to handle it
                    interceptor.intercept(request);
                }
                /**
                 * create the request data
                 */
                requestData = new RequestData(request);
                requestData.setRequestIp(ctx.getChannel().getRemoteAddress().toString());
                
                if(request.getMethod().getName().equalsIgnoreCase(HttpMethod.GET.getName())){
                    extractHttpGetData(request);
                }
                if(request.getMethod().getName().equalsIgnoreCase(HttpMethod.DELETE.getName())){
                    extractHttpDeleteData(request);
                }
                if(request.getMethod().getName().equalsIgnoreCase(HttpMethod.POST.getName())){
                    postDecoder = new HttpPostRequestDecoder(request);
                }
                if(request.getMethod().getName().equalsIgnoreCase(HttpMethod.PUT.getName())){
                    postDecoder = new HttpPostRequestDecoder(request);
                }
                
                
                if (request.isChunked()) {
                    readingChunks = true;
                }else {
                    if(!requestData.isReady()){// pass if the request is a GET
                        extractHttpPostData();
                    }
                }
            }
        }else {
            HttpChunk chunk = (HttpChunk) msg;
            postDecoder.offer(chunk);
            if(chunk.isLast()){
                extractHttpPostData();
                readingChunks = false;
            }
        }
        if((msg instanceof HttpRequest)|| (msg instanceof HttpChunk)){
            if(requestData.isReady()){
                //TODO change it back: 
                if(nettyConf.isForkHandlerThread()){
                    executor.execute(createTask(requestData, ctx,nettyConf));
                }else {
                    createTask(requestData, ctx,nettyConf).run();
                }
                
            }else {
                super.messageReceived(ctx, e);// TODO WARNING ! test this line 
            }
        }
    }
    
    /**
     * Creates an runnable task which actually does all the stuffs
     * @param request
     * @param ctx
     * @param config
     * @return
     */
    public abstract Runnable createTask(RequestData requestData,ChannelHandlerContext ctx,final NettyConf config);
    
    
    /**
     * get the params string from the uri, pass it to backend as the command requestbody
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getHttpGetParams(HttpRequest request) throws UnsupportedEncodingException{
        String requestBody = null;
        if(!StringUtils.isEmpty(request.getUri())){
            int pos = request.getUri().indexOf('?');
            if(pos>0){
                //requestBody = URLDecoder.decode(request.getUri().substring(pos+1),"UTF-8");
                requestBody = request.getUri().substring(pos+1);// decode later in queryparams
                //logger.debug("requestBody:"+requestBody);
            }
        }
        return requestBody;
    }
    /*
    public static String getHttpPostParams(RequestData requestData) throws UnsupportedEncodingException{
        StringBuilder sb = new StringBuilder();
        List<InterfaceHttpData> datas = requestData.getPostDatas();
        for (InterfaceHttpData data:datas) {
            
        }
        return sb.toString();
    }*/
    
    
    
    protected void writeResponse(RequestData requestData,ChannelHandlerContext ctx,String data) throws Exception{
        byte[] content = httpResponseContent(requestData.getRequest(), data);
        logger.debug("content length after transferring to bytes: "+content.length);
        HttpResponse response = httpResponse(requestData.getRequest(), content,HttpResponseStatus.OK);
        writeHTTPResponse(ctx,requestData.getRequest(),response);
    }
    
    protected void writeInternalError(RequestData requestData,ChannelHandlerContext ctx,String data)throws Exception{
        byte[] content = httpResponseContent(requestData.getRequest(), data);
        HttpResponse response = httpResponse(requestData.getRequest(), content,HttpResponseStatus.INTERNAL_SERVER_ERROR);
        writeHTTPResponse(ctx,requestData.getRequest(),response);
    }
    
    protected void writeBadRequestError(RequestData requestData,ChannelHandlerContext ctx,String data)throws Exception{
        byte[] content = httpResponseContent(requestData.getRequest(), data);
        
        HttpResponse response = httpResponse(requestData.getRequest(), content,HttpResponseStatus.BAD_REQUEST);
        writeHTTPResponse(ctx,requestData.getRequest(),response);
    }
    
    protected byte[] httpResponseContent(HttpRequest request,String data) throws Exception{
        if(logger.isDebugEnabled()){
            logger.debug(URLDecoder.decode(request.getUri(),"utf-8"));
            logger.debug(request.getContent().toString(CharsetUtil.UTF_8));
        }
        logger.debug("content length before transfer to bytes: "+data.length());
        logger.debug("content before transfer to bytes:" +data);
        return data.getBytes(CharsetUtil.UTF_8);
    }
    
    protected HttpResponse httpResponse(HttpRequest request, byte[] responseContent,HttpResponseStatus httpResponseStatus){
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1,
                httpResponseStatus);
        ChannelBuffer buf = ChannelBuffers.copiedBuffer(responseContent);
        setCookies(request,response);
        response.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(buf.readableBytes()));
        response.setHeader(HttpHeaders.Names.CONTENT_TYPE,"text/plain; charset=UTF-8");
        response.setContent(buf);
        return response;
    }
    /**
     * subclass should override this method
     * @param request
     * @param response
     */
    protected void setCookies(HttpRequest request,HttpResponse response){
        /*CookieEncoder cookieEncoder = new CookieEncoder(true);
        cookieEncoder.addCookie("whois","this");
        response.setHeader("Cookie", cookieEncoder.encode());
        */
    }
    
    protected void writeHTTPResponse(ChannelHandlerContext ctx,HttpRequest request,HttpResponse response){
        boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(request
                .getHeader(HttpHeaders.Names.CONNECTION)) ||
                request.getProtocolVersion().equals(HttpVersion.HTTP_1_0) &&
                !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(request
                        .getHeader(HttpHeaders.Names.CONNECTION));
        ChannelFuture future = ctx.getChannel().write(response);
        if(logger.isDebugEnabled()){
            logger.debug(ctx.getChannel().toString());
        }
        if(close){// only close it when client requests. Otherwise, keep it until we reach the timeout.
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    protected void extractHttpDeleteData(HttpRequest request) throws Exception{
        extractHttpGetData(request);
    }
    protected void extractHttpGetData(HttpRequest request) throws Exception{
        String requestBody = null;
        extractCookies(request);
        requestBody = getHttpGetParams(request);
        requestData.setRequestBody(requestBody);
        requestData.setReady(true);
    }
    protected void extractHttpPutData() throws Exception{
        extractHttpPostData();
    }
    protected void extractHttpPostData() throws Exception{
        List<InterfaceHttpData> datas = postDecoder.getBodyHttpDatas();
        requestData.setPostDatas(datas);
        requestData.setReady(true);
    }
    
    protected void extractCookies(HttpRequest request){
        Set<Cookie> cookies;
        String value = request.getHeader(HttpHeaders.Names.COOKIE);
        if (value == null) {
            cookies = Collections.emptySet();
        } else {
            CookieDecoder decoder = new CookieDecoder();
            cookies = decoder.decode(value);
        }
        requestData.setCookies(cookies);
    }
    
    @Override
    public void channelOpen(
            ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        NettyHttpServer.getAllchannels().add(e.getChannel());
        ctx.sendUpstream(e);
    }
    
    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    
    protected static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.setContent(ChannelBuffers.copiedBuffer(
                "Failure: " + status.toString() + "\r\n",
                CharsetUtil.UTF_8));

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * When file timestamp is the same as what the browser is sending up, send a "304 Not Modified"
     *
     * @param ctx
     *            Context
     */
    protected static void sendNotModified(ChannelHandlerContext ctx) {
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    /**
     * Sets the Date header for the HTTP response
     *
     * @param response
     *            HTTP response
     */
    protected static void setDateHeader(HttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.setHeader(DATE, dateFormatter.format(time.getTime()));
    }
    
    protected static void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.setHeader(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.setHeader(EXPIRES, dateFormatter.format(time.getTime()));
        response.setHeader(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.setHeader(
                LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param file
     *            file to extract content type
     */
    protected static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.setHeader(CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }
}
