package com.rapidbackend.webserver.netty.handler;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.IF_MODIFIED_SINCE;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.METHOD_NOT_ALLOWED;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelFutureProgressListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.FileRegion;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedFile;
import org.jboss.netty.handler.timeout.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.webserver.netty.command.CommandDispatcher;
import com.rapidbackend.webserver.netty.NettyConf;
import com.rapidbackend.webserver.netty.request.RequestData;
import com.rapidbackend.webserver.netty.request.data.DefaultRequestDataFilter;
import com.rapidbackend.webserver.netty.request.data.RequestDataFilter;
import com.rapidbackend.webserver.netty.request.data.RequestDataFilterFactory;
import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.command.DefaultCommand;
import com.rapidbackend.core.command.UnsupportCommandException;
import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.embedded.EmbeddedApi;
import com.rapidbackend.core.embedded.EmbeddedApi.RawCommand;
import com.rapidbackend.core.request.ParamException;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.util.io.JsonUtil;

/**
 * @author chiqiu
 */
public class DefaultHttpHandler extends HttpHandlerBase{
    protected CommandDispatcher commandDispatcher;
    protected RequestDataFilterFactory requestDataFilterFactory;
    protected RequestDataFilter requestDataFilter = new DefaultRequestDataFilter();
    
    public RequestDataFilterFactory getRequestDataFilterFactory() {
        return requestDataFilterFactory;
    }
    @Required
    public void setRequestDataFilterFactory(
            RequestDataFilterFactory requestDataFilterFactory) {
        this.requestDataFilterFactory = requestDataFilterFactory;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }
    @Required
    public void setCommandDispatcher(CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }
    
    
    public RequestDataFilter getRequestDataFilter() {
        return requestDataFilter;
    }
    public void setRequestDataFilter(RequestDataFilter requestDataFilter) {
        this.requestDataFilter = requestDataFilter;
    }
    public DefaultHttpHandler(){
        super();
    }
    @Override
    public Runnable createTask(RequestData requestData,ChannelHandlerContext ctx,NettyConf config){
        
        String uri =  requestData.getRequest().getUri();
        uri = decodeUri(uri);
        
        Runnable task;
        if(null!=uri&&uri.startsWith(config.getStaticFileRequestUri())){
            task = new GetStaticFileTask(this, requestData, ctx, config);
        }else{
             task = new StandardTask(this,requestData, ctx, config, commandDispatcher);
        }
        
        return task;
    }
    
    public static String decodeUri(String uri){
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error("error in decodeUri "+e1);
            }
        }
        return uri;
    }
    
    public static class StandardTask implements Runnable{
        protected RequestData requestData;
        protected ChannelHandlerContext ctx;
        protected NettyConf config;
        protected CommandDispatcher commandDispatcher;
        protected DefaultHttpHandler handler;
        public StandardTask(DefaultHttpHandler handler,RequestData requestData,ChannelHandlerContext ctx,NettyConf config,CommandDispatcher commandDispatcher){
            this.handler = handler;
            this.requestData = requestData;
            this.ctx = ctx;
            this.config = config;
            this.commandDispatcher = commandDispatcher;
        }
        @Override
        public void run() {
            String requestInfo = "";
            if(requestData!=null && requestData.getRequest()!=null){
                requestInfo = requestData.getRequest().toString();
            }
            try {
                try {
                    CommandResult<?> commandResult = new CommandResult();
                    String uri = decodeUri(requestData.getRequest().getUri());
                    
                    RawCommand command = commandDispatcher.dispatchCommand(uri);
                    
                    RequestDataFilter datafilter = handler.getRequestDataFilterFactory().getFilterCache().get(command);
                    if(datafilter!=null){
                        datafilter.filter(requestData);
                    }else {
                        if(handler.getRequestDataFilter()!=null){
                            handler.getRequestDataFilter().filter(requestData);
                        }
                    }
                    
                    DefaultCommand defaultCommand = EmbeddedApi.handleCommand(command, requestData);
                    
                    EmbeddedApi.handleResult(commandResult, defaultCommand);
                    
                    writeResponse(commandResult,defaultCommand);
                    
                } catch (Exception e) {// try to write the unknown error to client
                    logger.error("error handle request : "+requestInfo,e);
                    CommandResult<?> failedResult = new CommandResult();
                    EmbeddedApi.handleException(failedResult, e);
                    String data = JsonUtil.writeObjectAsString(failedResult);
                    handler.writeInternalError(requestData, ctx, data);
                }
               
                
            } catch (Exception e) {
                logger.error("error happened during processing http request: "+requestInfo,e);
                throw new HandlerException("error happened during processing http request: "+requestInfo,e);// try to close channel
            }
        }
        
        public void writeResponse(CommandResult<?> commandResult,DefaultCommand defaultCommand) throws Exception{
            String data = JsonUtil.writeObjectAsString(commandResult);
            //data = URLEncoder.encode(data, "UTF-8");
            logger.debug("content length to send is: "+data.length());
            CommandResponse response = defaultCommand.getResponse();
            if(response!=null && response.isError()){
                BackendRuntimeException exception = response.getException();
                if(exception !=null){
                    if(exception instanceof ParamException
                            || exception instanceof UnsupportCommandException){
                        handler.writeBadRequestError(requestData, ctx, data);
                    }else {
                        handler.writeInternalError(requestData, ctx, data);
                    }
                    
                }
            }else {
                handler.writeResponse(requestData, ctx, data);
            }
        }
    }
    /**
     * class for writing static files to clients
     * @author chiqiu
     *
     */
    public static class GetStaticFileTask implements Runnable{
        
        protected static Logger logger = LoggerFactory.getLogger(GetStaticFileTask.class);
        
        protected RequestData requestData;
        protected ChannelHandlerContext ctx;
        protected NettyConf config;
        protected CommandDispatcher commandDispatcher;
        protected DefaultHttpHandler handler;
        public GetStaticFileTask(DefaultHttpHandler handler,RequestData requestData,ChannelHandlerContext ctx,NettyConf config){
            this.handler = handler;
            this.requestData = requestData;
            this.ctx = ctx;
            this.config = config;
        }
        @Override
        public void run(){
            try {
                
                try {
                    HttpRequest request = requestData.getRequest();
                    if (request.getMethod() != GET) {
                        sendError(ctx, METHOD_NOT_ALLOWED);
                        return;
                    }

                    final String path = getFilePath();
                    if (path == null) {
                        sendError(ctx, FORBIDDEN);
                        return;
                    }

                    File file = new File(path);
                    if (file.isHidden() || !file.exists()) {
                        sendError(ctx, NOT_FOUND);
                        return;
                    }
                    if (!file.isFile()) {
                        sendError(ctx, FORBIDDEN);
                        return;
                    }
                    
                    // Cache Validation
                    String ifModifiedSince = request.getHeader(IF_MODIFIED_SINCE);
                    if (ifModifiedSince != null && ifModifiedSince.length() != 0) {
                        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
                        Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

                        // Only compare up to the second because the datetime format we send to the client does
                        // not have milliseconds
                        long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
                        long fileLastModifiedSeconds = file.lastModified() / 1000;
                        if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                            sendNotModified(ctx);
                            return;
                        }
                    }
                    
                    RandomAccessFile raf;
                    try {
                        raf = new RandomAccessFile(file, "r");
                    } catch (FileNotFoundException fnfe) {
                        sendError(ctx, NOT_FOUND);
                        return;
                    }
                    long fileLength = raf.length();

                    HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
                    setContentLength(response, fileLength);
                    setContentTypeHeader(response, file);
                    setDateAndCacheHeaders(response, file);

                    Channel ch = ctx.getChannel();

                    // Write the initial line and the header.
                    ch.write(response);

                    // Write the content.
                    ChannelFuture writeFuture;
                    if (ch.getPipeline().get(SslHandler.class) != null) {
                        // Cannot use zero-copy with HTTPS.
                        writeFuture = ch.write(new ChunkedFile(raf, 0, fileLength, 8192));
                    } else {
                        // No encryption - use zero-copy.
                        final FileRegion region =
                            new DefaultFileRegion(raf.getChannel(), 0, fileLength);
                        writeFuture = ch.write(region);
                        writeFuture.addListener(new ChannelFutureProgressListener() {
                            public void operationComplete(ChannelFuture future) {
                                region.releaseExternalResources();
                                logger.info("finished writing file "+path);
                            }

                            public void operationProgressed(
                                    ChannelFuture future, long amount, long current, long total) {
                                //System.out.printf("%s: %d / %d (+%d)%n", path, current, total, amount);
                            }
                        });
                    }

                    // Decide whether to close the connection or not.
                    if (!isKeepAlive(request)) {
                        // Close the connection when the whole content is written out.
                        writeFuture.addListener(ChannelFutureListener.CLOSE);
                    }

                } catch (Exception e) {
                    CommandResult<?> commandResult = new CommandResult();
                    commandResult.setError(true);
                    commandResult.setErrorMessage("error get static file "+requestData.getRequest().getUri());
                    commandResult.setException(e);
                    String data = JsonUtil.writeObjectAsString(commandResult);
                    handler.writeInternalError(requestData, ctx, data);
                }
                
            } catch (Exception e) {
                logger.error("error getting file" + requestData.getRequest().getUri() ,e);
            }
        }
        
        
        public String getFilePath(){
            String uri = decodeUri(requestData.getRequest().getUri());
            String requestedFile = StringUtils.substringAfter(uri, config.getStaticFileRequestUri());
            
            if (requestedFile.contains(File.separator + '.') ||
                    requestedFile.contains('.' + File.separator) ||
                    requestedFile.startsWith(".") || requestedFile.endsWith(".")) {
                    throw new HandlerException("bad request file format:"+requestedFile);
            }
            
            return (config.getStaticFileFolder()+"/"+requestedFile).replace("//", "/");
        }
        
    }
    
    
    @Override
    public void exceptionCaught(
            ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        if(e.getCause() instanceof TimeoutException){
            logger.info("connection timeout for "+e.getChannel().toString()+", disconnect");
        }else {
            logger.error("Exception caught in DefaultHttpHandler :", e.getCause());
        }
        ctx.getChannel().close();
    }
}
