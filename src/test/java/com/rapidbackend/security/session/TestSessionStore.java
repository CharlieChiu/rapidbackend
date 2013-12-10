package com.rapidbackend.security.session;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.util.comm.redis.RedisService;


/**
 * Test session store good path.Bad path will be test in {@link TestSession}.
 * @author chiqiu
 *
 */

public class TestSessionStore extends RapidbackendTestBase{
    
    protected static SessionStore sessionStore;
    @BeforeClass
    protected static void beforeClass() throws Exception{
        overrideConf("src/test/resources/config/override/testsession.xml");
        prepareTest();
        RedisService redisService = t.getRedisService();
        t.forceInitClusterableService(redisService, "redisService");
        sessionStore = (SessionStore)getAppContext().getBean("SessionStore");
    }
    protected String uuid = "91554389-5fef-4edc-8338-657fbbe97ce4";
    protected String notUsedUUID = "f64c0f8a-ba83-4297-a25e-784ae21fb802";
    
    //@Test
    public void testDeleteSession( )throws Exception{
        deleteSession();
        SessionBase session = SessionFactory.createSession();
        session.setSessionId("1:"+uuid);
        assertFalse(sessionStore.exists(session));
    }
    //@Test
    public void testUpdateSession( )throws Exception{
        SessionBase sessionBase =  loadSession();
        UserBase user = (UserBase)sessionBase.getAttribute("user");
        user.setScreenName("foo");
        SessionBase session = SessionFactory.createSession();
        
        sessionStore.store(session);
        sessionBase = loadSession();
        UserBase user2 = (UserBase)sessionBase.getAttribute("user");
        assertTrue(user2.getScreenName() !=null
                && user2.getScreenName().equalsIgnoreCase("foo"));
    }
    //@Test
    public void testLoadEmptySession( )throws Exception{
        SessionBase session = SessionFactory.createSession();
        session.setSessionId("1:"+notUsedUUID);
        session = sessionStore.load(session);
        assertNull(session);
    }
    
    
    //@Before
    public void storeSession() throws Exception{
        SessionBase session = SessionFactory.createSession();
        session.setSessionId("1:"+uuid);
        session.setAttribute("user", new UserBase());
        assertTrue(sessionStore.store(session));
    }
    
    public void deleteSession() throws Exception{
        SessionBase session = SessionFactory.createSession();
        session.setSessionId("1:"+uuid);
        sessionStore.delete(session);
    }
    
    public SessionBase loadSession() throws Exception{
        SessionBase session = SessionFactory.createSession();
        session.setSessionId("1:"+uuid);
        session = sessionStore.load(session);
        return session;
    }
    
}
