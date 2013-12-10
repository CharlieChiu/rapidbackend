package com.rapidbackend.webserver.netty.request.data;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.springframework.beans.factory.annotation.Required;
/**
 * simply rename attachment to a new file. 
 * TODO GraphicsMagic integration in the future for image handling
 * @author chiqiu
 *
 */
public class SimpleAttachmentRenamer implements FileStoreStrategy{
    
    protected String baseStoreDir;
    
    public String getBaseStoreDir() {
        return baseStoreDir;
    }
    @Required
    public void setBaseStoreDir(String baseStoreDir) {
        this.baseStoreDir = baseStoreDir;
    }
    @Override
    public String store(FileUpload attachment) throws IOException{
        String name = attachment.getFilename();
        String surfix = FilenameUtils.getExtension(name);
        String uuid = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder(64);
        sb.append(baseStoreDir).append("/").append(uuid);
        if(!StringUtils.isBlank(surfix)){
            sb.append(".").append(surfix);
        }
        String newFileName = sb.toString();
        File newFile = new File(newFileName);
        boolean res = attachment.renameTo(newFile);
        if (!res) {
            throw new IOException("failed to move file:"+attachment.toString());
        }
        return newFileName;
    }
}
