package com.rapidbackend.security.session;
import static com.rapidbackend.client.http.HttpCommandHelper.*;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;
import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.util.io.JsonUtil;
import com.rapidbackend.webserver.netty.NettyHttpServer;
@ThreadLeakScope(Scope.SUITE)
public class TestSession extends RapidbackendTestBase{
    protected static NettyHttpServer nettyHttpServer;
    protected static String TestCommandUri = "Test";
    protected static String overridingConfFile = "src/test/resources/config/override/testsession.xml";
    protected static String newSessionId = null;
    
    @BeforeClass
    public static void beforeClass() throws Exception{
        overrideConf(overridingConfFile);
        prepareTest();
        nettyHttpServer = t.getWebserverService();
        t.forceInitClusterableService(nettyHttpServer, Rapidbackend.WebserverServiceName);
        redisService = t.getRedisService();
        t.forceInitClusterableService(redisService, Rapidbackend.RedisServiceName);
    }
    /*
    @Test
    public String testCreateSession() throws Exception{
        ArrayList<CommandParam> params = new ArrayList<CommandParam>();
        params.add(new IntParam(10, "userId"));
        params.add(new StringParam(DefaultContent,"content"));
        params.add(new StringParam("password","password"));
        //params.add(new StringParam("10:db3cce6d-3d28-41c5-8746-95013c6096a1","sessionId"));
        HttpPost post = createHttpPostWithoutFile(DefaultProtocol, DefaultWebServerName, TestCommandUri, params);
        String result = httpClient.getCommandResult(post);
        logger.debug(result);
        CommandResult<Object> commandResult = JsonUtil.readObject(result.getBytes(), CommandResult.class);
        String sessionId = commandResult.getSessionId();
        logger.debug("session"+sessionId);
        assertNotNull(sessionId);
        assertTrue(sessionId.length()>36);
        return sessionId;
    }*/
    
    public void testSessionExpire(){
    }
    public void testSessionUpdate(){
    }
    public void testSessionDelete(){
    }
    public void testSessionAutoCreation(){
    }
    public void testInvalidSession(){
    }
    @AfterClass
    public static void afterClass(){
        nettyHttpServer.doStop();
        redisService.doStop();
    }
    
    public static void prepareSession() throws Exception{
        ArrayList<CommandParam> params = new ArrayList<CommandParam>();
        params.add(new IntParam( "userId",10));
        params.add(new StringParam("content",DefaultContent));
        params.add(new StringParam("password","password"));
        HttpPost post = createHttpPostWithoutFile(Protocol, DefaultWebServerName, TestCommandUri, params);
        String result = httpClient.getCommandResult(post);
        CommandResult<Object> commandResult = JsonUtil.readObject(result.getBytes(), CommandResult.class);
        String sessionId = commandResult.getSessionId();
        assertNotNull(sessionId);
        assertTrue(sessionId.length()>36);
        newSessionId = sessionId;
    }
}
