package com.rapidbackend.socialutil.dbinstall;

import org.junit.Test;

import com.rapidbackend.socialutil.install.service.ServiceGenerator;

public class TestGenerateServices {
    @Test
    public void testGenRequestParams() throws Exception{
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        
        serviceGenerator.genParams();
        
    }
}
