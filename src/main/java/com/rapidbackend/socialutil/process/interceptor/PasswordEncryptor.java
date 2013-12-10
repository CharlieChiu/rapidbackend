package com.rapidbackend.socialutil.process.interceptor;


import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.context.AppContextAware;
import com.rapidbackend.core.process.interceptor.CommandInterceptor;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.security.shiro.SimpleHashedCredentialsMatcher;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class PasswordEncryptor extends AppContextAware implements CommandInterceptor{
    
    protected SimpleHashedCredentialsMatcher SimpleHashedCredentialsMatcher;
    
    public SimpleHashedCredentialsMatcher getSimpleHashedCredentialsMatcher() {
        return SimpleHashedCredentialsMatcher;
    }
    @Required
    public void setSimpleHashedCredentialsMatcher(
            SimpleHashedCredentialsMatcher simpleHashedCredentialsMatcher) {
        SimpleHashedCredentialsMatcher = simpleHashedCredentialsMatcher;
    }

    @Override
    public boolean agree(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        boolean result = true;
        CommandParam param = request.getParam(ParamNameUtil.PASSWORD);
        if(param !=null){
            StringParam password = (StringParam) param;
            String input = password.getData();
            String hashedPass = SimpleHashedCredentialsMatcher.hashPassword(input);
            password.setText(hashedPass);
        }
        return result;
    }
}
