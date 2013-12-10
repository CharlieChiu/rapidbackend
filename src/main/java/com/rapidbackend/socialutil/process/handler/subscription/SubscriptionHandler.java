package com.rapidbackend.socialutil.process.handler.subscription;


import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.socialutil.process.handler.IntermediateDatahandler;
import com.rapidbackend.socialutil.subscription.SubscriptionService;

public class SubscriptionHandler extends IntermediateDatahandler{

    protected SubscriptionService subscriptionService;
    protected String inputFollowableIdParamName;
    protected String inputFollowerIdParamName;
    protected String subscriptionClassName;
    
    
    public String getSubscriptionClassName() {
        return subscriptionClassName;
    }
    @Required
    public void setSubscriptionClassName(String subscriptionClassName) {
        this.subscriptionClassName = subscriptionClassName;
    }
    public SubscriptionService getSubscriptionService() {
        return subscriptionService;
    }
    @Required
    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    public String getInputFollowableIdParamName() {
        return inputFollowableIdParamName;
    }
    public void setInputFollowableIdParamName(String inputFollowableIdParamName) {
        this.inputFollowableIdParamName = inputFollowableIdParamName;
    }
    public String getInputFollowerIdParamName() {
        return inputFollowerIdParamName;
    }
    public void setInputFollowerIdParamName(String inputFollowerIdParamName) {
        this.inputFollowerIdParamName = inputFollowerIdParamName;
    }
    
    @Override
    public void handle(CommandRequest request, CommandResponse response)
            throws BackendRuntimeException {
        throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"SubscriptionHandler need to be overrided");
    }
    
    
}
