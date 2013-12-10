package com.rapidbackend.core.request;

import org.junit.Test;

import com.rapidbackend.TestcaseBase;

public class TestParams extends TestcaseBase{
    @Test
    public void testIntParam(){
        int i = 1;
        IntParam param = new IntParam("param",i);
        assertTrue(i==param.getData());
    }
    @Test
    public void testFloatParam(){
        float f = 5.99999999f;
        FloatParam param =  new FloatParam("param",f);
        assertTrue(f==param.getData());
    }
    @Test
    public void testLongParam(){
        long l = 1231231836783781786l;
        LongParam param = new LongParam("param",l);
        assertTrue(l==param.getData());
    }
    @Test
    public void testStringParam(){
        String string = "test";
        StringParam param = new StringParam("param",string);
        assertTrue(string.equals(param.getData()));
    }
    @Test
    public void testIntListParam(){
        int[] array = {1,2,3,4,5};
        IntListParam param = new IntListParam("param",array);
        assertTrue(param.getData()[4]==array[4]);
    }
}
