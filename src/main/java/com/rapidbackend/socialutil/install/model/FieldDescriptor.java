package com.rapidbackend.socialutil.install.model;

public class FieldDescriptor {
    private String javaType;
    private String name;
    private boolean notNull = false;
    public String getJavaType() {
        return javaType;
    }
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public FieldDescriptor(String name,String javatype,boolean notNull){
        this.name = name;
        this.javaType = javatype;
        this.notNull = notNull;
    }
    public boolean isNotNull() {
        return notNull;
    }
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }
    
}
