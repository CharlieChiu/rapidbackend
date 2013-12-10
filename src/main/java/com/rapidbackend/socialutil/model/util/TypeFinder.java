package com.rapidbackend.socialutil.model.util;

import com.rapidbackend.socialutil.install.dbinstall.DaoGenerator;

public abstract class TypeFinder {
    public static TypeFinder getDefaultTypeFinderInstance() throws Exception{
        return (TypeFinder)Class.forName(DaoGenerator.modelTypeFinderClass).newInstance();
    }
    public abstract Class<?> getModelClass(String className);
    public abstract Class<?>[] getKnownModelClasses();
}
