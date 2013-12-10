package com.rapidbackend.core.process.handler.session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.security.session.SessionBase;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class SessionDeletionHandler extends SessionHandler{
    public static String idParamName = ParamNameUtil.ID;
    public static String sessionIdParamName = ParamNameUtil.SESSION_ID;
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
        StringParam sessionId = getSessionIdParam(request);
        
        if(sessionId == null || StringUtils.isEmpty(sessionId.getData())){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"no sessionid in request, no session to remove");
        }
        
        SessionBase session = request.getSession(true);
        session.setSessionId(sessionId.getData());
        
        if(session != null){
            try {
                if(session!=null && sessionShardingkeyFactory !=null){
                    boolean result = sessionShardingkeyFactory.setShardingkey(request, session);
                    if(!result){
                        logger.info("failed to handle session for request "+ request.toString());
                    }
                }
                sessionStore.delete(session);
            } catch (Exception e) {
                throw new BackendRuntimeException(BackendRuntimeException.InternalServerError, "error saving session store", e);
            }
        }
        
    }

}
