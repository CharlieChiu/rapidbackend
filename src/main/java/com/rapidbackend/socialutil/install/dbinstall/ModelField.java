package com.rapidbackend.socialutil.install.dbinstall;
/**
 * 
 * @author chiqiu
 *
 */
public class ModelField {
    
    private String name;
    private boolean createDbIndex = false;
    private String dataType;
    /**
     * the abbreviation of datatype in the configuration file. The datatype in the database persistent layer
     * can be fetched using the getTypemapping method in class InstallServlet 
     */
    private String type;
    private String defaultValue = "";
    private boolean notNull = false;
    private boolean unique = false;
    
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
        
    public boolean isCreateDbIndex() {
        return createDbIndex && allowCreateDbIndex();
    }
    public void setCreateDbIndex(boolean createDbIndex) {
        this.createDbIndex = createDbIndex;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public boolean isNotNull() {
        return notNull;
    }
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }
    public boolean isUnique() {
        return unique;
    }
    public void setUnique(boolean unique) {
        this.unique = unique;
    }
    
    public boolean allowCreateDbIndex(){// DbIndex request on text fields will be ignored
        if(type!=null && !type.equalsIgnoreCase("Text")){
            return true;
        }else {
            return false;
        }
    }
}
