package com.rapidbackend.web;

import static com.rapidbackend.client.http.HttpCommandHelper.*;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.AfterClass;
import org.junit.BeforeClass;


import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;
import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.FileParam;

import com.rapidbackend.webserver.netty.NettyConf;
import com.rapidbackend.webserver.netty.NettyHttpServer;
/**
 * Test class for basic command execute and error handling.
 * @author chiqiu
 *
 */
@ThreadLeakScope(Scope.SUITE)// netty will cause thread leak error, so we ignore them here
public class TestNetty extends RapidbackendTestBase{
    protected static NettyHttpServer nettyHttpServer;
    //protected static ExecutorService executorService ;
    @BeforeClass
    public static void beforeClass() throws Exception{
        print("-------------->before Test Netty");
        overrideConf("src/test/resources/config/override/testnettyconf.xml");
        prepareTest();
        
        //t.forceInitClusterableService(nettyHttpServer, Rapidbackend.WebserverServiceName);
        //executorService = t.getThreadManager().newFixedThreadPool(null, "httpclientConnector", 10);
        nettyHttpServer = t.getWebserverService();
    }
    //@Test
    //@Repeat(iterations=10)
    public void testNettyFileupload() throws Exception{
        HttpPost post = createHttpPostWithFile(Protocol, DefaultWebServerName, "testUpload", 
                new ArrayList<CommandParam>(), new FileParam("media",new File("src/test/resources/img/signature_of_old_times.jpg")));
        String commandResult = httpClient.getCommandResult(post);
        post.abort();
        print(commandResult);
    }
    //@Test
    //@Performance
    public void testNettyFileupload2() throws Exception{
        HttpPost post = createHttpPostWithFile(Protocol, "localhost", "testUpload", 
                new ArrayList<CommandParam>(), new FileParam("media",new File("src/test/resources/img/signature_of_old_times.jpg") ));
        
        String commandResult = httpClient.getCommandResult(post);
        post.abort();
        print(commandResult);
        
    }
    
    //@Test
    public void testNettyStaticFile() throws Exception{
        NettyConf conf = (NettyConf)getAppContext().getBean("NettyConf");
        String fileName = conf.getStaticFileRequestUri()+"img/signature_of_old_times.jpg";
        HttpGet get = createHttpGet(Protocol, "localhost", fileName);
        httpClient.getStaticFile(get, new File("tempfolder/"+"temp.jpg"));
    }
    
    
    public void testNettyStaticFileWithWrongMethod() throws Exception{
        
    }
    
    @AfterClass
    @SuppressWarnings("unused")
    public static void afterClass() throws Exception{
        print("-------------->after Test Netty");
        nettyHttpServer.doStop();
    }
    
}
