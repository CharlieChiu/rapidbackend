package com.rapidbackend.socialutil.model.util;

import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
/**
 * 
 * @author chiqiu
 *
 */
public class FeedFactory extends AppContextAware{
	/**
	 * configured in services.xml
	 */
    public static ContentTruncater contentTruncater;
    
	public static ContentTruncater getContentTruncater() {
        if(contentTruncater==null){
            contentTruncater = (ContentTruncater)getApplicationContext().getBean("FeedContentTruncater");
        }
        return contentTruncater;
    }
    public static void setContentTruncater(ContentTruncater contentTruncater) {
        FeedFactory.contentTruncater = contentTruncater;
    }

    public static FeedContentBase createFeed(UserBase user,String content){
		FeedContentBase feed = createEmptyFeed();
		feed.setUserId(user.getId());
		setFeedContent(feed,content);
		return feed;
	}
    
    public static void setFeedContent(FeedContentBase feed , String content){
        String truncatedContent = getContentTruncater().truncate(content);
        feed.setContent(truncatedContent);
        feed.setTruncated(isTruncated(content,truncatedContent));
    }
    
	public static void setDefaultValues(FeedContentBase feed){
	    feed.setReplyToId(0);
	    feed.setRepostToUserId(0);
	    feed.setRepostToFeedId(0);
	    feed.setSeedFeedId(0);
	    feed.setTruncated(0);
	}
	/**
	 * repost does not need to add the former feed's content,just copy the root id and
	 * the former feed's id is enough
	 * @param repostTo
	 * @param userId
	 * @param content
	 * @return
	 */ 
	public static FeedContentBase createRepostFeed(FeedContentBase repostTo,Integer userId,String content){
	    FeedContentBase repost = createEmptyFeed();
	    repost.setRepostToFeedId(repostTo.getId());
	    repost.setUserId(userId);
	    repost.setRepostToUserId(repostTo.getUserId());
	    setFeedContent(repost,content);
	    setSeedFeedId(repostTo,repost);
	    return repost;
	}
	/**
	 * if repostTo is a repost, set seed id to repostTo's seedFeedId, else, set seedFeedId to repostTo's Id
	 * @param repostTo
	 * @param repost
	 */
	public static void setSeedFeedId(FeedContentBase repostTo,FeedContentBase repost){
	    if(repostTo.getSeedFeedId()!=null&&repostTo.getSeedFeedId()!=0){
	        repost.setSeedFeedId(repostTo.getSeedFeedId());
	    }else {
	        repost.setSeedFeedId(repostTo.getId());
        }
	}
	
	public static FeedContentBase createEmptyFeed(){
	    FeedContentBase feed = new FeedContentBase();
	    setDefaultValues(feed);
	    return feed;
	}
	
	public static Integer isTruncated(String orginal,String destination){
	    if(destination.length()<orginal.length()){
	        return 1;
	    }else {
            return 0;
        }
	}
	
}
