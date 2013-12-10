package com.rapidbackend.webserver.netty;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.util.thread.DefaultThreadFactory;
/**
 * Simple embedded http server. As simple as possiable.
 * @author chiqiu
 *
 */
public class NettyHttpServer extends ClusterableService{
     
    protected Logger logger = LoggerFactory.getLogger(NettyHttpServer.class);
    protected static Map<String, Object> defaultServerOptions = new HashMap<String, Object>();
    static{//TODO add it to configuration?
        defaultServerOptions.put("backlog", 1000);
        defaultServerOptions.put("reuseAddress", true);
        defaultServerOptions.put("child.keepAlive", true);
        defaultServerOptions.put("child.tcpNoDelay", true);
    }
    
    List<ExecutorService> executorServices = new ArrayList<ExecutorService>();
    protected ChannelFactory channelFactory;
    protected ServerBootstrap serverBootstrap;
    protected ThreadFactory threadFactory;
    protected String serverName;
    protected NettyConf configuration;
    protected Channel channel;
    protected ExecutorService httpHandlerExecutorService;
    protected ExecutorService boss;
    protected ExecutorService workers;
    
    protected static ChannelGroup allchannels = new DefaultChannelGroup("NettyHttpServer");
    
    
    public static ChannelGroup getAllchannels() {
        return allchannels;
    }
    protected Timer timer;
    
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public List<ExecutorService> getExecutorServices() {
        return executorServices;
    }
    public void setExecutorServices(List<ExecutorService> executorServices) {
        this.executorServices = executorServices;
    }
    
    public NettyConf getConfiguration() {
        return configuration;
    }
    @Required
    public void setConfiguration(NettyConf configuration) {
        this.configuration = configuration;
    }
    @Override
    public void doStart(){
        logger.info("starting netty server");
        int port = configuration.getServerPort();
        configuration.getUploadConf().config();
        
        serverName = getString(serverName, "RapidBackendHttpServer");
        String namePattern = "Rapidbackend httpserver ("  + ") thread #${counter} - ${name}";
        boss = Executors.newCachedThreadPool(new DefaultThreadFactory(namePattern, "netty boss", true));
        workers = Executors.newCachedThreadPool(new DefaultThreadFactory(namePattern, "netty worker", true));
        if(httpHandlerExecutorService==null){
            httpHandlerExecutorService = new ThreadPoolExecutor(0, configuration.getCustomHttpHanderThreadNumber(),
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    new DefaultThreadFactory(namePattern, "netty custom task handler", true));
        }
        
        channelFactory = new NioServerSocketChannelFactory(boss, workers);
        
        timer = new HashedWheelTimer();
        
        ChannelPipelineFactory pipelineFactory = new HttpChannelPipelineFactory(httpHandlerExecutorService,timer);
        
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.setPipelineFactory(pipelineFactory);
        serverBootstrap.setFactory(channelFactory);
        channel = serverBootstrap.bind(new InetSocketAddress(port));
        allchannels.add(channel);
        collectExecutorServices(boss);
        collectExecutorServices(workers);
        collectExecutorServices(httpHandlerExecutorService);
        
        setRunning(true);
        logger.info("netty server started");
    }
    @Override
    public void doStop(){
        try {
            ChannelGroupFuture future = allchannels.close();
            future.awaitUninterruptibly();
        } catch (Exception e) {
            logger.error("error release netty channel",e);
        }
        try {
            if(timer!=null){
                timer.stop();
            }
        } catch (Exception e) {
            logger.error("error release netty hashed wheel timer",e);
        }
        try {
            httpHandlerExecutorService.shutdownNow();
            for(ExecutorService executorService: executorServices){
                executorService.shutdownNow();//TODO more safe way?
                //ExecutorUtil.terminate(executorService);
            }
        } catch (Exception e) {
            logger.error("error release netty executorServices",e);
        }
        try {
            if(serverBootstrap!=null){
                serverBootstrap.releaseExternalResources();
            }
        } catch (Exception e) {
            logger.error("error release netty external Resource",e);
        }
        
        setRunning(false);
    }
    
    public void collectExecutorServices(ExecutorService executorService){
        if(executorService!=null && !executorServices.contains(executorService)){
            executorServices.add(executorService);
        }
    }
    
    
    
    public static Integer getInt(Integer param,Integer defaultValue){
        if(param==null){
            return defaultValue;
        }else {
            return param;
        }
    }
    public static String getString(String param,String defaultValue){
        if(param==null){
            return defaultValue;
        }else {
            return param;
        }
    }
}
