package com.rapidbackend.socialutil.dao;

import com.rapidbackend.core.model.DbRecord;
/**
 * for query test only
 * @author chiqiu
 *
 */
public class Testmodel extends DbRecord{
    
    private Integer intField;
    private String charField;
    private String textField;
    private Float floatField;
    private Long longField;
    private Long where;
    private String varchar;
    
    
    public Long getWhere() {
        return where;
    }
    public void setWhere(Long where) {
        this.where = where;
    }
    public String getVarchar() {
        return varchar;
    }
    public void setVarchar(String varchar) {
        this.varchar = varchar;
    }
    public Integer getIntField() {
        return intField;
    }
    public void setIntField(Integer intField) {
        this.intField = intField;
    }
    public String getCharField() {
        return charField;
    }
    public void setCharField(String charField) {
        this.charField = charField;
    }
    public String getTextField() {
        return textField;
    }
    public void setTextField(String textField) {
        this.textField = textField;
    }
    public Float getFloatField() {
        return floatField;
    }
    public void setFloatField(Float floatField) {
        this.floatField = floatField;
    }
    public Long getLongField() {
        return longField;
    }
    public void setLongField(Long longField) {
        this.longField = longField;
    }
    
}
