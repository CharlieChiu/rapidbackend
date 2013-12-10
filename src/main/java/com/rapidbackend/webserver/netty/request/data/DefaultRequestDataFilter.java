package com.rapidbackend.webserver.netty.request.data;


import com.rapidbackend.webserver.netty.request.RequestData;
/**
 * 
 * @author chiqiu
 * TODO enhance performance for post request, don't have to treat them like get requests. Can pass a map into
 * {@link QueryParameters } directly
 */
public class DefaultRequestDataFilter implements RequestDataFilter{
    @Override
    public void filter(RequestData requestData) throws Exception{
        return;
        //renewRequestBody(requestData);
    }
    @Deprecated
    public void renewRequestBody(RequestData requestData) throws Exception{
        /**
         * commented out because we process post data and get data separately
         * **/
        /* 
        List<InterfaceHttpData> datas = requestData.getPostDatas();
        StringBuilder sb = new StringBuilder();
        if (requestData.getRequestBody()!=null) {
            sb.append(requestData.getRequestBody()).append("&");
        }
        if(datas!=null){
            for(InterfaceHttpData data:datas){
                if(data.getHttpDataType()==HttpDataType.FileUpload){
                    FileUpload fileUpload = (FileUpload) data;
                    sb.append(fileUpload.getName()).append("=");
                    sb.append(fileUpload.getFilename()).append("&");
                }else {
                    HttpData httpData = (HttpData)data;
                    sb.append(URLEncoder.encode(httpData.getName(),"UTF-8")).append("=");
                    sb.append(URLEncoder.encode(httpData.getString(CharsetUtil.UTF_8),"UTF-8")).append("&");
                }
            }
        }
        
        requestData.setRequestBody(sb.toString());*/
    }
    
}
