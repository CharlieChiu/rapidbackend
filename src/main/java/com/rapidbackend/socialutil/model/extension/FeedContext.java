package com.rapidbackend.socialutil.model.extension;

import com.rapidbackend.socialutil.model.metadata.FeedMetaData;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;

/**
 * feed context which contains some additional datas, like repost contexts and users
 * @author chiqiu
 *
 */
public class FeedContext {
    
    
    private Object feed;
    private Integer id;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    /**
     * who created this feed
     */
    private UserBase user;    
    private boolean feedNotFound = false;// we can still store the context in the cache if the feed is not found
    private boolean seedFeedNotFound = false;// this value means we have seed feed but we didn't find it
    private boolean userNotFound = false;
    private boolean seedFeedUserNotFound = false;
    @Deprecated
    private FeedMetaData feedMetaData;
    @Deprecated
    private FeedMetaData seedFeedMetaData;
    
    /**
     * Now only called by serializer
     */
    @Deprecated
    public FeedContext(){
        
    }
    public FeedContext(int id){
        setId(id);
    }
    
    public FeedMetaData getFeedMetaData() {
        return feedMetaData;
    }
    public void setFeedMetaData(FeedMetaData feedMetaData) {
        this.feedMetaData = feedMetaData;
    }
    public FeedMetaData getSeedFeedMetaData() {
        return seedFeedMetaData;
    }
    public void setSeedFeedMetaData(FeedMetaData seedFeedMetaData) {
        this.seedFeedMetaData = seedFeedMetaData;
    }
    public Object seedFeed;
    public UserBase seedFeedUser;

    public Object getFeed() {
        return feed;
    }
    public void setFeed(Object feed) {
        this.feed = feed;
    }
    
    public UserBase getUser() {
        return user;
    }
    public void setUser(UserBase user) {
        this.user = user;
    }
    public UserBase getSeedFeedUser() {
        return seedFeedUser;
    }
    public void setSeedFeedUser(UserBase seedFeedUser) {
        this.seedFeedUser = seedFeedUser;
    }
    public boolean isFeedNotFound() {
        return feedNotFound;
    }
    public void setFeedNotFound(boolean feedNotFound) {
        this.feedNotFound = feedNotFound;
    }
    public boolean isSeedFeedNotFound() {
        return seedFeedNotFound;
    }
    public void setSeedFeedNotFound(boolean seedFeedNotFound) {
        this.seedFeedNotFound = seedFeedNotFound;
    }
    public Object getSeedFeed() {
        return seedFeed;
    }
    public void setSeedFeed(Object seedFeed) {
        this.seedFeed = seedFeed;
    }
    public boolean isUserNotFound() {
        return userNotFound;
    }
    public void setUserNotFound(boolean userNotFound) {
        this.userNotFound = userNotFound;
    }
    public boolean isSeedFeedUserNotFound() {
        return seedFeedUserNotFound;
    }
    public void setSeedFeedUserNotFound(boolean seedFeedUserNotFound) {
        this.seedFeedUserNotFound = seedFeedUserNotFound;
    }
    
    public FeedContentBase feed(){
        return (FeedContentBase)feed;
    }
    
    public FeedContentBase seedFeed(){
        return (FeedContentBase)seedFeed;
    }
}
