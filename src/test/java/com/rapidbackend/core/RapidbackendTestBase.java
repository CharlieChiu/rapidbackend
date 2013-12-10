package com.rapidbackend.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;
import com.rapidbackend.TestException;
import com.rapidbackend.TestcaseBase;
import com.rapidbackend.client.http.HttpCommandHelper.WebProtocol;
import com.rapidbackend.client.http.HttpClientConf;
import com.rapidbackend.client.http.SimpleHttpClient;
import com.rapidbackend.core.command.DefaultCommand;
import com.rapidbackend.core.context.AppContext;
import com.rapidbackend.core.request.RequestConfig;
import com.rapidbackend.util.comm.redis.RedisService;
import com.rapidbackend.webserver.netty.NettyConf;
import com.rapidbackend.webserver.netty.NettyHttpServer;
@ThreadLeakScope(Scope.SUITE)
public abstract class RapidbackendTestBase extends TestcaseBase{
    protected static Logger logger = LoggerFactory.getLogger(RapidbackendTestBase.class);
    protected static long FIVE_MIN = 1000*60*5;
    protected static AtomicInteger constructTime = new AtomicInteger(0);
    @Deprecated
    protected static String overridingContext = "src/test/resources/config/override.xml";
    
    protected static Rapidbackend t;
    protected static SimpleHttpClient httpClient;
    protected static String DefaultWebServerName = "localhost";
    protected static String ProtocolPropertyName = "WebProtocol";
    protected static WebProtocol Protocol = WebProtocol.https;
    protected static NettyHttpServer nettyHttpServer;
    protected static RedisService redisService;
    
    protected static String DefaultContent = "The Internet is a global system of interconnected computer networks that use the standard Internet protocol suite (often called TCP/IP, although not all applications use TCP) to serve billions of users worldwide. It is a network of networks that consists of millions of private, public, academic, business, and government networks, of local to global scope, that are linked by a broad array of electronic, wireless and optical networking technologies. The Internet carries an extensive range of information resources and services, such as the inter-linked hypertext documents of the World Wide Web (WWW) and the infrastructure to support &email." +
    		"Most traditional communications media including telephone, music, film, and television are reshaped or redefined by the Internet, giving birth to new services such as Voice over Internet Protocol (VoIP) and Internet Protocol Television (IPTV). Newspaper, book and other print publishing are adapting to Web site technology, or are reshaped into blogging and web feeds. The Internet has enabled and accelerated new forms of human interactions through instant messaging, Internet forums, and social networking. Online shopping has boomed both for major retail outlets and small artisans and traders. Business-to-business and financial services on the Internet affect supply chains across entire industries.";
    
    @BeforeClass
    public static void prepareRapidbackend() throws Exception{
        logger.info("prepareRapidbackend ============>");
        //createEmptyOverridingContext();
        
        t = Rapidbackend.getCore();
        constructTime.incrementAndGet();
        //print("RapidbackendTestBase:"+constructTime.get());
        
        logger.info("prepareRapidbackend <============");
    }
    @AfterClass
    public static void cleanupRapidbackend(){
        /*
         * release thread consumed by commands
         */
        DefaultCommand.getThreadPoolExecutor().shutdownNow();
    }
    
    /**
     * Get the spring context config files used in test
     * @return
     */
    @Deprecated
    public static String[] getTestAppContextConfigFiles() throws Exception{
        String[] defaultContext = AppContext.getApplicationContextConfigFiles();
        List<String> configFiles =  new ArrayList<String>();
        for(String s: defaultContext){
            configFiles.add(s);
        }
        
        configFiles.add("src/test/resources/config/testmodels.xml");
        configFiles.add(overridingContext);
        
        return configFiles.toArray(new String[0]);
    }
    
    /**
     * 
     * init with an empty context
     */
    @Deprecated
    public static void createEmptyOverridingContext()throws Exception{
        try {
            FileUtils.copyFile(new File("src/test/resources/config/override/empty.xml"), new File(overridingContext));
        } catch (IOException e) {
            throw new TestException("",e);
        }
    }
    /**
     * @deprecated use Extension4Test instead
     * override the default configuration with new bean settings 
     * @param configFile
     */
    @Deprecated
    protected static void overrideConf(String configFile) throws Exception{
        assertNotNull(configFile);
        try {
            FileUtils.copyFile(new File(configFile), new File(overridingContext));
        } catch (IOException e) {
            throw new TestException("overrideConf error",e);
        }
    }
    
    
    public static void prepareTest() throws Exception{
        //Rapidbackend.getApplicationContext().refresh();
       // t.init4Test(); //include the default settings and test
        t.init();
        NettyConf nettyConf = (NettyConf)getAppContext().getBean("NettyConf");
        HttpClientConf httpClientConf = (HttpClientConf)getAppContext().getBean("HttpClientConf");
        if(nettyConf.isUseSSL()){
            Protocol = WebProtocol.https;
        }else {
            Protocol = WebProtocol.http; 
        }
        switch (Protocol) {
        case http:
            httpClientConf.setHttpPort(NettyConf.DefaultHttpPort);
            break;
        default:
            httpClientConf.setHttpPort(NettyConf.DefaultHttpsPort);
            break;
        }
        httpClient = new SimpleHttpClient();
    }
    /**
     * 
     * @throws Exception
     */
    @Deprecated
    public static void prepareSchema() throws Exception{
        RequestConfig.clear();
        RequestConfig.init(false,getTestAppContextConfigFiles());
    }
    public static AbstractApplicationContext getAppContext(){
        return Rapidbackend.getApplicationContext();
    }
    public static Object getBean(String beanName){
        return getAppContext().getBean(beanName);
    }
    public static String getOverridingContext() {
        return overridingContext;
    }
    public static Rapidbackend getCore() {
        return t;
    }
    public static SimpleHttpClient getHttpClient() {
        return httpClient;
    }
    public static String getProtocolPropertyName() {
        return ProtocolPropertyName;
    }
    public static WebProtocol getDefaultProtocol() {
        return Protocol;
    }
    public static NettyHttpServer getNettyHttpServer() {
        return nettyHttpServer;
    }
    @Deprecated
    public static RedisService getRedisService() {
        return redisService;
    }
    
    protected static AbstractApplicationContext getApplicationContext(){
        return AppContext.getApplicationContext();
    }
}
