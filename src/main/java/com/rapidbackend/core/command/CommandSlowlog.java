package com.rapidbackend.core.command;



import java.util.concurrent.locks.ReentrantLock;

import com.rapidbackend.util.general.PriorityQueue;
/**
 * track slow logs,
 * TODO truncate command parameter if it is too long
 * @author chiqiu
 *
 */
public class CommandSlowlog {
    protected int default_slow_log_number = 5000;
    protected long default_slow_log_time_threshold = 500l;
    protected int slowLogNumber = default_slow_log_number;
    protected long slowLogTimeThreshold = default_slow_log_time_threshold;
    
    protected static CommandSlowlog instance;
    
    public synchronized static CommandSlowlog getInstance(){
        if(instance == null){
            instance = new CommandSlowlog();//TODO create by spring
            return instance;
        }else {
            return instance;
        }
    }
    
    private final ReentrantLock lock = new ReentrantLock(true);
    protected PriorityQueue<DefaultCommand> slowCommands = new PriorityQueue<DefaultCommand>(slowLogNumber) {
        public boolean lessThan(DefaultCommand a,DefaultCommand b){
            return a.getTimeElapsed()<b.getTimeElapsed()?true:false;
        }
    };
    public int getSlowLogNumber() {
        return slowLogNumber;
    }

    public void setSlowLogNumber(int slowLogNumber) {
        this.slowLogNumber = slowLogNumber;
    }
    
    public long getSlowLogTimeThreshold() {
        return slowLogTimeThreshold;
    }

    public void setSlowLogTimeThreshold(long slowLogTimeThreshold) {
        this.slowLogTimeThreshold = slowLogTimeThreshold;
    }
    /**
     * put into slow log queue, only the slowest commands will be hold.<br>
     * queue size is not 
     * @param command
     */
    public void slowLogAnalysis(DefaultCommand command){
        final ReentrantLock lock = this.lock;
        try{
            lock.lock();
            if(command.getTimeElapsed()>getSlowLogTimeThreshold()){
                slowCommands.insertWithOverflow(command);
            }
        }finally{
            lock.unlock();
        }
        
    }
    /**
     * clear slowCommands. very fast.
     */
    public void clear(){
        final ReentrantLock lock = this.lock;
        try{
            lock.lock();
            slowCommands.clear();
        }finally {
            lock.unlock();
        }
        
    }
    /**
     * 
     * @param number
     */
    public void resetSlowLogNumber(int number){
        final ReentrantLock lock = this.lock;
        if(number<0){
            return;
        }else {
            try{
                lock.lock();
                slowCommands = new PriorityQueue<DefaultCommand>(number) {
                    public boolean lessThan(DefaultCommand a,DefaultCommand b){
                        return a.getTimeElapsed()<b.getTimeElapsed()?true:false;
                    }
                };
            }finally{
                lock.unlock();
            }
        }
    }
}
