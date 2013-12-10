package com.rapidbackend.socialutil.dao.util;

import org.apache.commons.beanutils.BeanUtils;

public class ModelBeanUtil {
    public static void setBeanCreationTime(Object bean) throws Exception{
        BeanUtils.copyProperty(bean, "created", System.currentTimeMillis());
    }
    
    public static void setBeanModifiedTime(Object bean) throws Exception{
        BeanUtils.copyProperty(bean, "modified", System.currentTimeMillis());
    }
    
    public static void initBeanTimestamps(Object bean) throws Exception{
        setBeanCreationTime(bean);
        setBeanModifiedTime(bean);
    }
}
