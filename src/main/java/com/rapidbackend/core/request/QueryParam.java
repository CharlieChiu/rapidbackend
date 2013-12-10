package com.rapidbackend.core.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.io.JsonUtil;
/**
 * a stringparam used in
 * @author chiqiu
 *
 */
public class QueryParam extends StringParam{
    
    public QueryParam(String query){
        super(ParamNameUtil.MODEL_QUERY,query);
    }
    
    public QueryParam(StringParam queryParam){
        this(queryParam.getData());
    }
    
    public List<QueryPartial> getQueryPartials(){
        try{
            return JsonUtil.readObject(this.getData(), new TypeReference<ArrayList<QueryPartial>>() {
            });
        }catch(IOException e){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"can't parse query: "+getData() ,e);
        }
    }
    
    public static class QueryPartial{
        private String col;
        private String op;
        private String value;
        static String[] queryOperators = new String[]{">=","<=",">","<","="};
        public String getCol() {
            return col;
        }
        public void setCol(String col) {
            this.col = col;
        }
        public String getOp() {
            return op;
        }
        public void setOp(String op) {
            boolean validOp = false;
            for(String o:queryOperators){
                if(o.equals(op)){
                    validOp = true;
                    break;
                }
            }
            if(!validOp){
                throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"unsupprted operator "+op);
            }
            this.op = op;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public QueryPartial(){
            
        }
        public QueryPartial(String col, String op, String value) {
            super();
            this.col = col;
            setOp(op);
            this.value = value;
        }
    }
    /**
     * 
     * @return
     */
    public static String createQueryString(QueryPartial... querypartials) throws IOException{
        if(querypartials!=null ){
            return JsonUtil.writeObjectAsString(querypartials);
        }else {
            return "";
        }
    }
}
