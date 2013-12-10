package com.rapidbackend.util.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


import com.rapidbackend.core.context.AppContextAware;

/**
 * persist Objects in jvm to disk periodically
 * @author chiqiu
 *
 */
public class PeriodicWriter extends AppContextAware{
	
	public static String Good_State = "good";
	public static String Error_State = "error";
	protected String fileName;
	protected long interval;
	protected ScheduledExecutorService scheduler;
	protected PersistantSourceProvider sourceProvider;
	protected WriteTask writeTask;
	protected OutputFormat outPutFormat;
	/**
	 * 
	 * @param filename
	 * @param syncInterval
	 * @param provider
	 * @param format
	 */
	public PeriodicWriter(String filename, ScheduledExecutorService scheduledExecutorService,long syncInterval, PersistantSourceProvider provider,OutputFormat format){
		fileName = filename;
		interval = syncInterval;
		//scheduler = Executors.newScheduledThreadPool(1);//TODO use threadmanager to manage this thread
		scheduler = scheduledExecutorService;
		sourceProvider = provider;
		outPutFormat = format;
		Assert.notNull(fileName);
		Assert.notNull(interval);
		Assert.notNull(outPutFormat);
		Assert.notNull(sourceProvider);
		Assert.notNull(scheduler);
		writeTask = new WriteTask(scheduler, fileName, interval, sourceProvider,outPutFormat);
	}
	
	public synchronized void startWriteTask(){
		writeTask.start();
	}
	
	public synchronized void stopWriteTask(){
		writeTask.stop();
	}
	
	public String getStatus(){
		return writeTask.getTaskStatus();
	}
	
	
	public static class WriteTask implements Runnable{
		Logger logger = LoggerFactory.getLogger(PeriodicWriter.class);
		
		protected volatile ScheduledFuture<?> writeFuture;
		protected String fileName;
		protected long interval;
		protected ScheduledExecutorService scheduler;
		protected PersistantSourceProvider sourceProvider;
		protected String taskStatus = Good_State;
		protected OutputFormat outPutFormat;
		protected String getTaskStatus(){
			return taskStatus;
		}
		public void setTaskStatus(String taskStatus) {
			this.taskStatus = taskStatus;
		}
		public WriteTask(ScheduledExecutorService scheduleService,String filename, long syncInterval,PersistantSourceProvider provider,OutputFormat format){
			fileName = filename;
			interval = syncInterval;
			scheduler = scheduleService;
			sourceProvider = provider;
			outPutFormat = format;
		}
		
		public synchronized void start(){
			writeFuture = scheduler.scheduleWithFixedDelay(this, 0l, interval, TimeUnit.MILLISECONDS);
		}
		
		public synchronized void stop(){
			if(writeFuture!=null&& !writeFuture.isDone()){
				writeFuture.cancel(true);
			}
		}
		
		public void write() throws IOException{
			Object target = sourceProvider.provideSource();
			switch (outPutFormat) {
			case Text:
				String data = target.toString();
				writeString(data);
				break;
			default:
				writeObject(target);
			}
		}
		
		public void writeObject(Object object) throws IOException{
			ObjectIoUtil.writeObj(object, fileName);
		}
		
		public void writeString(String data) throws IOException{
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(data);
			writer.close();
		}
		@Override
		public void run(){
			try{
				write();
			}catch(IOException e){
				logger.error("error happened while doing PeriodicWrite",e);
				setTaskStatus(Error_State);
			}
		}
		
	}
	
	public abstract static class PersistantSourceProvider{
		public abstract Object provideSource();
	}
	
	public static enum OutputFormat{
		JavaObject,Text,Bin
	}
}
