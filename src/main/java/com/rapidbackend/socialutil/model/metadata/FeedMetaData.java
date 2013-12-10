package com.rapidbackend.socialutil.model.metadata;

import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * 
 * @author chiqiu
 *
 */
public class FeedMetaData extends ModelMetadata{
	protected Integer repostCount = 0;
	protected Integer replyCount = 0;
	protected Integer likeCount = 0;
    
    
    public Integer getRepostCount() {
        return repostCount;
    }
    public void setRepostCount(Integer repostCount) {
        this.repostCount = repostCount;
    }
    public Integer getReplyCount() {
        return replyCount;
    }
    public void setReplyCount(Integer replyCount) {
        this.replyCount = replyCount;
    }
    public Integer getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    protected static HashMap<String, Method> methodCache = new HashMap<String, Method>();
   
    @Override
    public HashMap<String, Method> getMethodCache() {
        return methodCache;
    }
}
