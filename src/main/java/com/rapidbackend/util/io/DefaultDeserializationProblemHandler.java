package com.rapidbackend.util.io;

import org.codehaus.jackson.map.DeserializationProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ignore all unknown fields.
 * @author chiqiu
 *
 */
public class DefaultDeserializationProblemHandler extends DeserializationProblemHandler{
    Logger logger = LoggerFactory.getLogger(DefaultDeserializationProblemHandler.class);
    public boolean handleUnknownProperty(
            org.codehaus.jackson.map.DeserializationContext ctxt, 
            org.codehaus.jackson.map.JsonDeserializer deserializer,
            java.lang.Object beanOrClass, java.lang.String propertyName
            ) throws java.io.IOException, org.codehaus.jackson.JsonProcessingException{
        logger.debug("unknown json field "+propertyName);
        return false;
    }
}
