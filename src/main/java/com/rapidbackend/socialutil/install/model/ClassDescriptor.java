package com.rapidbackend.socialutil.install.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.rapidbackend.socialutil.install.dbinstall.ModelField;

public class ClassDescriptor {
    private String name;
    private List<FieldDescriptor> customFields;
    private String rootClass;
    private ModelType modelType;
    
    public ModelType getModelType() {
        return modelType;
    }
    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<FieldDescriptor> getCustomFields() {
        return customFields;
    }
    public void setCustomFields(List<FieldDescriptor> customFields) {
        this.customFields = customFields;
    }
    public String getRootClass() {
        return rootClass;
    }
    public void setRootClass(String rootClass) {
        this.rootClass = rootClass;
    }
    /**
     * 
     * @param name name of the datamodel, notice the first character will be converted to upper class to comply with Java class name convention
     * @param type
     * @param fields custom fields
     */
    public ClassDescriptor(String name,ModelType type,List<ModelField> fields){
        this.name = StringUtils.capitalize(name);
        Assert.notNull(name,"model name shouldn't be null");
        customFields = toFieldDescriptors(fields);
        modelType =type;
        
    }
    
    public static List<FieldDescriptor> toFieldDescriptors(List<ModelField> fields){
        List<FieldDescriptor> result = new ArrayList<FieldDescriptor>();
        if(null!= fields){
            for(ModelField field:fields){
                String name = field.getName();
                String type = field.getType();
                Assert.notNull(name,"filedName shouldn't be null");
                Assert.notNull(type,"filed type shouldn't be null");
                String javatype = JavaModelGenerator.getJavaTypeMapper().get(type);
                Assert.notNull(javatype,"java type shouldn't be null for type "+type);
                
                result.add(new FieldDescriptor(name, javatype,field.isNotNull()));
            }
        }
        
        return result;
    }
}
