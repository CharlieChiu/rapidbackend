package com.rapidbackend.webserver.netty.request.data;

import java.util.List;

import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import org.springframework.beans.factory.annotation.Required;

import com.rapidbackend.webserver.netty.request.RequestData;

/**
 * @author chiqiu
 */
public class RenameAttachmentFilter extends DefaultRequestDataFilter{
    protected FileStoreStrategy storeStrategy;
    
    public FileStoreStrategy getStoreStrategy() {
        return storeStrategy;
    }
    @Required
    public void setStoreStrategy(FileStoreStrategy storeStrategy) {
        this.storeStrategy = storeStrategy;
    }

    @Override
    public void filter(RequestData requestData) throws Exception{
        List<InterfaceHttpData> datas = requestData.getPostDatas();
        for(InterfaceHttpData data:datas){
            if(data.getHttpDataType()==HttpDataType.FileUpload){
                FileUpload fileUpload = (FileUpload) data;
                String fileName = storeStrategy.store(fileUpload);
                fileUpload.setFilename(fileName);
            }
        }
        //renewRequestBody(requestData);
    }
}
