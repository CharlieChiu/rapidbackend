package com.rapidbackend.util.comm;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Poller which can be used for poll tasks
 * modified by chiqiu
 *
 */
public abstract class AbstractPoller implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(AbstractPoller.class);
    protected  boolean concurrent = true;// we use the default poller - handler model
    protected BlockingQueue<Exchanger> exchangerQueue ;
    protected String state = "stopped";
    /**
     * @return the concurrent
     */
    public boolean isConcurrent() {
        return concurrent;
    }
    
    public BlockingQueue<Exchanger> getExchangerQueue() {
        return exchangerQueue;
    }
    /**
     * user must let poller and handlers share the same exchanger queue
     * @param exchangerQueue
     */
    public void setExchangerQueue(BlockingQueue<Exchanger> exchangerQueue) {
        this.exchangerQueue = exchangerQueue;
    }


    public AbstractPoller(){}
    /**
     * @param concurrent the concurrent to set
     */
    public void setConcurrent(boolean concurrent) {
        this.concurrent = concurrent;
    }
    public AbstractPoller(boolean concurrent,BlockingQueue<Exchanger> queue){
        this.concurrent = concurrent;
        exchangerQueue = queue;
    }
    
    public abstract String getPollerName() ;
    public abstract boolean underPollingCondition();
    public abstract void onStartPolling();
    public abstract void onStopPolling();
    public abstract Object doPolling() throws Exception;
    public abstract String getPollTarget();
    public abstract void handleNoPollingResult() ;
    public abstract void handleIntrupt();
    public abstract boolean resultNotEmpty(Object returnValue);
    
    public void run() {
        

        
        
        onStartPolling();

        Exchanger exchanger = null;
        while (underPollingCondition()) {
            logger.debug(getPollerName() + "poller is running");
            if (concurrent) {
                try {
                    exchanger = exchangerQueue.take();
                } catch (InterruptedException e) {
                    if (logger.isDebugEnabled()) {
                        //logger.debug("Interrupted, are we stopping? {}", isStopping() || isStopped());
                        handleIntrupt();
                    }
                    continue;
                }

            }

            // Poll  until we get an object back
            Object value = null;
            while (underPollingCondition()) {
                logger.debug("Polling {}", getPollTarget());
                try {
                    value = doPolling();
                    if (resultNotEmpty(value)) {
                        break;
                    }
                } catch (Exception e) {
                    if (underPollingCondition()) {
                        logger.error("error during polling",e);
                    }
                }

                if (underPollingCondition()) {
                    handleNoPollingResult();
                }
            }

            logger.trace("Got object from {}", getPollTarget());

            if (concurrent) {
                try {
                    exchanger.exchange(value);
                } catch (InterruptedException e) {
                    if (logger.isDebugEnabled()) {
                        //logger.debug("Interrupted, are we stopping? {}", isStopping() || isStopped());
                        handleIntrupt();
                    }
                    continue;
                }
            } else {
                //TODO
            }
        }
        logger.trace("Finished polling {}", getPollTarget());

        // Decrement the shutdown countdown latch
        onStopPolling();
    }
}