package com.rapidbackend.core.process.handler.session;

import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.security.session.SessionBase;

public interface SessionShardingkeyFactory {
    /**
     * Sets correct session sharding key for a session
     * @param session
     * @return true if a key was successfully create and set
     */
    public boolean setShardingkey(CommandRequest request,SessionBase session);
}
