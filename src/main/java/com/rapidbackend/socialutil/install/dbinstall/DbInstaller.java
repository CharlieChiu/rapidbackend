package com.rapidbackend.socialutil.install.dbinstall;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;


import com.rapidbackend.socialutil.install.webserver.InstallServlet;
import com.rapidbackend.socialutil.install.webserver.WebConfig;

public class DbInstaller {
    
    public static String userDBConfig = "src/main/resources/dbInstall/userconfig/config.xml";
    public static String defaultEncodingParam = "characterEncoding=utf-8";
    public static String installSqlFilePath = "src/main/resources/dbInstall/dbInstall.sql";
            
    public void installDB() throws Exception{
        installDB(installSqlFilePath);
    }
    
    public void installDB(String sqlFilePath) throws Exception{
        /*
        FileReader dbConfigreader =  new FileReader(new File(DbInstaller.userDBConfig));
        InputSource config = new InputSource(dbConfigreader);
        XPath xPath = xPathFactory.newXPath();
        Node root = (Node)xPath.evaluate("/database", config,XPathConstants.NODE);
        Assert.notNull(root,"database node shouldn't be null");
        
        String dburi = DOMUtil.getChild(root, "dbUri").getTextContent().trim();
        String userName = DOMUtil.getChild(root, "userName").getTextContent().trim();
        String password = DOMUtil.getChild(root, "password").getTextContent().trim();
        */
        DbConfigParser sqlScriptGenerator = new DbConfigParser();
        
        sqlScriptGenerator.parseSettingAndCreateDbScript();
         
        WebConfig webConfig = InstallServlet.getCurrentWebConfig();
        
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(webConfig.getDbUri(),webConfig.getUsername(),webConfig.getPassword());
        
        FileInputStream installSqlFile = new FileInputStream(new File(sqlFilePath));
        SQLScriptRunner scriptRunner = new SQLScriptRunner(installSqlFile);
        
        scriptRunner.runScript(con, true);
    }
    
    /**
     * TODO add params to separate the steps
     * @param args
     */
    public static void main(String[] args) throws Exception{
        DbInstaller dbInstaller = new DbInstaller();
        dbInstaller.installDB();
    }
    
}
