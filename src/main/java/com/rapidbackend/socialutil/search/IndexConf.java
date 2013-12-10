package com.rapidbackend.socialutil.search;

import java.util.List;

public class IndexConf {
    protected List<String> propertyToIndex;
    public List<String> getPropertyToIndex() {
        return propertyToIndex;
    }
    public void setPropertyToIndex(List<String> propertyToIndex) {
        this.propertyToIndex = propertyToIndex;
    }
}
