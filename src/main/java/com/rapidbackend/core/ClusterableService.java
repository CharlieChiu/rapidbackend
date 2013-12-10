package com.rapidbackend.core;

import com.rapidbackend.core.context.AppContextAware;

public abstract class ClusterableService extends AppContextAware{
    protected boolean runAllowed = true;
    protected boolean shutdownPending = false;
    protected boolean running = false;
    protected boolean startPending = false;
    
    public boolean readyForInit(){
        return isRunAllowed()&& !isRunning();
    }
    /**
     * @return the runAllowed
     */
    public boolean isRunAllowed() {
        return runAllowed;
    }
    /**
     * @param runAllowed the runAllowed to set
     */
    public void setRunAllowed(boolean runAllowed) {
        this.runAllowed = runAllowed;
    }
    /**
     * @return the shutdownPending
     */
    public boolean isShutdownPending() {
        return shutdownPending;
    }
    /**
     * @param shutdownPending the shutdownPending to set
     */
    public void setShutdownPending(boolean shutdownPending) {
        this.shutdownPending = shutdownPending;
    }
    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }
    /**
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        if(!running){
            setShutdownPending(false);
        }else {
            setStartPending(false);
        }
        this.running = running;
    }
    
    public boolean isStartPending() {
        return startPending;
    }
    public void setStartPending(boolean startPending) {
        this.startPending = startPending;
    }
    
    public synchronized void tryToStart() throws Exception{
        if(!running && !startPending){
            setStartPending(true);
            doStart();
            setRunning(true);
        }
    }
    public synchronized void trytoStop() throws Exception{
        if(running && !shutdownPending){
            setShutdownPending(true);
            doStop();
            setRunning(false);
        }
    }
    public abstract void doStart() throws Exception;
    public abstract void doStop() throws Exception;
}
