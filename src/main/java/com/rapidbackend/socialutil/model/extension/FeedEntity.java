package com.rapidbackend.socialutil.model.extension;

/**
 * contains meta data of the feeds, like repost counters ....
 * @author chiqiu
 *
 */
public class FeedEntity {
    protected FeedContext feedContext;
    protected int repostCount = 0;
    public FeedContext getFeedContext() {
        return feedContext;
    }
    public void setFeedContext(FeedContext feedContext) {
        this.feedContext = feedContext;
    }
    public int getRepostCount() {
        return repostCount;
    }
    public void setRepostCount(int repostCount) {
        this.repostCount = repostCount;
    }
    
}
