package com.rapidbackend.core.request;

import static com.rapidbackend.client.http.HttpCommandHelper.*;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.BeforeClass;
import org.junit.Test;

import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope;
import com.carrotsearch.randomizedtesting.annotations.ThreadLeakScope.Scope;
import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.request.util.ParamList;
import com.rapidbackend.extension.Extension4Test;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.general.Tuple;
import com.rapidbackend.util.io.JsonUtil;
@ThreadLeakScope(Scope.SUITE)
public class TestRequestSchema extends RapidbackendTestBase{
    
    @BeforeClass
    public static void beforeClass() throws Exception{
        System.setProperty("testing", "true");
        Extension4Test extension = new Extension4Test(
                new String[]{"src/test/resources/config/override/testRequestSchema.xml"}, 
                new Tuple<String, String>("/SchemaTest","SchemaTest"),
                new Tuple<String, String>("/EchoContent","EchoContent")
                );
        Extension4Test.setInstance(extension);
        prepareTest();
    }
    @Test
    @SuppressWarnings("unchecked")
    public void testMissingRequestParam() throws Exception{
        ArrayList<CommandParam> params = new ArrayList<CommandParam>();
        params.add(new LongParam( "date",System.currentTimeMillis()));
        HttpGet get = createHttpGet(Protocol, DefaultWebServerName, "SchemaTest", params);
        String commandResult = httpClient.getCommandResult(get);
        get.abort();
        print(commandResult);
        CommandResult<Object> result = JsonUtil.readObject(commandResult.getBytes(), CommandResult.class);
        String errMsg = result.getErrorMessage();
        assertNotNull(errMsg);
        assertTrue(errMsg.indexOf(ParamException.class.getSimpleName())>=0);
        assertTrue(errMsg.indexOf("missing")>=0);
    }
    @Test
    public void testMultipleMandatoryGroup() throws Exception{
        ArrayList<CommandParam> params = new ArrayList<CommandParam>();
        params.add(new IntParam("fid",1));
        params.add(new StringParam("content","we are cjks tests 你好，这是汉字" ));
        HttpGet get = createHttpGet(Protocol, DefaultWebServerName, "SchemaTest", params);
        String commandResult = httpClient.getCommandResult(get);
        get.abort();
        print(commandResult);
    }
    @Test
    public void testMissingCommandSchema() throws Exception{
        ArrayList<CommandParam> params = new ArrayList<CommandParam>();
        params.add(new LongParam( "date",System.currentTimeMillis()));
        HttpGet get = createHttpGet(Protocol, DefaultWebServerName, "ThisCommandShouldNotExist", params);
        HttpResponse response = httpClient.getCommandHttpResponse(get);
        logger.debug(response.getStatusLine().toString());
        assertTrue(response.getStatusLine().toString().indexOf(""+BackendRuntimeException.BAD_REQUEST)>0);
    }
    @Test
    public void testUtf8PostRequest() throws Exception{
        ParamList paramList = new ParamList();
        String cjk = "漢語，又称中文cjk";
        StringParam content = new StringParam(ParamNameUtil.CONTENT,cjk);
        paramList.add(content);
        HttpPost post = createHttpPostWithoutFile(Protocol, DefaultWebServerName, "EchoContent", paramList);
        String commandResult = httpClient.getCommandResult(post);
        logger.debug(commandResult);
        logger.debug(cjk);
        
        CommandResult<String> result = parseResult(commandResult, String.class);
        assertFalse(result.isError());
        
        String echoBack = result.getResult();
        
        assertEquals(echoBack, cjk);
    }
    /**
     * create a command with 100 multiple not needed params, see if we can 
     * extract correct param
     * @throws Exception
     */
    @Test
    public void testMultiplePostRequestParam() throws Exception{
        ParamList paramList = new ParamList();
        String cjk = "漢語，又称中文cjk";
        StringParam content = new StringParam(ParamNameUtil.CONTENT,cjk);
        paramList.add(content);
        
        for(int i=0;i<100;i++){
            StringParam param = new StringParam(ParamNameUtil.CONTENT+i,"empty");
            paramList.add(param);
        }
        
        HttpPost post = createHttpPostWithoutFile(Protocol, DefaultWebServerName, "EchoContent", paramList);
        String commandResult = httpClient.getCommandResult(post);
        logger.debug(commandResult);
        
        CommandResult<String> result = parseResult(commandResult, String.class);
        assertFalse(result.isError());
        
        String echoBack = result.getResult();
        
        assertEquals(echoBack, cjk);
    }
}
