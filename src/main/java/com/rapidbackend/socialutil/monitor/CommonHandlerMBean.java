package com.rapidbackend.socialutil.monitor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.rapidbackend.util.time.SimpleTimer;

public abstract class CommonHandlerMBean implements SocialHandlerMBean{
	
	protected String currentState;
	protected String[] allPossiableStates = {"unInicialized","running","stopped"};
	protected AtomicLong requestsNumber  = new AtomicLong(0);
	protected SimpleTimer timer = new SimpleTimer("");
	public String[] getAllPossiableStates(){
		return allPossiableStates;
	}
	public long howManyRequstHanded(){
		return requestsNumber.get();
	}
	public long howLongSinceIWasBorn(){
		return timer.getIntervalMili();
	}
	public String getCurrentState(){
		return currentState;
	}
	public abstract long timeSpentOnCurrentRequest();
	
}
