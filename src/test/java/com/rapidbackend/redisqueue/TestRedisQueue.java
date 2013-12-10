package com.rapidbackend.redisqueue;

import java.util.ArrayList;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.extension.Extension4Test;


@Deprecated// we've covered redisque in timeline tests
public class TestRedisQueue extends RapidbackendTestBase{
    /*
    RedisQueue redisQueue;
    String redisTargetName;
    String queueName;
    static RedisQueueService redisQueueService;
    @BeforeClass
    @SuppressWarnings({ "unused"})
    public static void beforeClass() throws Exception{
        System.setProperty("testing", "true");
        Extension4Test extension = new Extension4Test(
                new String[]{"src/test/resources/config/override/testRedisQueue.xml"}
                );
        Extension4Test.setInstance(extension);
        prepareTest();
    }
    @AfterClass
    public static void afterClass() throws Exception{
        redisQueueService.doStop();
        Thread.currentThread().sleep(1000l);
    }
    @Before
    public void before() throws Exception{
        redisQueueService = (RedisQueueService)getAppContext().getBean("TestRedisQueueService");
        redisQueueService.tryToStart();
        redisQueue = redisQueueService.getRedisQueue();
        RedisQueueProcessor processor = redisQueueService.getQueueProcessors().get(0);
        redisTargetName = processor.getRedisConfig().getTargetName();
        queueName = processor.getQueueName();
        //redisQueue.deleteQueue(queueName, redisTargetName);
    }
    
    @Test
    public void testRedisQueueSequence() throws Exception{
        
        List<String> jobs = new ArrayList<String>();
        jobs.add("one");
        jobs.add("two");
        jobs.add("three");
        for(String s:jobs){
            redisQueue.addItem(s, redisTargetName, queueName);
        }
        Thread.currentThread().sleep(1000l);
        assertArrayEquals(jobs.toArray(new String[0]), QueueTestPollHandler.revieved.toArray(new String[0]));
    }*/
}
