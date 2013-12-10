package com.rapidbackend.socialutil.feeds.service;

public interface FollowableService {
    /**
     * returns which followable this service serves. 
     * @return for example : user, tag, group.... depends on user configuration
     */
    public String getFollowableDomain();
}
