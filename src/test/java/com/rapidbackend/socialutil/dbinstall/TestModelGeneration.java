package com.rapidbackend.socialutil.dbinstall;

import org.junit.Test;

import com.rapidbackend.socialutil.install.dbinstall.DaoGenerator;

public class TestModelGeneration {
    @Test
    public void genModels() throws Exception{
        System.out.println("Test model generation!");
        DaoGenerator daoGenerator = new DaoGenerator();
        daoGenerator.genModels();
        
        daoGenerator.genDao();
        
        daoGenerator.genModelTypeFinder();
        
        
    }
}
