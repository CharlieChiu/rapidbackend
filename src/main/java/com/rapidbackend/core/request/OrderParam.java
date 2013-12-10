package com.rapidbackend.core.request;

import org.apache.commons.lang3.StringUtils;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.dao.util.MysqlUtil;
import com.rapidbackend.socialutil.util.ParamNameUtil;

public class OrderParam extends StringParam{
    private String col;
    private String order;
    
    public OrderParam(StringParam param){
        this(param.getData());
    } 
    
    public OrderParam(String data){
        super(ParamNameUtil.QUERY_ORDER,data);
        try {
            boolean valid = false;
            String[] array = data.split(",");
            if(array.length ==2){
                setCol(array[0]);
                setOrder(array[1]);
            }
            if(!valid){
                throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"error parsing order data");
            }
        } catch (Exception e) {
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"error parsing order:"+data);
        }
    }

    public String getCol() {
        return col;
    }

    public void setCol(String col) {
        if(StringUtils.isEmpty(col)){
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"error parsing order data, no column defined");
        }
        
        this.col = col;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        if(order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc")){
            this.order = order;
        }else {
            throw new BackendRuntimeException(BackendRuntimeException.BAD_REQUEST,"error parsing order:"+order);
        }
    }
    @Override
    public String toString(){
        return "order by "+ MysqlUtil.escape(col)+" "+order;
    }
}
