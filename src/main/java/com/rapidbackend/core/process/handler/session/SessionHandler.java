package com.rapidbackend.core.process.handler.session;

import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.process.RequestHandlerBase;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.core.request.CommandParam.ParamDataType;
import com.rapidbackend.security.session.SessionConf;
import com.rapidbackend.security.session.SessionStore;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public abstract class SessionHandler extends RequestHandlerBase{
    
    protected SessionStore sessionStore;
    protected static SessionConf sessionConf;
    
    protected String sessionIdParamName = ParamNameUtil.SESSION_ID;
    
    public String getSessionIdParamName() {
        return sessionIdParamName;
    }
    public void setSessionIdParamName(String sessionIdParamName) {
        this.sessionIdParamName = sessionIdParamName;
    }
    public SessionStore getSessionStore() {
        return sessionStore;
    }
    public StringParam getSessionIdParam(CommandRequest request){
        CommandParam param = request.getParam(sessionIdParamName);
        if(param!=null && param.getParamDataType()==ParamDataType.String){
            return (StringParam) param;
        }else {
            return null;
        }
    }
    @Required
    public void setSessionStore(SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }
    
    public static SessionConf getSessionConf() {
        if(sessionConf ==null){
            sessionConf = (SessionConf)getApplicationContext().getBean("SessionConf");
        }
        return sessionConf;
    }
    
}
