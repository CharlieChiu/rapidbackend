package com.rapidbackend.socialutil.dao;

import static com.rapidbackend.client.http.HttpCommandHelper.LocalHost;
import static com.rapidbackend.client.http.HttpCommandHelper.createHttpGet;
import static com.rapidbackend.client.http.HttpCommandHelper.parseListResult;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.embedded.CommandResult;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.request.QueryParam;
import com.rapidbackend.core.request.QueryParam.QueryPartial;
import com.rapidbackend.core.request.StringParam;
import com.rapidbackend.extension.Extension4Test;
import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;
import com.rapidbackend.socialutil.util.ParamNameUtil;
import com.rapidbackend.util.general.Tuple;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestQuery extends RapidbackendTestBase{
    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void beforeClass() throws Exception{
        try {
            System.setProperty("testing", "true");
            Extension4Test extension = new Extension4Test(
                    new String[]{"src/test/resources/config/override/testQuery.xml"}, 
                    new Tuple<String, String>("/QueryTestmodel","QueryTestmodel")
                    );
            Extension4Test.setInstance(extension);
            prepareTest();
            
            //src/test/resources/dbInstall/model/templates/dao/testmodel.ftl
            
            prepareData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    
    static String charPrefix = "charPrefix";
    static String textPrefix = "textPrefixtextPrefixtextPrefixtextPrefixtextPrefixtextPrefix";// TODO
    static Float floatNum = 1.2f;    
    public static void prepareData() throws Exception{
        List<String> currentInstallSql = FileUtils.readLines(new File(DbInstaller.installSqlFilePath));
        DbConfigParser dbConfigParser = new DbConfigParser();
        HashMap<String, Object> variables = dbConfigParser.parseSetting();
        Configuration configuration = new Configuration();
        configuration.setDirectoryForTemplateLoading(new File("src/test/resources/dbInstall/model/templates/dao/"));
        Template template = configuration.getTemplate("testmodel.ftl");
        StringWriter writer = new StringWriter();
        template.process(variables, writer);
        
        currentInstallSql.add(writer.toString());
        
        String newInstallSqlFile = "src/test/resources/config/override/newInstall.sql";
        FileUtils.writeLines(new File(newInstallSqlFile), currentInstallSql,"\n");
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB(newInstallSqlFile);
        
        BaseDao dao = (BaseDao)getApplicationContext().getBean("TestmodelDao");
        
        for(int i=0;i<20;i++){
            Testmodel model = new Testmodel();
            model.setCharField(charPrefix+i);
            model.setFloatField(floatNum);
            model.setIntField(i);
            model.setLongField(new Long(i*2+""));
            model.setTextField(textPrefix);
            model.setVarchar(charPrefix+i);
            model.setWhere(new Long(i*2+""));
            dao.storeNewModelBean(model);
        }
    }
    
    @AfterClass
    public static void tearDown() throws Exception{
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB();// clean database
    }
    
    @Test
    public void testQueryOnSingleField() throws Exception{
        QueryPartial queryPartial = new QueryPartial("intField", ">=", "0");
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryInt = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryInt);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(20, results.size());
    }
    
    @Test
    public void testQueryOnSingleField0() throws Exception{
        QueryPartial queryPartial = new QueryPartial("intField", ">", "0");
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryInt = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryInt);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(19, results.size());
    }
    
    @Test
    public void testQueryOnSingleField1() throws Exception{
        QueryPartial queryPartial = new QueryPartial("intField", "<", "0");
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryInt = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryInt);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(0, results.size());
    }
    
    @Test
    public void testQueryOnSingleField2() throws Exception{
        QueryPartial queryPartial = new QueryPartial("intField", "<=", "0");
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryInt = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryInt);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(1, results.size());
    }
    
    @Test
    public void testQueryOnSingleField3() throws Exception{// test charfield
        QueryPartial queryPartial = new QueryPartial("charField", "=", "charPrefix0");
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(1, results.size());
    }
    
    @Test
    public void testQueryOnSingleField4() throws Exception{// test textfield
        QueryPartial queryPartial = new QueryPartial("textField", "=", textPrefix);
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(20, results.size());
    }
    
    @Test
    public void testQueryOnSingleField5() throws Exception{// test float field
        QueryPartial queryPartial = new QueryPartial("floatField", "=", "1.2");
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(20, results.size());
    }
    
    @Test
    public void testQueryOnSingleField6() throws Exception{// test long field
        QueryPartial queryPartial = new QueryPartial("longField", "=", "2");
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(1, results.size());
    }
    
    @Test
    public void testQueryOnSingleField7() throws Exception{// test long field
        QueryPartial queryPartial = new QueryPartial("longField", "=", "1");
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(0, results.size());
    }
    
    @Test
    public void testQueryOnMultipleField() throws Exception{
        QueryPartial queryPartial = new QueryPartial("longField", ">", "2");
        QueryPartial queryPartial1 = new QueryPartial("intField", "<", "100");
        String query = QueryParam.createQueryString(queryPartial,queryPartial1); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(18, results.size());
    }
    
    @Test
    public void testEscapeKeywords() throws Exception{
        QueryPartial queryPartial = new QueryPartial("where", ">", "2");
        QueryPartial queryPartial1 = new QueryPartial("varchar", "=", "charPrefix0");
        String query = QueryParam.createQueryString(queryPartial,queryPartial1); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(0, results.size());
    }
    @Test
    public void testEscapeKeywords1() throws Exception{
        QueryPartial queryPartial = new QueryPartial("where", ">=", "0");
        QueryPartial queryPartial1 = new QueryPartial("varchar", "=", "charPrefix0");
        String query = QueryParam.createQueryString(queryPartial,queryPartial1); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(1, results.size());
    }
    @Test
    public void testPaging() throws Exception{
        QueryPartial queryPartial = new QueryPartial("where", ">=", "0");
        
        IntParam startParam = new IntParam(ParamNameUtil.START,0);
        IntParam pageParam = new IntParam(ParamNameUtil.PAGE_SIZE,10);        
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param,startParam,pageParam);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(10, results.size());
        assertEquals(new Integer(20), results.get(0).getId());
    }
    @Test
    public void testOrdering() throws Exception{
        QueryPartial queryPartial = new QueryPartial("where", ">=", "0");
        
        IntParam startParam = new IntParam(ParamNameUtil.START,0);
        IntParam pageParam = new IntParam(ParamNameUtil.PAGE_SIZE,10);        
        String query = QueryParam.createQueryString(queryPartial); 
        QueryParam param = new QueryParam(query);
        StringParam order = new StringParam(ParamNameUtil.ID_ORDER, "asc");
        HttpGet queryRequest = createHttpGet(Protocol, LocalHost, "QueryTestmodel", param,startParam,pageParam,order);
        String queryResult = httpClient.getCommandResult(queryRequest);
        logger.debug("result:"+queryResult);
        CommandResult<?> queryCommandResult = parseListResult(queryResult, Testmodel.class);
        List<Testmodel> results = (List<Testmodel>)queryCommandResult.getResult();
        assertFalse(queryCommandResult.isError());
        assertEquals(10, results.size());
        assertEquals(new Integer(1), results.get(0).getId());
    }
}
