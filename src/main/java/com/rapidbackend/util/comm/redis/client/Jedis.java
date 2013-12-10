package com.rapidbackend.util.comm.redis.client;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * extends jedis in case need to override functions
 * @author chiqiu
 *
 */
public class Jedis extends redis.clients.jedis.Jedis{
    Logger logger = LoggerFactory.getLogger(com.rapidbackend.util.comm.redis.client.Jedis.class);
    public Jedis(String host,int port){
        super(host, port);
    }
    AtomicLong rpopNum = new AtomicLong(0);
    @Override
    public String rpop(final String key) {
        rpopNum.incrementAndGet();
        logger.debug("rpop ====>"+rpopNum.get());
        checkIsInMulti();
        client.eval(rpopLua, 1, key);
        //logger.debug("rpop command sent ====>"+System.currentTimeMillis());
        String val = client.getBulkReply();
        logger.debug("rpop result  <===="+rpopNum.get());
        if(val.equals("emptylist")){
            return null;
        }else {
            return val;
        }
    }
    
    private static String rpopLua = "local res\n res = redis.call('LLEN',KEYS[1])\n if res>0 then return redis.call('RPOP',KEYS[1]) else return 'emptylist' end";
    
    
    public static void main(String[] args) throws Exception{
        Jedis jedis = new Jedis("192.168.135.138", 6374);
        
        int num = 1;
        while(num>0){
            
            if (num %2 ==0) {
                jedis.lpush("userFeedPostQueue", rpopLua); 
            }
            System.out.println("rpop======>"+num);
            jedis.rpop("userFeedPostQueue");
            System.out.println("rpop<======"+num);
            Thread.currentThread().sleep(100);
            num++;
        }
    }
}
