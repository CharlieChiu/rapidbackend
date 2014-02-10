package com.rapidbackend.socialutil.install.webserver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.socialutil.install.dbinstall.DaoGenerator;
import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;
import com.rapidbackend.socialutil.install.service.ServiceGenerator;
import com.rapidbackend.util.io.JsonUtil;

public class InstallServlet extends HttpServlet{
    static Logger logger = LoggerFactory.getLogger(InstallServlet.class);
    /**
     * 
     */
    private static final long serialVersionUID = -6992009001661467161L;
    protected static final String currentConfigFile = "src/main/resources/install/web/js/currentInstallConfig.js";
    protected static final String fieldTypeConfigFile = "src/main/resources/install/web/js/fieldTypeConfig.js";
    protected static ObjectMapper objectMapper = new ObjectMapper();
    protected static HashMap<String, String> typeMapping;
    
    @Override
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
        
        StringBuffer log = new StringBuffer();
        try {
            
            String webconfigJson = request.getParameter("config");
            WebConfig webConfig = getWebConfig(webconfigJson);// we try to parse the configs to see if we have and exceptions
            DbConfigParser dbConfigParser = new DbConfigParser();
            dbConfigParser.parseSetting(webConfig);
            
            getTypeMapping();
            
            saveCurrentConfig(JsonUtil.writeObjectPretty(webConfig));// no exceptions the config seems good, save it to disk
            
            
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(log.toString());
            response.getWriter().flush();
        } catch (Exception e) {
            System.out.println(e);
            writeError(log.toString(),e,response);
        }
    }
    
    public static void installModels() throws Exception{
        StringBuffer log = new StringBuffer();
        log.append("parse configuration \n <br/>");
        DbConfigParser dbConfigParser = new DbConfigParser();
        dbConfigParser.parseSettingAndCreateDbScript();
        
        log.append("installDb \n <br/>");
        DbInstaller installer = new DbInstaller();
        installer.installDB();
        
        log.append("generating dao objects \n <br/>");
        
        DaoGenerator daoGenerator = new DaoGenerator();
        
        daoGenerator.genModels();
        
        daoGenerator.genDao();
        
        daoGenerator.genModelTypeFinder();
        
        log.append("models created now complies the new java sources");
        
        
        System.out.println(log.toString());
    }
    
    public static void installServices() throws Exception{
        StringBuffer log = new StringBuffer();
                
        log.append("generating services \n <br/>");
        
        ServiceGenerator serviceGenerator = new ServiceGenerator();
        
        log.append("generating params for commands \n <br/>");
        
        serviceGenerator.genParams();
        
        log.append("generating commands for commands \n <br/>");
        
        serviceGenerator.genCommandMappings();
        /*
        log.append("generating crud schema for all models \n <br/>");
        
        serviceGenerator.genCrudRequestSchemas();*/
        
        log.append("generating crud pipelines for all models \n <br/>");
        
        serviceGenerator.genCrudRequestPipelines();
        
        log.append("generating redis configs for all models \n <br/>");
        serviceGenerator.genRedisConfig();
        
        log.append("generating configs for followables \n <br/>");
        
        serviceGenerator.genSocialUtilConfigs();
        serviceGenerator.genSocialUtilServiceConfig();
        serviceGenerator.genSocialUtilSecurityConfig();
        
        log.append("installation done  \n <br/>");
        
        System.out.println(log.toString());
    }
    
    
    public static void saveCurrentConfig(String webconfigJson) throws IOException{
        FileUtils.writeStringToFile(new File(currentConfigFile), webconfigJson);
    }
    
    public static WebConfig getCurrentWebConfig() throws IOException{
        String webconfigJson = FileUtils.readFileToString(new File(currentConfigFile), Charset.forName("UTF-8"));
        return getWebConfig(webconfigJson);
    }
    
    public static WebConfig getWebConfig(String webconfigJson) throws IOException{
        
        JsonParser parser = objectMapper.getJsonFactory().createJsonParser(webconfigJson);
        JsonNode root = objectMapper.readTree(parser);
        WebConfig webConfig = objectMapper.readValue(root, new TypeReference<WebConfig>(){});
        return webConfig;
    }
    
    public static HashMap<String, String> getTypeMapping() throws IOException{
        
        if(typeMapping==null){
            String configJson = FileUtils.readFileToString(new File(fieldTypeConfigFile),Charset.forName("UTF-8"));
            JsonParser parser = objectMapper.getJsonFactory().createJsonParser(configJson);
            JsonNode root = objectMapper.readTree(parser);
            typeMapping =  objectMapper.readValue(root, new TypeReference<HashMap<String, String>>() {});
        }
        
        return typeMapping;
    }
    
    @Override
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
        doGet(request, response);
    }
    
    public void writeError(String msg,Exception e, HttpServletResponse response) throws IOException{
        writeError(msg+e.toString()+"\n" + ExceptionUtils.getStackTrace(e), response);
    }
    
    public void writeError(String e, HttpServletResponse response) throws IOException{
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, StringEscapeUtils.escapeHtml3(e));
    }
    
    
    
}
