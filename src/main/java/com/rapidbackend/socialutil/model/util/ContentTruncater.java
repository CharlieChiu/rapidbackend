package com.rapidbackend.socialutil.model.util;
/**
 * truncate the content 
 * @author chiqiu
 *
 */
public class ContentTruncater {
    
    protected int contentLengthLimit = 0;
    public int getContentLengthLimit() {
        return contentLengthLimit;
    }
    public void setContentLengthLimit(int contentLengthLimit) {
        this.contentLengthLimit = contentLengthLimit;
    }
    /**
     * 
     * @param content
     * @return
     */
    public String truncate(String content){
        if(contentLengthLimit>0&&content!=null&& content.length()>contentLengthLimit){
            return content.substring(0, contentLengthLimit);
        }else {
            return content;
        }
    }
}
