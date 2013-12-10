package com.rapidbackend.webserver.netty.handler;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import com.rapidbackend.webserver.netty.NettyConf;

/**
 * only add chunk aggregator under some conditions
 * TODO custom it in the future
 * @author chiqiu
 *
 */
public class PipelineModifier extends SimpleChannelUpstreamHandler{
    
    protected NettyConf configuration;
    public PipelineModifier(NettyConf conf){
        super();
        configuration = conf;
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object event = e.getMessage();
        if(event instanceof HttpRequest){
            HttpRequest request = (HttpRequest)event;
            HttpMethod method =request.getMethod();
            if(!method.getName().equalsIgnoreCase(HttpMethod.POST.getName())){
                ctx.getPipeline().addAfter("modifier", "aggregator", new HttpChunkAggregator(configuration.getMaxContentLength()));
            }
        }
        super.messageReceived(ctx, e);
    }
}
