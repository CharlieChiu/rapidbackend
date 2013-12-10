package com.rapidbackend.socialutil.service.security;

import static com.rapidbackend.client.http.HttpCommandHelper.LocalHost;
import static com.rapidbackend.client.http.HttpCommandHelper.createHttpPostWithoutFile;
import static com.rapidbackend.client.http.HttpCommandHelper.parseResult;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.apache.http.client.methods.HttpPost;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.security.session.SessionStore;
import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;
import com.rapidbackend.socialutil.model.data.ModelFactory;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.util.comm.redis.client.RedisClient;

public class SecurityTestBase extends RapidbackendTestBase{
    
    protected static Logger logger = LoggerFactory.getLogger(SecurityTestBase.class);
    protected static UserBase user;
    protected static Class<?> userClass;
    protected static String screenName;
    protected static Integer userId;
    protected static String sessionId;
    protected static String plaintextPassowrd;
    protected static String encryptedPassword;
    protected static SessionStore sessionStore;
    
    @BeforeClass
    public static void beforeClass() throws Exception{
        print("-----------------> SecurityTestBase beforeClass");
        prepareTest();
        init();
    }
    
    public static void init() throws Exception{
        // init session store bean
    	sessionStore = (SessionStore)getApplicationContext().getBean("SessionStore");
        // reinstall db
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB();
        //create user
        createUser();
        assertTrue(checkRedisSessionStore());
        cleanRedis();
        
    }
    
    @AfterClass
    public static void after() throws Exception{
        logger.info("clean all data");
        // clear redis session store
        cleanRedis();
        // reinstall db
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB();
    }
    
    protected static boolean checkRedisSessionStore() throws Exception{
        boolean result = false;
        result = sessionStore.getRedisCache().ping(userId);
        return result;
    }
    
    public static void cleanRedis() throws Exception{
        Collection<BlockingQueue<RedisClient>> poolCollection = Rapidbackend.getCore().getRedisClientPoolContainer().getClientPools().values();
        for(BlockingQueue<RedisClient> queue:poolCollection){
            RedisClient client = queue.peek();
            if(client!=null)
                client.getJedis().flushAll();
        }
    }
    
    protected static void createUser() throws Exception{
        ModelFactory modelFactory = new ModelFactory();
        Class<?> userClass = ModelReflectionUtil.getUserClass();
        DbRecord model = modelFactory.createModel(userClass);
        user = (UserBase)model;
        
        String name = ModelReflectionUtil.getPropertyValue(userClass, "screenName", user).toString();
        String pass = ModelReflectionUtil.getPropertyValue(userClass, "password", user).toString();
        
        screenName = name;
        plaintextPassowrd = pass;
        
        HttpPost create = createHttpPostWithoutFile(Protocol, LocalHost, "CreateUser", model);
        String createResult = httpClient.getCommandResult(create);
        Class<?> modelClass = ModelReflectionUtil.getUserClass();
        userClass = modelClass;
        
        logger.debug("create Result for "+ModelReflectionUtil.getUserClass().getSimpleName());
        logger.debug(createResult);
        CommandResult<?> CreateCommandResult = parseResult(createResult, modelClass);
        
        boolean error = CreateCommandResult.isError();
        assertFalse(error);
        
        DbRecord object = (DbRecord)CreateCommandResult.getResult();
        userId = object.getId();
        
        DbRecord newUser = (DbRecord)user;
        newUser.setId(userId);
    }
    
}
