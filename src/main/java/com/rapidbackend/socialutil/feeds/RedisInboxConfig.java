package com.rapidbackend.socialutil.feeds;

public class RedisInboxConfig implements InboxConfig{
	
	protected int inboxSize = 512;
	/**
	 * for now we only store the last read time as the last item in the inbox
	 */
	protected int reservedItemNumber = 1;
	
	@Override
	public int getInboxSize() {
		return inboxSize;
	}
	@Override
	public int getReservedItemNumber() {
		return reservedItemNumber;
	}
	public void setReservedItemNumber(int reservedItemNumber) {
		this.reservedItemNumber = reservedItemNumber;
	}
	public void setInboxSize(int inboxSize) {
		this.inboxSize = inboxSize;
	}
	
	/**
	 * return the negative index from which the item stored in inbox is a feed tuple
	 * @return
	 */
	public int getLastFeedItemIndex(){
		return reservedItemNumber*-1 -1;
	}
	/**
	 * 
	 * @return the negative max index in redis 
	 */
	public int getMaxFirstFeedItemIndex(){
		return (inboxSize+reservedItemNumber)*-1;
	}
	@Override
	public int getAllowedFeedItemIndexRangeEnd(){
		return getLastFeedItemIndex();
	}
	@Override
	public int getAllowedFeedItemIndexRangeStart(){
		return getMaxFirstFeedItemIndex();
	}
}
