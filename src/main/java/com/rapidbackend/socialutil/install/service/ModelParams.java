package com.rapidbackend.socialutil.install.service;

import java.util.Set;
import java.util.TreeSet;

import com.rapidbackend.core.request.CommandParam;

public class ModelParams {
    Set<CommandParam> params = new TreeSet<CommandParam>();
    
    protected String modelName;
    
    public Set<CommandParam> getParams() {
        return params;
    }

    public void setParams(Set<CommandParam> params) {
        this.params = params;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
