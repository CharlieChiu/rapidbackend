package com.rapidbackend.webserver.netty;

import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.util.Timer;

import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.webserver.netty.handler.CloseIdleChannelHandler;
import com.rapidbackend.webserver.netty.handler.HttpHandlerBase;
/**
 * @author chiqiu
 */
public class HttpChannelPipelineFactory extends AppContextAware implements ChannelPipelineFactory{
    protected final NettyConf configuration;
    protected final SSLFactory sslFactory;
    protected final Timer timer;
    protected final ChannelHandler timeoutHandler;
    protected final String serverName;
    protected final ExecutorService httpHandlerExecutorService;
    public HttpChannelPipelineFactory(ExecutorService httpHandlerExecutorService, Timer timer){
        configuration = (NettyConf)getApplicationContext().getBean("NettyConf");
        sslFactory = new SSLFactory();
        this.timer = timer;
        timeoutHandler = new CloseIdleChannelHandler(timer, 0,0,configuration.getTimeoutSeconds());
        this.serverName = configuration.getServerName();
        this.httpHandlerExecutorService = httpHandlerExecutorService;
    }
    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        if(configuration.isUseSSL()){
            SSLEngine engine = sslFactory.getServerContext().createSSLEngine();
            engine.setUseClientMode(false);
            SslHandler handler = new SslHandler(engine);
            handler.setIssueHandshake(true);
            pipeline.addLast("ssl", handler);
        }
        pipeline.addLast("timeoutControl",timeoutHandler );
        pipeline.addLast("decoder", new HttpRequestDecoder(configuration.getMaxInitialLineLength(), 
                configuration.getMaxHeaderSize(), configuration.getMaxChunkSize()));
        //pipeline.addLast("modifier", new PipelineModifier(configuration));
        //pipeline.addLast("aggregator", new HttpChunkAggregator(configuration.getMaxContentLength()));
        pipeline.addLast("decompressor", new HttpContentDecompressor());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("compressor", new HttpContentCompressor());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        //pipeline.addLast("htmluploadpage", new HtmluploadHandler());
        HttpHandlerBase httpHandler = (HttpHandlerBase)getApplicationContext().getBean("NettyHttpHandler");
        httpHandler.setExecutor(httpHandlerExecutorService);
        httpHandler.setServerName(serverName);
        httpHandler.setNettyConf(configuration);
        pipeline.addLast("HttpHandler", httpHandler);
        return pipeline;
    }
}
