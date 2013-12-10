package com.rapidbackend.socialutil.service.security;

import static com.rapidbackend.client.http.HttpCommandHelper.*;

import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.core.request.util.ParamList;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class TestUserLogin extends SecurityTestBase{
    
    @Test
    public void testLoginOfflineUser() throws Exception{
        
        HttpPost login  = createHttpPostWithoutFile(Protocol, LocalHost, "Login", (DbRecord)user);
        
        String loginResult = httpClient.getCommandResult(login);
        logger.debug(loginResult);
        CommandResult<?> commandResult = parseResult(loginResult, userClass);
        assertNotNull(commandResult.getSessionId());
        sessionId = commandResult.getSessionId();
    }
    
    @Test
    public void testLoginOnlineUser() throws Exception{
        logger.debug("test login online user");
        ParamList params = new ParamList();
        StringParam param = new StringParam(ParamNameUtil.SESSION_ID, sessionId);
        StringParam screenName = new StringParam(ParamNameUtil.SCREEN_NAME,user.getScreenName());
        StringParam password = new StringParam(ParamNameUtil.PASSWORD, user.getPassword());
        params.appendParam(param).appendParam(password).appendParam(screenName);
        
        HttpPost login  = createHttpPostWithoutFile(Protocol, LocalHost, "Login", params);
        String loginResult = httpClient.getCommandResult(login);
        logger.debug(loginResult);
        CommandResult<?> commandResult = parseResult(loginResult, userClass);
        assertNotNull(commandResult.getSessionId());
        assertEquals(sessionId, commandResult.getSessionId());
    }
    @Test
    public void testLoginWithWrongPassword() throws Exception{
        logger.debug("test login online user");
        ParamList params = new ParamList();
        StringParam param = new StringParam(ParamNameUtil.SESSION_ID, sessionId);
        StringParam screenName = new StringParam(ParamNameUtil.SCREEN_NAME,user.getScreenName());
        StringParam password = new StringParam(ParamNameUtil.PASSWORD, "this can't be correct");
        params.appendParam(param).appendParam(password).appendParam(screenName);
        
        HttpPost login  = createHttpPostWithoutFile(Protocol, LocalHost, "Login", params);
        String loginResult = httpClient.getCommandResult(login);
        logger.debug(loginResult);
        CommandResult<?> commandResult = parseResult(loginResult, userClass);
        assertNull(commandResult.getSessionId());
        assertTrue(commandResult.isError());
    }    
}
