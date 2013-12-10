package com.rapidbackend;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class FatalErrorListener extends RunListener{
    @Override
    public void testFailure(Failure failure) throws Exception{
        String msg = failure.getMessage();
        super.testFailure(failure);
        if(msg.contains(TestcaseBase.FATAL_ERROR)){
            System.out.println(failure.getTrace());
            System.out.println("fatal error happened, please check your configuration!");
            System.exit(100);
        }
    }
}
