package com.rapidbackend.extension;


import java.util.Set;

public class ExtensionDescriptor {
    private String extentionName;
    private String extentionBean;
    private Set<String> springContextFiles;
    
    public String getExtentionName() {
        return extentionName;
    }
    public void setExtentionName(String extentionName) {
        this.extentionName = extentionName;
    }
    public Set<String> getSpringContextFiles() {
        return springContextFiles;
    }
    public void setSpringContextFiles(Set<String> springContextFiles) {
        this.springContextFiles = springContextFiles;
    }
    public String getExtentionBean() {
        return extentionBean;
    }
    public void setExtentionBean(String extentionBean) {
        this.extentionBean = extentionBean;
    }
}
