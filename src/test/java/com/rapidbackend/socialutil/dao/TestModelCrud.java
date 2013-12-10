package com.rapidbackend.socialutil.dao;

import static com.rapidbackend.client.http.HttpCommandHelper.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.model.DbRecord;
import com.rapidbackend.core.model.util.ModelReflectionUtil;
import com.rapidbackend.core.request.CommandParam;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;
import com.rapidbackend.socialutil.install.dbinstall.ModelInformation;
import com.rapidbackend.socialutil.install.service.ServiceGenerator;
import com.rapidbackend.socialutil.model.data.ModelFactory;
import com.rapidbackend.socialutil.model.util.TypeFinder;
//TODO this test should be kept to test for all models we should use a test spring config xml to test the simple crud against all models
@RunWith(Parameterized.class)
public class TestModelCrud extends RapidbackendTestBase{
    Logger logger = LoggerFactory.getLogger(getClass());
    private Class<?> modelClass;
    
    public  TestModelCrud(Class<?> clazz){
        this.modelClass = clazz;
    }
    @Parameters(name="Test CRUD for Model {0}")
    public static Iterable<Object[]> data() {
            try {
                ServiceGenerator serviceGenerator = new ServiceGenerator();
                
                List<Object[]> resultList = new ArrayList<Object[]>();
                for (Class<?> clazz:serviceGenerator.modelsExposetoCRUDCommands()) {
                    resultList.add(new Object[]{clazz});
                }
                return resultList;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
    
    @BeforeClass
    public static void before()throws Exception{
        print("-----------------> TestModelCrud before");
        prepareTest();
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB();
        print("<----------------- TestModelCrud before");
    }
    
    @AfterClass
    public static void after() throws Exception{
        print("------------------> reinstall database");
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB();
        print("<----------------- TestModelCrud ends");
    }
    @Test
    public void testModelCrud(){
        try {
            //if(modelClass.getSimpleName().equalsIgnoreCase("group")){
                //print();
            //}
            print("--------------> testModelCrud");
            ModelFactory modelFactory = new ModelFactory();
            ModelInformation modelInformation = new ModelInformation();
            DbRecord model = modelFactory.createModel(modelClass);
            String className = model.getClass().getSimpleName();
            
            HttpPost create = createHttpPostWithoutFile(Protocol, LocalHost, "Create"+className, model);
            
            String createResult = httpClient.getCommandResult(create);
            
            logger.debug("createResult for "+className);
            logger.debug(createResult);
            CommandResult<?> createCommandResult = parseResult(createResult, modelClass);
            
            boolean error = createCommandResult.isError();
            assertFalse(error);
           
            Object createdModel = createCommandResult.getResult();
            
            assertTrue(createdModel.getClass() == modelClass);
            /* end of testing create model*/
            
            /* 
             * begin of testing update model*/
            Set<Field> requiredFields = modelInformation.getRequiredFields(modelClass);
            
            Field updatedField = null;
            
            if(requiredFields.size()>0){
                Field[] fields = requiredFields.toArray(new Field[0]);
                updatedField = fields[0];
            }
            
            DbRecord record = (DbRecord) createdModel;
            Object updatedValue = null;
            if(updatedField!=null){
                updatedValue = modelFactory.generateUniqueFieldValue(updatedField, record.getClass());
                if(updatedValue!=null){
                    //ReflectionTools.setField(testField, createdModel, updateValue);
                    ModelReflectionUtil.setPropertyValue(modelClass, updatedField.getName(), updatedValue, record);
                }
            }
            print("--------------> testModelCrud3");
            if(updatedValue==null){//we faied to try to set a required field,now we try to set the 'modified' field
                Field modified = ModelReflectionUtil.getModelField("modified", record.getClass());
                updatedField = modified;
                updatedValue = new Long(System.currentTimeMillis());
                record.setModified((Long)updatedValue);
            }
            print("testing updating field "+ updatedField +"model "+record.getClass());
            
            
            HttpPut updatePut = createHttpPutWithoutFile(Protocol, LocalHost, "Update"+className, record);
            String updateResult = httpClient.getCommandResult(updatePut);
            logger.debug("updateResult for "+className);
            logger.debug(updateResult);
            CommandResult<?> updateCommandResult = parseResult(updateResult, modelClass);
            
            Object updatedModel = updateCommandResult.getResult();
            assertTrue(updatedModel.getClass() == modelClass);
            assertFalse(updateCommandResult.isError());
            
            /**
             *  test reading updated class
             */
            Integer id = record.getId();
            CommandParam idParam = new IntParam("id", id);
            HttpGet readUpdated = createHttpGet(Protocol, LocalHost, "Read"+className, idParam);
            String readUpdatedResult = httpClient.getCommandResult(readUpdated);
            logger.debug("readUpdatedResult for "+className);
            logger.debug(readUpdatedResult);
            Object retrievedModel = parseResult(readUpdatedResult, modelClass).getResult();
            
            Object retrievedValue =  ModelReflectionUtil.getPropertyValue(modelClass, updatedField.getName(), retrievedModel);
            
            assertTrue(retrievedValue.equals(updatedValue));// compare the reterived value and the modified value
            
            /**
             * test delete model
             * 
             */
            HttpDelete delete =  createHttpDelete(Protocol, LocalHost, "Delete"+className, idParam);
            String deleteResult = httpClient.getCommandResult(delete);
            logger.debug("deleteResult for "+className);
            logger.debug(deleteResult);
            CommandResult<?> deleteCommandResult = parseResult(deleteResult, modelClass);
            assertFalse(deleteCommandResult.isError());
            print("<-------------- testModelCrud");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
}
