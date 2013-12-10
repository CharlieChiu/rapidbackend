package com.rapidbackend.socialutil.service.security;

import static com.rapidbackend.client.http.HttpCommandHelper.*;

import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;

import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.ParamFactory;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.security.session.SessionBase;
import com.rapidbackend.security.session.SessionFactory;

public class TestUserLogout extends SecurityTestBase{
    @Before
    public void before() throws Exception{
        TestUserLogin testUserLogin = new TestUserLogin();
        testUserLogin.testLoginOfflineUser();
    }
    
    @Test
    public void testLogout() throws Exception{
    	List<CommandParam> params = ParamFactory.convertModelToParams((DbRecord)user);
        StringParam param = new StringParam("sessionId", sessionId);
        params.add(param);
        SessionBase session = SessionFactory.createSession(sessionId);
        session.setShardingKey(userId);
        assertTrue(sessionStore.exists(session));
        //;
        HttpGet get = createHttpGet(Protocol, LocalHost, "Logout", params);
        String logoutInfo = httpClient.getCommandResult(get);
        logger.debug(logoutInfo);
        CommandResult<?> commandResult = parseResult(logoutInfo, userClass);
        assertFalse(sessionStore.exists(session));
        assertFalse(commandResult.isError());
    }
    
}
