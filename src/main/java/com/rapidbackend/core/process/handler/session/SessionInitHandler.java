package com.rapidbackend.core.process.handler.session;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.security.session.SessionBase;
import com.rapidbackend.security.session.SessionFactory;

public class SessionInitHandler extends SessionHandler{
    
      
    
    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        if(getSessionConf().isSessionEnabled()){
            StringParam sessionId = getSessionIdParam(request);
            SessionBase session = null;
            if(sessionId ==null){
                session = SessionFactory.createSession();
            }else {
                session = SessionFactory.createSession(sessionId.getData());
                SessionBase storedSession = sessionStore.load(session);
                if(null == storedSession){
                    session = SessionFactory.createSession();
                }else {
                    session = storedSession;
                }
            }
            
            if(session !=null){
                request.setSession(session);
            }
        }
    }
}
