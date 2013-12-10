package com.rapidbackend.webserver.netty;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.jboss.netty.handler.codec.http.multipart.DiskFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadConf {
    Logger logger = LoggerFactory.getLogger(UploadConf.class);
    protected boolean deleteOnExitTemporaryFile = true;
    protected String uploadtmpDir = "files/fileupload"; 
    
    public boolean isDeleteOnExitTemporaryFile() {
        return deleteOnExitTemporaryFile;
    }

    public void setDeleteOnExitTemporaryFile(boolean deleteOnExitTemporaryFile) {
        this.deleteOnExitTemporaryFile = deleteOnExitTemporaryFile;
    }

    public String getUploadtmpDir() {
        return uploadtmpDir;
    }

    public void setUploadtmpDir(String uploadtmpDir) {
        this.uploadtmpDir = uploadtmpDir;
    }

    public void config(){
        try {
            File dir = new File(uploadtmpDir);
            if(dir.exists()&& dir.isDirectory()){
            }else {
                FileUtils.deleteQuietly(dir);
                FileUtils.forceMkdir(dir);
            }
        } catch (Exception e) {
            logger.error("error checking upload tmp dir:"+uploadtmpDir,e);
        }
        DiskFileUpload.baseDirectory = uploadtmpDir;
        DiskFileUpload.deleteOnExitTemporaryFile = deleteOnExitTemporaryFile;
    }
    
}
