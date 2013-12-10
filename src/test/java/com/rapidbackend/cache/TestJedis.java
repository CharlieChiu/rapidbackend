package com.rapidbackend.cache;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;

public class TestJedis {
    
    public static void main(String[] args) throws Exception{
        List<Jedis> container = new ArrayList<Jedis>();
        
        for(int i =0;i<10000;i++){
            Jedis jedis = new Jedis("192.168.135.132", 6375);
            jedis.getClient().setTimeout(0);
            jedis.connect();
            jedis.getClient().setTimeoutInfinite();
            container.add(jedis);
            
        }
        
        System.out.println("jedis inited");
        System.out.println(container.size());
        
    }
}
