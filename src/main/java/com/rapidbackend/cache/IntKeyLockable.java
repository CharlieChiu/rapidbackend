package com.rapidbackend.cache;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.util.time.SimpleTimer;


/**
 * 
 * @author chiqiu
 *
 */
public class IntKeyLockable extends KeyLockable<Integer>{
    Logger logger = LoggerFactory.getLogger(getClass());
    protected BitSet locks = new BitSet();
    ReentrantLock reentrantLock = new ReentrantLock(true);
    @Override
    public  void lock(Integer key){
        try {
            reentrantLock.lock();
            locks.set(key,true);
        }finally{
            reentrantLock.unlock();
        }
        
    }
    @Override
    public  void unlock(Integer key){
        try {
            reentrantLock.lock();
            locks.set(key, false);
        }finally{
            reentrantLock.unlock();
        }
        
    }
    @Override
    public boolean isLocked(Integer key){
        return locks.get(key);
    }
    @Override
    public  void lock(Collection<Integer> keys){
        try {
            reentrantLock.lock();
            if(keys !=null){
                for(Integer k:keys){
                    locks.set(k,true);
                }
            }
        }finally{
            reentrantLock.unlock();
        }
        
    }
    @Override
    public  void unlock(Collection<Integer> keys){
        try {
            reentrantLock.lock();
            if(keys !=null){
                for(Integer k:keys){
                    locks.set(k,false);
                }
            }
        }finally{
            reentrantLock.unlock();
        }
        
    }
    public  void lock(int[] keys){
        try {
            reentrantLock.lock();
            if(keys !=null){
                logger.debug("start lock");
                for(int k:keys){
                    locks.set(k,true);
                }
                logger.debug("finish lock");
            }
        }finally{
            reentrantLock.unlock();
        }
        
    }
    public  void unlock(int[] keys){
        try {
            reentrantLock.lock();
            if(keys !=null){
                for(int k:keys){
                    locks.set(k,false);
                }
            }
        }finally{
            reentrantLock.unlock();
        }
    }
    
    public static void main(String[] args){
        IntKeyLockable lockable = new IntKeyLockable();
        SimpleTimer timer = new SimpleTimer();
        HashMap<Integer, Object> map = new HashMap<Integer, Object>();
        for(int i = 0;i<1000000;i++){
            lockable.lock(i);
        }
        System.out.println(timer.getIntervalMili());
        SimpleTimer timer2 = new SimpleTimer();
        for(int i = 0;i<1000000;i++){
            map.put(i, new Object());
        }
        System.out.println(timer2.getIntervalMili());
    }
}
