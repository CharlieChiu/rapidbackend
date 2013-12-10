package com.rapidbackend.util.comm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.context.AppContextAware;
/**
 * modified by chiqiu
 *
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractPollHandler extends AppContextAware implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(AbstractPollHandler.class);
    protected BlockingQueue<Exchanger> exchangerQueue ;
    protected Exchanger exchanger = new Exchanger();
    
    public AbstractPollHandler(){
    }
    
    public AbstractPollHandler(BlockingQueue<Exchanger> queue){
        exchangerQueue = queue;
    }
    
    public abstract boolean underPollingCondition();
    public abstract void handleIntrupt();
    public abstract void onStartExchange();
    public abstract void onFinishExchange();
    public abstract void handleReturnValue(Object value);
    public abstract void onStopHandle();
    protected Object currentJob;
    public Object getCurrentJob(){
        return currentJob;
    }
    
    private void setCurrentJob(Object currentJob) {
        this.currentJob = currentJob;
    }

    public void run() {
        logger.debug("{} is starting", Thread.currentThread().getName());

        while (underPollingCondition()) {
            logger.debug("{} is handling", Thread.currentThread().getName());
            try {
                exchangerQueue.put(exchanger);
            } catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    handleIntrupt();
                }
                continue;
            }

            onStartExchange();
            try {
                // Now wait for an object to come through the exchanger
                Object value;
                try {
                    value = exchanger.exchange(this);
                } catch (InterruptedException e) {
                    if (logger.isDebugEnabled()) {
                        //logger.debug("Interrupted, are we stopping? {}", isStopping() || isStopped());
                        handleIntrupt();
                    }
                    continue;
                }
                setCurrentJob(value);
                handleReturnValue(value);
                setCurrentJob(null);
                logger.trace("Got a value from the exchanger");
            } finally {
                onFinishExchange();
            }
        }

        // Decrement the shutdown countdown latch
        //shutdownLatch.countDown();
        onStopHandle();
        if (logger.isTraceEnabled()) {
            logger.trace("{} is finished", Thread.currentThread().getName());
        }
    }

    public BlockingQueue<Exchanger> getExchangerQueue() {
        return exchangerQueue;
    }

    public void setExchangerQueue(BlockingQueue<Exchanger> exchangerQueue) {
        this.exchangerQueue = exchangerQueue;
    }
    
}
