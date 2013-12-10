package com.rapidbackend.core.command;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.core.BackendErrorCodes;
import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.TimeOutAware;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.process.Pipeline;
import com.rapidbackend.core.process.ProcessStatus;
import com.rapidbackend.core.process.ProcessException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.util.time.SimpleTimer;
/**
 * 
 * @author chiqiu
 *
 */
public class DefaultCommand extends AppContextAware implements TimeOutAware,Callable<Boolean>{
    protected Logger logger = LoggerFactory.getLogger(DefaultCommand.class);
    protected static ExecutorService commandThreadPool;
    protected static int default_thread_pool_size = 100;//TODO move config to socialutily config
    protected long timeOut;
    protected CountDownLatch latch;
    protected Pipeline pipeline;
    protected CommandRequest request;
    protected CommandResponse response;
    protected SimpleTimer timer;
    protected static CommandSlowlog commandSlowlog = CommandSlowlog.getInstance();
    
    public static ExecutorService getThreadPoolExecutor() {
        if(null == commandThreadPool){
            commandThreadPool = Rapidbackend.getCore().getThreadManager().newFixedThreadPool(null, "CommandThreadPool", default_thread_pool_size);
        }
        return commandThreadPool;
    }
    
    /**
     * return the latch which is used to test if the command execution is timeout
     * @return
     */
    public CountDownLatch getLatch() {
        return latch;
    }
    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
    public Pipeline getPipeline() {
        return pipeline;
    }
    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }
    /**
     * @return the timeOut
     */
    public long getTimeOut() {
        return timeOut;
    }
    
    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
    
    public CommandRequest getRequest() {
        return request;
    }

    public void setRequest(CommandRequest request) {
        this.request = request;
    }

    public CommandResponse getResponse() {
        return response;
    }

    public void setResponse(CommandResponse response) {
        this.response = response;
    }

    /**
     * 
     * @param timeOut
     * @param req
     * @param rsp
     */
    public void setTimeOut(long timeOut, CommandRequest req,CommandResponse rsp) {
        this.timeOut = timeOut;
        this.request = req;
        this.response = rsp;
    }
    
    public DefaultCommand(long timeout,CommandRequest req,CommandResponse rsp){
        this.setTimeOut(timeout);
        latch = new CountDownLatch(1);
        timer = new SimpleTimer();
        this.request = req;
        this.response = rsp;
    }
    
    
    
    public SimpleTimer getTimer() {
        return timer;
    }

    public void setTimer(SimpleTimer timer) {
        this.timer = timer;
    }
    
    public long getTimeElapsed(){
        return timer.getIntervalMili();
    }

    /**
     * make warning of the timeout commands and log them to help us find out faulty commands    
     */
    public void handleTimeout(){
        cancelProcessingRequest();
    }
    
    public void cancelProcessingRequest(){
        request.setProcessStatus(ProcessStatus.Canceled);
    }
    
    /**
     * execute this command
     */
    public void execute() throws BackendRuntimeException{
        try {
            getThreadPoolExecutor().submit(this);
            boolean finished = false;
            try {
                finished = latch.await(timeOut, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // ignore latch exception
                logger.error("InterruptedException caught while waiting command ");
            }
            if(!finished){
                handleTimeout();
                // do not stop the timer if the command times out, keep the timer run in background
            }else {
                stopTimer();// stop the timer
                // add commands to slow log
                commandSlowlog.slowLogAnalysis(this);
                return;
            }
        } catch (Exception e) {
            logger.error("error excuting command"+this.toString(), e);
            throw new ProcessException(BackendErrorCodes.ErrorCreatingCommand, "error excuting");
        }
    }
    
    public void stopTimer(){
        timer.stop();
    }
    
    /**
     * 
     */
    public Boolean call() {
        try{
            pipeline.doHandle(request, response);
        }catch (Exception e) {
            logger.error("error during handle command,"+ pipeline.getPipelineName(),e);
            if(e instanceof BackendRuntimeException){
                response.setError(true);
                response.setException((BackendRuntimeException)e);
            }
        }finally{
            latch.countDown();
        }
        
        return true;
    }
    
    
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer(128);
        sb.append(request.toString());
        return sb.toString();
    }
    
}