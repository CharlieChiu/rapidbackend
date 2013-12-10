package com.rapidbackend.socialutil.install.dbinstall;

import java.util.ArrayList;
import java.util.List;

public class FollowableConfig{
    private String name = "";
    private String feedByFollowableTableName = "";
    private String feedContentTableName = "";
    private String subscriptionTableName = "";
    private String feedCommentTableName = "";
    
    private List<ModelField> collumnsAppendToFeedContentTable = new ArrayList<ModelField>();
    private List<ModelField> collumnsAppendToFollowableTable = new ArrayList<ModelField>();
    private List<ModelField> collumnsAppendToCommentTable = new ArrayList<ModelField>();
    
    private boolean referenceAnotherFollowablesFeed = false;
    private String referenceFollowableName = "";
    
    
    private String feedByFollowableTableSql="";
    private String feedContentTableSql="";
    private String subscriptionTableSql="";
    private String followableTableSql="";
    private String feedCommentTableSql="";
    
    
    public String getFeedCommentTableName() {
        return feedCommentTableName;
    }
    public void setFeedCommentTableName(String feedCommentTableName) {
        this.feedCommentTableName = feedCommentTableName;
    }
    public List<ModelField> getCollumnsAppendToCommentTable() {
        return collumnsAppendToCommentTable;
    }
    public void setCollumnsAppendToCommentTable(
            List<ModelField> collumnsAppendToCommentTable) {
        this.collumnsAppendToCommentTable = collumnsAppendToCommentTable;
    }
    
    
    public String getFeedCommentTableSql() {
        return feedCommentTableSql;
    }
    public void setFeedCommentTableSql(String feedCommentTableSql) {
        this.feedCommentTableSql = feedCommentTableSql;
    }
    public String getSubscriptionTableName() {
        return subscriptionTableName;
    }
    public void setSubscriptionTableName(String subscriptionTableName) {
        this.subscriptionTableName = subscriptionTableName;
    }
    public String getFollowableTableSql() {
        return followableTableSql;
    }
    public void setFollowableTableSql(String followableTableSql) {
        this.followableTableSql = followableTableSql;
    }
    public String getFeedByFollowableTableSql() {
        return feedByFollowableTableSql;
    }
    public void setFeedByFollowableTableSql(String feedByFollowableTableSql) {
        this.feedByFollowableTableSql = feedByFollowableTableSql;
    }
    public String getFeedContentTableSql() {
        return feedContentTableSql;
    }
    public void setFeedContentTableSql(String feedContentTableSql) {
        this.feedContentTableSql = feedContentTableSql;
    }
    public String getSubscriptionTableSql() {
        return subscriptionTableSql;
    }
    public void setSubscriptionTableSql(String subscriptionTableSql) {
        this.subscriptionTableSql = subscriptionTableSql;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getFeedByFollowableTableName() {
        return feedByFollowableTableName;
    }
    public void setFeedByFollowableTableName(String feedByFollowableTableName) {
        this.feedByFollowableTableName = feedByFollowableTableName;
    }
    public String getFeedContentTableName() {
        return feedContentTableName;
    }
    public void setFeedContentTableName(String feedContentTableName) {
        this.feedContentTableName = feedContentTableName;
    }
    
    
    public List<ModelField> getCollumnsAppendToFeedContentTable() {
        return collumnsAppendToFeedContentTable;
    }
    public void setCollumnsAppendToFeedContentTable(
            List<ModelField> collumnsAppendToFeedContentTable) {
        this.collumnsAppendToFeedContentTable = collumnsAppendToFeedContentTable;
    }
    /**
     * not supported any more, forkfollowable will be implemented in release 0.3 or per user requests
     * @return
     */
    @Deprecated
    public boolean isReferenceAnotherFollowablesFeed() {
        return false;
        //return referenceAnotherFollowablesFeed;
    }
    public void setReferenceAnotherFollowablesFeed(
            boolean referenceAnotherFollowablesFeed) {
        this.referenceAnotherFollowablesFeed = referenceAnotherFollowablesFeed;
    }
    public String getReferenceFollowableName() {
        return referenceFollowableName;
    }
    public void setReferenceFollowableName(String referenceFollowableName) {
        this.referenceFollowableName = referenceFollowableName;
    }
    public List<ModelField> getCollumnsAppendToFollowableTable() {
        return collumnsAppendToFollowableTable;
    }
    public void setCollumnsAppendToFollowableTable(
            List<ModelField> collumnsAppendToFollowableTable) {
        this.collumnsAppendToFollowableTable = collumnsAppendToFollowableTable;
    }
    
}