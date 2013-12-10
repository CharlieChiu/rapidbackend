package com.rapidbackend.socialutil.install.webserver;

import java.io.Serializable;
import java.util.List;

public class WebConfig implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 6754327553564386691L;
    
    private String dbName;
    private String dbUri;
    private String username;
    private String password;
    
    List<FollowableWebConfig> followableConfigs;
    List<UserDefinedModelWebConfig> userDefinedModels;

    public List<UserDefinedModelWebConfig> getUserDefinedModels() {
        return userDefinedModels;
    }

    public void setUserDefinedModels(
            List<UserDefinedModelWebConfig> userDefinedModels) {
        this.userDefinedModels = userDefinedModels;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUri() {
        return dbUri;
    }

    public void setDbUri(String dbUri) {
        this.dbUri = dbUri;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<FollowableWebConfig> getFollowableConfigs() {
        return followableConfigs;
    }

    public void setFollowableConfigs(List<FollowableWebConfig> followableConfigs) {
        this.followableConfigs = followableConfigs;
    }
    
    
}