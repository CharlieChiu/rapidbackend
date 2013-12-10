package com.rapidbackend.client.http;
import static com.rapidbackend.client.http.HttpCommandHelper.*;

import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.rapidbackend.core.command.DefaultCommands;
import com.rapidbackend.core.request.FileParam;
import com.rapidbackend.core.request.IntListParam;
import com.rapidbackend.core.request.CommandParam;

public class ClientCommandFactory {
    /**
     * 
     * @param host
     * @param fids feed ids
     * @return
     */
    public static HttpUriRequest getFeeds(WebProtocol protocol,String host,IntListParam fids){
        return createHttpGet(protocol,host,DefaultCommands.GetFeeds,fids);
    }
    /**
     * This is the default implementation for post feed, user may need to override this method
     * @param protocol
     * @param host
     * @return
     */
    public static HttpUriRequest postFeed(WebProtocol protocol,String host,
            List<CommandParam> params, FileParam mediaAttachment) throws Exception{
        return createHttpPostWithFile(protocol, host, DefaultCommands.PostFeed, params,mediaAttachment);
    }
    
}
