package com.rapidbackend.util.time;
/**
 * utility class to track how long has elapsed
 * @author chiqiu
 *
 */
public class SimpleTimer {
	
	protected long begin;
	protected long end;
	protected String mesg;
	protected boolean stopped = false;
	
	/**
	 * return how long has this object been created in miliseconds
	 * @return
	 */
	public long getIntervalMili(){
	    setEndTime();
		return end - begin;
	}
	/**
	 * return how long has this object been created in a message string
	 * @return
	 */
	public String getIntervalString(){
	    setEndTime();
		return mesg +" : "+ (end - begin)+"ms";
	}
	public SimpleTimer(String message){
		mesg = message;
		begin = System.currentTimeMillis();
	}
	public SimpleTimer(){
		mesg = "";
		begin = System.currentTimeMillis();
	}
	/**
	 * reset the timer
	 */
	public void reset(){
	    stopped = false;
		begin = System.currentTimeMillis();
	}
	public String toString(){
		return getIntervalString();
	}
	/**
	 * set the timer's end time
	 */
	private void setEndTime(){
	    if(!stopped){
	        end = System.currentTimeMillis();
	    }
	}
	/**
	 * stop the timer
	 */
	public void stop(){
	    end = System.currentTimeMillis();
	    stopped = true;
	}
	/**
	 * return if the timer is stopped(not ticking)
	 * @return
	 */
    public boolean isStopped() {
        return stopped;
    }
    /**
     * set the start time to a earlier value
     * @param startTime
     */
	public void rewind(long startTime){
	    if(startTime<System.currentTimeMillis()){
	        begin = startTime;
	    }
	}
}
