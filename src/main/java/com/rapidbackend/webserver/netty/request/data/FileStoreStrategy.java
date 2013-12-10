package com.rapidbackend.webserver.netty.request.data;

import java.io.IOException;

import org.jboss.netty.handler.codec.http.multipart.FileUpload;

public interface FileStoreStrategy {
    
    public String store(FileUpload attachment) throws IOException;
}
