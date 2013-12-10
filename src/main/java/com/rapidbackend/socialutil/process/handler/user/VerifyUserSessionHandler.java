package com.rapidbackend.socialutil.process.handler.user;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.process.handler.session.SessionHandler;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.response.CommandResponse;
import com.rapidbackend.security.session.SessionBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;
/**
 * this class verifies if the session sepecified by the user exists.
 * @author chiqiu
 *
 */
public class VerifyUserSessionHandler extends SessionHandler{

    @Override
    public void handle(CommandRequest request, CommandResponse response)
            throws BackendRuntimeException {
        try {
            SessionBase session = request.getSession(false);// session will be null if it doesn't exists
            if(session == null){
                throw new BackendRuntimeException(BackendRuntimeException.NON_AUTHORITATIVE_INFORMATION,"session is null, please aquire the session again");
            }
            if (!sessionStore.exists(session)) {
                throw new BackendRuntimeException(BackendRuntimeException.NON_AUTHORITATIVE_INFORMATION,"session "+session.getSessionId() +" does not exists, please aquire the session again");
            }
            UserBase user = session.getUser();
            request.setUser(user);
            response.setSessionId(session.getSessionId());
        } catch (Exception e) {
            handleException(BackendRuntimeException.INTERNAL_SERVER_ERROR, null, e);
        }
        
    }
}
