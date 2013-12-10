package com.rapidbackend.util.io;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
/**
 * simple utitly class to create smile format binary json<br>
 * according to benchmarks , smile's data size is comparable with protobuf
 * @author chiqiu
 *
 */
public class SmileJsonUtil {
	protected static ObjectMapper mapper = new ObjectMapper(new SmileFactory());
	
	static {
		//mapper.configure(f, state)
	    mapper.configure(SerializationConfig.Feature.AUTO_DETECT_GETTERS, false);
        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS , true);
        mapper.configure(SerializationConfig.Feature.USE_ANNOTATIONS,true);
	    mapper.getDeserializationConfig().addHandler(new DefaultDeserializationProblemHandler());
	    mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	    
	}
	public static byte[] writeObject(Object o) throws IOException{
		byte[] smile =  mapper.writeValueAsBytes(o);
		return smile;
	}
	public static String writeObjectAsString(Object o) throws IOException{
	    byte[] smile =  mapper.writeValueAsBytes(o);
	    return new String(smile);
    }
	public static <T> T readObject(byte[] data, Class<T> clazz) throws IOException, JsonParseException{
        JsonParser jParser = mapper.getJsonFactory().createJsonParser(data);
        return jParser.readValueAs(clazz);
    }
	
	public static ObjectMapper getMapper() {
		return mapper;
	}
	public static void setMapper(ObjectMapper mapper) {
		SmileJsonUtil.mapper = mapper;
	}
	
}
