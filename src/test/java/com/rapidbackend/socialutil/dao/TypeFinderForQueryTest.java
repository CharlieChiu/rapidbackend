package com.rapidbackend.socialutil.dao;

import com.rapidbackend.socialutil.model.util.TypeFinder;

public class TypeFinderForQueryTest extends TypeFinder{
    
    protected Class<?>[] knownModelClasses= {
        Testmodel.class};
    @Override
    public Class<?>[] getKnownModelClasses() {
        return knownModelClasses;
    }
    @Override
    public Class<?> getModelClass(String className){
        return Testmodel.class;
    }
}
