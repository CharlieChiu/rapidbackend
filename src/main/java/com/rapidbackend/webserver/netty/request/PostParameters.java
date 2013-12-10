package com.rapidbackend.webserver.netty.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.netty.handler.codec.http.multipart.Attribute;
import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.jboss.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

public class PostParameters{
    private HashMap<String, List<String> > attributes = new HashMap<String, List<String>>();
    private HashMap<String, FileUpload> fileUploads = new HashMap<String, FileUpload>();
    
    public HashMap<String, List<String>> getAttributes() {
        return attributes;
    }
    public void setAttributes(HashMap<String, List<String>> attributes) {
        this.attributes = attributes;
    }
    public HashMap<String, FileUpload> getFileUploads() {
        return fileUploads;
    }
    public void setFileUploads(HashMap<String, FileUpload> fileUploads) {
        this.fileUploads = fileUploads;
    }
    
    public PostParameters(List<InterfaceHttpData> postdata) throws IOException{
        for(InterfaceHttpData data:postdata){
            String name = data.getName();
            if(data.getHttpDataType()==HttpDataType.FileUpload){
                fileUploads.put(name, (FileUpload)data);
            }
            if(data.getHttpDataType() == HttpDataType.Attribute){
                List<String> values = attributes.get(name);
                if(values==null){
                    values = new ArrayList<String>();
                }
                Attribute attribute = (Attribute)data;
                values.add(attribute.getValue());
                attributes.put(name, values);
            }
        }
    }
        
    
}