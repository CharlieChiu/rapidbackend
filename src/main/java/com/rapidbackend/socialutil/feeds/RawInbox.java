package com.rapidbackend.socialutil.feeds;
import java.util.*;

import com.rapidbackend.core.context.AppContextAware;
/**
 * A inbox which contains only feed ids
 * @author chiqiu
 *
 */
public abstract class RawInbox extends AppContextAware{
	public abstract int getInboxMaxSize();
	public abstract int size();
	public abstract List<String> getAllFeedIds();
	public abstract List<String> getFeedIds(int start,int limit);
	public abstract boolean initialized();
	/*
	
	public abstract List<Integer> getFeedIds(int start,int limit);
	public abstract boolean initialized();
	*/
}
