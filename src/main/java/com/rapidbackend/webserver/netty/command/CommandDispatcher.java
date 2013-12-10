package com.rapidbackend.webserver.netty.command;

import com.rapidbackend.core.embedded.EmbeddedApi.RawCommand;

public interface CommandDispatcher {
    /**
     * return the command name according to the input uri.
     * If command mapping is not found.An unsupported RawCommand is returned
     * @param uri
     * @return
     */
    public RawCommand dispatchCommand(String uri);
}
