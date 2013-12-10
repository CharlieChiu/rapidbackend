package com.rapidbackend.socialutil.feeds.config;

/**
 * 
 * @author chiqiu
 *
 */
public class UserStatusCacheConfig {
	public static String defaultJournalName = "UserStatusCache";
	public static long defaultPersistInterval = 1000l*60*60*24; //24 hours
	
	protected String journalFileName = defaultJournalName;
	
	protected long persistInterval = defaultPersistInterval;
	
	public String getJournalFileName() {
		return journalFileName;
	}

	public void setJournalFileName(String journalFileName) {
		this.journalFileName = journalFileName;
	}

	public long getPersistInterval() {
		return persistInterval;
	}

	public void setPersistInterval(long persistInterval) {
		this.persistInterval = persistInterval;
	}
	
}
