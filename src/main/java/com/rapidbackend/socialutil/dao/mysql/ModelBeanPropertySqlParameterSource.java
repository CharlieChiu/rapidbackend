package com.rapidbackend.socialutil.dao.mysql;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class ModelBeanPropertySqlParameterSource extends BeanPropertySqlParameterSource{
    private final BeanWrapper beanWrapper;
    public ModelBeanPropertySqlParameterSource(Object object){
        super(object);
        this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
    }
    @Override
    public boolean hasValue(String paramName) {
        
        return false;
        
    }
}
