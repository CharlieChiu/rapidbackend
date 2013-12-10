package com.rapidbackend.socialutil.process.handler.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.RequestHandlerBase;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.security.shiro.SimpleDelegatingSubject;
import com.rapidbackend.security.shiro.SimpleSecurityManager;
import com.rapidbackend.socialutil.dao.UserDao;
import com.rapidbackend.socialutil.feeds.InboxService;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.security.UserLoginRealm.ScreenNamePasswordToken;
import com.rapidbackend.socialutil.util.ParamNameUtil;

/**
 * a basic implementation to login a user with the password/username combination
 * 
 * TODO add email login detection to support login by email
 * @author chiqiu
 *
 */
public class SimpleLoginHandler extends RequestHandlerBase{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    protected UserDao userDao;
    
    public UserDao getUserDao() {
        return userDao;
    }
    @Required
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    /*
    protected InboxService inboxService;
    
    public InboxService getInboxService() {
        return inboxService;
    }
    public void setInboxService(InboxService inboxService) {
        this.inboxService = inboxService;
    }*/
    
    protected List<InboxService> inboxServices;
    
    public List<InboxService> getInboxServices() {
        return inboxServices;
    }
    public void setInboxServices(List<InboxService> inboxServices) {
        this.inboxServices = inboxServices;
    }
    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        
        try {
            CommandParam screenName = request.getParam(ParamNameUtil.SCREEN_NAME);
            CommandParam password = request.getParam(ParamNameUtil.PASSWORD);
            
            SimpleSecurityManager securityManager = (SimpleSecurityManager)getApplicationContext().getBean("SecurityManager");
            SimpleDelegatingSubject simpleDelegatingSubject = new SimpleDelegatingSubject(request, securityManager);
            securityManager.enableSessionCache(request.getSession(false));
            
            ScreenNamePasswordToken screenNamePasswordToken = new ScreenNamePasswordToken(
                    screenName.getData().toString(),password.getData().toString(),"Login");
            simpleDelegatingSubject.login(screenNamePasswordToken);
            
            UserBase user  = (UserBase)simpleDelegatingSubject.getPrincipal();
            
            IntParam id = new IntParam("id", user.getId());// set the id param as the session ShardingKey
            
            request.addParam(id);
            
            user.setPassword("");// remove user password from response
            
            response.setResult(user);
            /*
            if(inboxService!=null){
                if(!inboxService.isRunning()){
                    inboxService.tryToStart();
                }
                if(!inboxService.isUserInboxReady(user.getId())){
                    getInboxService().createInboxFromDb(user.getId());
                }
            }*/
            
            if(inboxServices!=null){
                for(InboxService inboxService:inboxServices){
                    if(!inboxService.isRunning()){
                        inboxService.tryToStart();
                    }
                    if(!inboxService.isUserInboxReady(user.getId())){
                        inboxService.createInboxFromDb(user.getId());
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("error login user",e);
            throw new BackendRuntimeException(BackendRuntimeException.InternalServerError,"error login user",e);
        }
        
    }
    
    
}
