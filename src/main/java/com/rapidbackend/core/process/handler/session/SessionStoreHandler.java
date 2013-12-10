package com.rapidbackend.core.process.handler.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.security.session.SessionBase;

/**
 * When this handler is attached to a pipeline, the session in the 
 * request will be persisted to sessionStore automatically. Attach it to your pipeline only when you need to store or update a session.
 * @author chiqiu
 *
 */
public class SessionStoreHandler extends SessionHandler{
    Logger logger = LoggerFactory.getLogger(SessionStoreHandler.class);
    
    protected SessionShardingkeyFactory sessionShardingkeyFactory;
    
    public SessionShardingkeyFactory getSessionShardingkeyFactory() {
        return sessionShardingkeyFactory;
    }
    @Required
    public void setSessionShardingkeyFactory(
            SessionShardingkeyFactory sessionShardingkeyFactory) {
        this.sessionShardingkeyFactory = sessionShardingkeyFactory;
    }

    @Override
    public void handle(CommandRequest request,CommandResponse response) throws BackendRuntimeException{
        SessionBase session = request.getSession(false);
        if(session != null){
            try {
                if(session!=null && sessionShardingkeyFactory !=null){
                    boolean result = sessionShardingkeyFactory.setShardingkey(request, session);
                    if(!result){
                        logger.info("failed to store session for request "+ request.toString());
                    }
                }
                if(sessionStore.store(session)){
                    response.setSessionId(session.getSessionId());
                }
            } catch (Exception e) {
                throw new BackendRuntimeException(BackendRuntimeException.InternalServerError, "error saving session store", e);
            }
        }
    }
}
