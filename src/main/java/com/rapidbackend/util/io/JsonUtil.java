package com.rapidbackend.util.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import com.rapidbackend.socialutil.install.dbinstall.ModelField;
/**
 * 
 * @author chiqiu
 *
 */
public class JsonUtil {
    
    protected static ObjectMapper mapper = new ObjectMapper(new MappingJsonFactory());
    
    
    static {
        //mapper.configure(f, state)
        mapper.configure(SerializationConfig.Feature.USE_ANNOTATIONS,true);
        mapper.getDeserializationConfig().addHandler(new DefaultDeserializationProblemHandler());
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    
    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static byte[] writeObject(Object o) throws IOException{
        byte[] json =  mapper.writeValueAsBytes(o);
        return json;
    }
    
    public static String writeObjectAsString(Object o) throws IOException{
        byte[] json =  mapper.writeValueAsBytes(o);
        return new String(json);
    }
        
    public static <T> T readObject(byte[] data, Class<T> clazz) throws IOException, JsonParseException{
        JsonParser jParser = mapper.getJsonFactory().createJsonParser(data);
        return jParser.readValueAs(clazz);
    }
    
    public static <T> T readObject(String data, Class<T> clazz) throws IOException, JsonParseException{
        JsonParser jParser = mapper.getJsonFactory().createJsonParser(data);
        return jParser.readValueAs(clazz);
    }
    
    public static <T> T readObject(File file, TypeReference<T> typeReference) throws IOException{
        String configJson = FileUtils.readFileToString(file,CharsetUtil.UTF8);
        JsonParser parser = mapper.getJsonFactory().createJsonParser(configJson);
        JsonNode root = mapper.readTree(parser);
        return mapper.readValue(root, typeReference);
    }
    /**
     * 
     * @param data
     * @param objectClass
     * @param listMemberClass
     * @throws IOException
     */
    public static Object readListGenericObject(String data, Class<?> objectClass,Class<?> listMemberClass) throws IOException{
        JsonParser parser = mapper.getJsonFactory().createJsonParser(data);
        JsonNode root = mapper.readTree(parser);
        JavaType listType = mapper.getTypeFactory().constructCollectionLikeType(List.class, listMemberClass);
        JavaType type = mapper.getTypeFactory().constructSimpleType(objectClass, new JavaType[]{listType});
        return mapper.readValue(root, type);
    }
    
    public static <T> T readObject(String data, TypeReference<T> typeReference) throws IOException{
        JsonParser parser = mapper.getJsonFactory().createJsonParser(data);
        JsonNode root = mapper.readTree(parser);
        return mapper.readValue(root, typeReference);
    }
    
    public static String writeObjectPretty(Object o) throws IOException{
        ObjectWriter objectWriter = mapper.writerWithDefaultPrettyPrinter();
        return objectWriter.writeValueAsString(o);
    }
    
    public static void writeObjectPretty(Object o, File file) throws IOException{
        FileUtils.write(file, writeObjectPretty(o));
    }
    
    public static class ExceptionEssensial{
        protected String exception;
        
    }
    
    public static void main(String[] args) throws Exception{
        List<ModelField> requiredFields = JsonUtil.readObject(new File("src/main/resources/install/web/js/modelconfig/user.js"), new TypeReference<List<ModelField>>() {
        });
        requiredFields.get(0);
    }
    
}