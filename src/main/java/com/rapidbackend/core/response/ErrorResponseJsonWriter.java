package com.rapidbackend.core.response;

import java.util.HashMap;

import com.rapidbackend.core.BackendRuntimeException;

/**
 * utility class to convert an error response to json string
 * @author chiqiu
 *
 */
public class ErrorResponseJsonWriter extends SimpleJsonWriter{
    /**
     * @param exception
     * @return
     */
    public String toJsonString(BackendRuntimeException exception){
        StringBuffer buffer = new StringBuffer("");
        HashMap<String, Object> wrapper = new HashMap<String, Object>(1);
        HashMap<String, Object> resutls = new HashMap<String, Object>(7);
        wrapper.put("result", resutls);
        resutls.put("date", exception.getUnixTimestamp());
        resutls.put("code", exception.getErrorCode());
        resutls.put("description", exception.getDetailedInfo());
        writeSimpleMap(buffer, wrapper);
        return buffer.toString();
    }
    
}
