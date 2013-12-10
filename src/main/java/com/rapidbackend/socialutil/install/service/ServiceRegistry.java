package com.rapidbackend.socialutil.install.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.ClusterableService;


public class ServiceRegistry {
    protected List<ClusterableService> serviceBeans;

    public List<ClusterableService> getServiceBeans() {
        return serviceBeans;
    }
    @Required
    public void setServiceBeans(List<ClusterableService> serviceBeans) {
        this.serviceBeans = serviceBeans;
    }
}
