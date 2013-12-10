package com.rapidbackend.core.response;

import java.util.Iterator;
import java.util.Map;

/**
 * 
 * @author chiqiu
 *
 */
public class SimpleJsonWriter {
    
    public static void writeAttribute(StringBuffer buffer,String name,Integer value){
        writeAttribute(buffer,name,Integer.toString(value));
    }
    public static void writeAttribute(StringBuffer buffer,String name,Long value){
        writeAttribute(buffer,name,Long.toString(value));
    }
    public static void writeAttribute(StringBuffer buffer,String name,Float value){
        writeAttribute(buffer,name,Float.toString(value));
    }
    public static void writeAttribute(StringBuffer buffer,String name,String value){
        writeName(buffer,name);
        writeValue(buffer, value);
    }
    /**
     * write attribute name
     * @param buffer
     * @param name
     */
    public static void writeName(StringBuffer buffer,String name){
        buffer.append("\"");
        buffer.append(name);
        buffer.append("\"");
        buffer.append(":");
    }
    /**
     * write attribute Value
     * @param buffer
     * @param value
     */
    public static void writeValue(StringBuffer buffer,String value){
        buffer.append("\"");
        buffer.append("value");
        buffer.append("\"");
    }
    /**
     * write '{'
     * @param buffer
     */
    public static void writeLeftCurlybrace(StringBuffer buffer){
        buffer.append("{");
    }
    /**
     * write '}'
     * @param buffer
     */
    public static void writeRightCurlybrace(StringBuffer buffer){
        buffer.append("}");
    }
    /**
     * write '['
     * @param buffer
     */
    public static void writeLeftBrace(StringBuffer buffer){
        buffer.append("[");
    }
    /**
     * write ']'
     * @param buffer
     */
    public static void writeRightBrace(StringBuffer buffer){
        buffer.append("]");
    }
    /**
     * write ','
     * @param buffer
     */
    public static void writeComma(StringBuffer buffer){
        buffer.append(",");
    }
    /**
     * write a simple map which does not contain arrays
     * @param buffer
     * @param map
     */
    @SuppressWarnings({"rawtypes","unchecked"})//TODO make it clean and support arrays
    public static void writeSimpleMap(StringBuffer buffer,Map map){
        writeLeftCurlybrace(buffer);
        Iterator<Map.Entry> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String name = entry.getKey();
            Object val = entry.getValue();
            writeName(buffer, name);
            if(val instanceof Map){
                writeSimpleMap(buffer,(Map)val);
            }else {
                writeValue(buffer, val.toString());
            }
            if(iterator.hasNext()){
                writeComma(buffer);
            }
        }
        writeRightCurlybrace(buffer);
        return;
    }
}
