package com.rapidbackend.socialutil.feed;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.socialutil.dao.UserDao;
import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;
import com.rapidbackend.socialutil.install.service.ServiceGenerator;
import com.rapidbackend.socialutil.model.data.ModelFactory;
import com.rapidbackend.socialutil.model.reserved.UserBase;
import com.rapidbackend.socialutil.subscription.SubscriptionService;

/**
 * This method will create test datas for StreamService Tests.
 * After test data for one followable is created, corresponding touch file will be created.
 * For example: if we have create data for  'followable' user , a touch file testDataForUserCreated will be created.
 * And if we detect testDataForUserCreated has been created, no new data will be added to this followable.
 * @author chiqiu
 *
 */
public class TestDataGenerator extends AppContextAware{
    Logger logger = LoggerFactory.getLogger(TestDataGenerator.class);
    ModelFactory modelFactory;
    public TestDataGenerator() throws Exception{
        modelFactory = new ModelFactory();
    }
    
    
    public void prepareDataForFollowable(String followableName){
                        
    }
            
    public void cleanDataBase(String followableName) throws Exception{
        logger.info("------->cleanDataBase: reinstall database for "+followableName);
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB();
        logger.info("<-------cleanDataBase: reinstall database done");
    }
    
    public void createOneFollowableWithFollowers(String followableName) throws Exception{
        
        // In the test we should keep all the Users get online though a 'login' action 
        //StreamService streamService = (StreamService)getApplicationContext().getBean(ServiceGenerator.getStreamServiceBeanName(followableName));
        //streamService.getInboxService().setPushAllways(true);
        
        if(followableName.equalsIgnoreCase("User")){
            createUserWithFollowers();
        }
    }
    
    
    public void createUserWithFollowers() throws Exception{
        logger.info("------->createUser");
        
        logger.info("create first User");
        UserBase star = modelFactory.createUser();
        SubscriptionService subscriptionService = (SubscriptionService)getApplicationContext().getBean(ServiceGenerator.getSubscriptionServiceBeanName("User"));
        UserDao userDao = (UserDao)getApplicationContext().getBean("UserDao");
        Number id = userDao.storeModelBean(star);
        
        if(id.intValue() != 1){
            throw new RuntimeException("the first user's id is :"+ id.intValue() + " , which should be 1");
        }
        logger.info("create 100 followings for user");
        
        List<UserBase> fans = new ArrayList<UserBase>();
        for(int i=0;i<100;i++){
            UserBase fan = modelFactory.createUser();
            userDao.storeModelBean(fan);
            fans.add(fan);
        }
        
        logger.info("create 99 followers to user 1");        
        for(int i=2;i<=100;i++){
            subscriptionService.addSubscription(1, i);
        }
        
        
        logger.info("<-------createUser");
    }
    
    /**
     * 
     * @param userId
     */
    public void loginFollower(Integer userId){
        
    }
        
    public void createTouchFile(String followableName,Status status){
        
    }
    
    //public void removeTouchFiles()
    
    private enum Status{
        Generating,Done
    }
    
}
