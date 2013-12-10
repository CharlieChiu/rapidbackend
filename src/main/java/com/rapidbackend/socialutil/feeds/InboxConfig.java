package com.rapidbackend.socialutil.feeds;
/**
 * The last 2 items are reserved for last read timestamp and latestupdateTime 
 * @author chiqiu
 *
 */
public interface InboxConfig {
	/**
	 * return how many items are reserved items<br>
	 * ( for example metadata, last read time ,last insert time) in this inbox
	 * @return
	 */
	public int getReservedItemNumber();
	/**
	 * Allowed FeedItem Index Range's start number, this is the index number 
	 * <br>from which we allow the feed item to be stored  
	 * @return
	 */
	public int getAllowedFeedItemIndexRangeStart();
	/**
	 * Allowed FeedItem Index Range's end number, this is the index number 
	 * <br>from which we don't allow the feed item to be stored.Outside this range, we should only store metadata or other things.
	 * @return
	 */
	public int getAllowedFeedItemIndexRangeEnd();
	/**
	 * get the inbox size, the maxim number of the stored feeds.
	 * @return
	 */
	public int getInboxSize();
	
	
}
