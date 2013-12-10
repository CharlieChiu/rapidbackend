package com.rapidbackend.core.request;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.core.request.CommandParam.ParamDataType;
import com.rapidbackend.util.general.ConversionUtils;
import com.rapidbackend.util.general.MsgBuilder;
/**
 * 
 * @author chiqiu
 *
 */
public class ParamFactory {
    protected static Logger logger = LoggerFactory.getLogger(ParamFactory.class);
    public static CommandParam createParam(CommandParam predefinedFormat,List<String> values) throws ParamException{
        CommandParam resultParam = null;
        String name = predefinedFormat.getName();
        ParamDataType type = predefinedFormat.getParamDataType();
        try{
            switch (type) {
            case Int:
                int intval = Integer.parseInt(values.get(0));
                resultParam = new IntParam(name,intval);
                break;
            case Long:
                long longval = Long.parseLong(values.get(0));
                resultParam = new LongParam(name,longval);
                break;
            case String:
                resultParam = new StringParam(name,values.get(0));
                break;
            case Boolean:
                boolean boolval = Boolean.parseBoolean(values.get(0));
                resultParam = new BooleanParam(name,boolval);
                break;
            case IntList:
                resultParam = new IntListParam(name,ConversionUtils.stringCollentionToIntArray(values));
                break;
            case Float:
                float f = Float.parseFloat(values.get(0));
                resultParam = new FloatParam(name,f);
            }
        }catch (Exception e) {
            //TODO
            MsgBuilder msg = new MsgBuilder();
            msg.$("ParamFactory:Error Parsing Param:").$(predefinedFormat.getName()).$(",").$(e.getClass().getSimpleName());
            logger.error(msg.toString());
            throw new ParamException(BackendRuntimeException.InternalServerError,msg.toString(),e);
        }
        return resultParam;
    }
    /**
     * only Integer, String, Float, Long fields are supported
     * This method is used in autowire request schema by model class fields.
     * For now only above four classes are allowed in all the models. 
     * @param field
     * @return
     */
    public static CommandParam createParam(Field field){
        String name = field.getName();
        Class<?> fClass = field.getType();
        CommandParam param = null;
        if(fClass.equals(Integer.class)){
            param = new IntParam();
        }else if (fClass.equals(String.class)) {
            param = new StringParam();
        }else if (fClass.equals(Float.class)) {
            param = new FloatParam();
        }else if (fClass.equals(Long.class)) {
            param = new LongParam();
        }else if (fClass.equals(Boolean.class) || fClass.equals(boolean.class)) {
            param = new BooleanParam();
        }else {
            throw new UnsupportedOperationException(field+" field is not supported yet by ParamFactory.createParam");
        }
        if(param!=null){
            param.setName(name);
        }
        return param;
    }
    public static FileParam createFileParam(FileUpload fileUpload){
        String filename = fileUpload.getFilename();
        String paramName = fileUpload.getName();
        File file = new File(filename);
        FileParam param = new FileParam(paramName, file);
        return param;
    }
    public static CommandParam createParam(Field field,Object value){
        String name = field.getName();
        Class<?> fClass = field.getType();
        CommandParam param = null;
        if(fClass.equals(Integer.class)){
            param = new IntParam(name,(Integer)value);
        }else if (fClass.equals(String.class)) {
            param = new StringParam(name,(String)value);
        }else if (fClass.equals(Float.class)) {
            param = new FloatParam(name,(Float)value);
        }else if (fClass.equals(Long.class)) {
            param = new LongParam(name,(Long)value);
        }else if (fClass.equals(Boolean.class)) {
            param = new BooleanParam(name,(Boolean)value);
        }else {
            throw new UnsupportedOperationException(field+" field is not supported yet by ParamFactory.createParam");
        }
        return param; 
    }
    
    
    public static ParamDataType[] supportedFieldParamType = {ParamDataType.Int,ParamDataType.Float,ParamDataType.String,ParamDataType.Long};
    
    public static boolean isAllowedModelFieldParamType(CommandParam param){
        ParamDataType type = param.getParamDataType();
        for(ParamDataType t:supportedFieldParamType){
            if (type.equals(t)) {
                return true;
            }
        }
        return false;
    }
    /**
     * convert the model into a list of commandParams. Note: any property in the model class without a proper getter will cause a runtime exception
     * @param model
     * @return
     */
    public static List<CommandParam> convertModelToParams(DbRecord model){
        if(model ==null){
            return new ArrayList<CommandParam>();// we never return null
        }
        Field[] fields = ModelReflectionUtil.getModelFields(model.getClass());
        List<CommandParam> results = new ArrayList<CommandParam>();
        for(Field f:fields){
            Object value = ModelReflectionUtil.getPropertyValue(model.getClass(), f.getName(), model);
            if (value!=null) {
                CommandParam param = createParam(f,value);
                results.add(param);
            }
        }
        return results;
    }
    
}
