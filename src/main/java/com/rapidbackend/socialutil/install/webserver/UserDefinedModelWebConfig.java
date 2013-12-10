package com.rapidbackend.socialutil.install.webserver;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.rapidbackend.socialutil.install.dbinstall.ModelField;

public class UserDefinedModelWebConfig {
    private List<ModelField> modelConfig;
    private String modelName;
    
    public List<ModelField> getModelConfig() {
        return modelConfig;
    }
    public void setModelConfig(List<ModelField> modelConfig) {
        this.modelConfig = modelConfig;
    }
    public String getModelName() {
        return modelName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public void mapModelFieldTypes() throws IOException{
        String errMsg = "error creating fields for user defined model: "+modelName;
        for(ModelField field : modelConfig){
            String dataType = InstallServlet.getTypeMapping().get(field.getType());
            if(StringUtils.isEmpty(dataType)){
                throw new RuntimeException(errMsg+"dataType is not setting correctly for field "+ field.getName());
            }
            field.setDataType(dataType);
        }
    }
    
    private String installSql;

    public String getInstallSql() {
        return installSql;
    }
    public void setInstallSql(String installSql) {
        this.installSql = installSql;
    }
    
    
}
