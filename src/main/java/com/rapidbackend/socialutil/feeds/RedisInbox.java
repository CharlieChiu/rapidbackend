package com.rapidbackend.socialutil.feeds;


import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;



import redis.clients.jedis.Tuple;



/**
 * The redis inbox looks like this:<br>
 * (tuple -1),(tuple -2),(tuple -3)..... =====><br>
 * (lastReadTimestamp,Long.MAX_VALUE -1),(tuple3),(tuple4)......<p>
 * We should only update the last read timestamp when the inbox is read by the user himself.<br>
 * Because we might want to read a user's inbox by other apis<br>
 * The last read timestamp is useful when we want to poll the newest updates with a js and tell user how many feeds has been updated since the user viewed his timeline<p>
 * after reading, we need to cache it or just use a bit?<br>
 * Pro: bit don't need to worry about threading, only successful read can flip the bit. 
 * the redis inbox is stored in a local LRU cache for performance<br>
 * 
 * @author chiqiu
 *
 */
@Deprecated
public class RedisInbox extends RawInbox{
	/**
	 * The last 2 items are reserved for last read timestamp and latestupdateTime
	 */
	
	public static Long lastReadTimeStampScore = Long.MAX_VALUE -1;
	public static Long lastPostTimeStampScore = Long.MAX_VALUE;
	
	protected int userId;
	protected ConcurrentSkipListSet<Tuple> sortedFeeds;
	protected boolean initialized = false;
	protected long startTimeStamp;
	protected long endTimeStamp;
	protected int maxSize;
	private final ReentrantLock lock = new ReentrantLock();//TODO remove this lock?
	
	/**
	 * 
	 * @param allfeeds
	 * @param startScore startTimeStamp
	 * @param endScore endTimeStamp
	 */
	public RedisInbox(Set<Tuple> allfeeds, long startScore,long endScore){
		startTimeStamp = startScore;
		endTimeStamp = endScore;
		sortedFeeds = new ConcurrentSkipListSet<Tuple>();
		InboxConfig inboxConfig = (InboxConfig)getApplicationContext().getBean("FeedInboxConfig");
		maxSize = inboxConfig.getInboxSize();
		initData(allfeeds);
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public void initData(Set<Tuple> feeds) {
		if(feeds!=null){
			insertWithOverflow(feeds);
		}
		initialized = true;
	}
	/**
	 * insert feeds into this inbox , pop oldest feeds if we reach the max size of this inbox
	 * @param feeds
	 */
	public void insertWithOverflow(Iterable<Tuple> feeds){
		try{
			for(Tuple feed:feeds){
				sortedFeeds.add(feed);
				if(sortedFeeds.size()>getInboxMaxSize()){
					sortedFeeds.pollFirst();
				}
			}
		}finally{
		}
	}
	@Override
	public int getInboxMaxSize(){
		return maxSize;
	}
	@Override
	public int size(){
		return sortedFeeds.size();
	}
	@Override
	public  List<String> getAllFeedIds(){
		List<String> res = new ArrayList<String>();
		try{
			lock.lock();
			for(Tuple t:sortedFeeds){
				res.add(t.getElement());
			}
		}finally{
			lock.unlock();
		}
		return res;
	}
	
	@Override
	public List<String> getFeedIds(int start,int limit){
		List<String> result = new ArrayList<String>();
		if(initialized()){
			try{
				int i =0;
				int j = limit;
				Iterator<Tuple> iter = sortedFeeds.descendingIterator();
				lock.lock();
				while(iter.hasNext()){
					Tuple temp = iter.next();
					if(i>=start&&j>0){
						result.add(temp.getElement());
						j--;
					}
					i++;
				}
			}finally{
				lock.unlock();
			}
			
			/*
			int j = limit;
			int size = allIntFeedIds.size();
			int i = start;
			while(i<size&&j>0){
				result.add(allIntFeedIds.get(size -1-i++ ));
				j--;
			}*/
		}
		return result;
	}
	
	/**
	 * check if this inbox has been inited
	 */
	@Override
	public boolean initialized(){
		return this.initialized;
	}
	
	public static Tuple createLastReadItem(Long date){
		Long time = null;
		if(null != date){
			time = date;
		}else {
			time = System.currentTimeMillis();
		}
		double fixedScore = lastPostTimeStampScore.doubleValue();
		return new Tuple(time.toString(), fixedScore);
	}
	
}
