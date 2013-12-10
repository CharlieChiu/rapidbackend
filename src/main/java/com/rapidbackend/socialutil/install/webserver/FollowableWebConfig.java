package com.rapidbackend.socialutil.install.webserver;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rapidbackend.socialutil.install.dbinstall.ModelField;
import com.rapidbackend.socialutil.install.dbinstall.FollowableConfig;

public class FollowableWebConfig implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 5112269526052125364L;
    private String followableName;
    private boolean referToAnotherFollowablesFeed;
    private String referringToFollowable;
    private List<ModelField> followableModelConfig;
    private List<ModelField> followableFeedContentConfig;
    private List<ModelField> followableFeedCommentConfig;
    
    
    
    public List<ModelField> getFollowableFeedCommentConfig() {
        return followableFeedCommentConfig;
    }
    public void setFollowableFeedCommentConfig(
            List<ModelField> followableFeedCommentConfig) {
        this.followableFeedCommentConfig = followableFeedCommentConfig;
    }
    public String getFollowableName() {
        return followableName;
    }
    public void setFollowableName(String followableName) {
        this.followableName = followableName;
    }
    /**
     * this method is deprecated, forkfollowable will be implemented in release 0.3
     * @return
     */
    @Deprecated
    public boolean isReferToAnotherFollowablesFeed() {
        return false;
        //return referToAnotherFollowablesFeed;
    }
    public void setReferToAnotherFollowablesFeed(
            boolean referToAnotherFollowablesFeed) {
        this.referToAnotherFollowablesFeed = referToAnotherFollowablesFeed;
    }
    public String getReferringToFollowable() {
        return referringToFollowable;
    }
    public void setReferringToFollowable(String referringToFollowable) {
        this.referringToFollowable = referringToFollowable;
    }
    public List<ModelField> getFollowableModelConfig() {
        return followableModelConfig;
    }
    public void setFollowableModelConfig(List<ModelField> followableModelConfig) {
        this.followableModelConfig = followableModelConfig;
    }
    public List<ModelField> getFollowableFeedContentConfig() {
        return followableFeedContentConfig;
    }
    public void setFollowableFeedContentConfig(
            List<ModelField> followableFeedContentConfig) {
        this.followableFeedContentConfig = followableFeedContentConfig;
    }
    
    public FollowableConfig toFollowableConfig() throws IOException{
        FollowableConfig followableConfig = new FollowableConfig();
        followableConfig.setName(followableName);
        followableConfig.setReferenceAnotherFollowablesFeed(isReferToAnotherFollowablesFeed());
        followableConfig.setReferenceFollowableName(referringToFollowable);
        
        mapModelFieldTypes(followableFeedContentConfig);
        mapModelFieldTypes(followableModelConfig);
        mapModelFieldTypes(followableFeedCommentConfig);
        
        followableConfig.setCollumnsAppendToFeedContentTable(followableFeedContentConfig);
        followableConfig.setCollumnsAppendToFollowableTable(followableModelConfig);
        followableConfig.setCollumnsAppendToCommentTable(followableFeedCommentConfig);
        
        String feedByFollowableTableName = followableName+"feed";
        followableConfig.setFeedByFollowableTableName(feedByFollowableTableName);
        
        String subscriptionTableName = followableName +"subscription";
        followableConfig.setSubscriptionTableName(subscriptionTableName);            
        
        String feedContentTableName = feedByFollowableTableName+"content";
        followableConfig.setFeedContentTableName(feedContentTableName);
        
        String feedCommentTableName = feedByFollowableTableName + "comment";
        followableConfig.setFeedCommentTableName(feedCommentTableName);
        
        return followableConfig;
    }
    
    public void mapModelFieldTypes(List<ModelField> fields) throws IOException{
        String errMsg = "error creating fields for followable: "+followableName;
        for(ModelField field : fields){
            String dataType = InstallServlet.getTypeMapping().get(field.getType());
            if(StringUtils.isEmpty(dataType)){
                throw new RuntimeException(errMsg+"dataType is not setting correctly for field "+ field.getName());
            }
            field.setDataType(dataType);
        }
    }
}