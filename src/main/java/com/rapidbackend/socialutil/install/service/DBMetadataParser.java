package com.rapidbackend.socialutil.install.service;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.springframework.util.Assert;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.rapidbackend.socialutil.install.dbinstall.ModelField;
import com.rapidbackend.socialutil.install.dbinstall.DOMUtil;
import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;
/**
 * @author chiqiu
 *
 */
public class DBMetadataParser {
    
    XPathFactory xPathFactory = XPathFactory.newInstance();
    Connection con;
    
    public DBMetadataParser() throws Exception{
        FileReader dbConfigreader =  new FileReader(new File(DbInstaller.userDBConfig));
        InputSource config = new InputSource(dbConfigreader);
        XPath xPath = xPathFactory.newXPath();
        Node root = (Node)xPath.evaluate("/database", config,XPathConstants.NODE);
        Assert.notNull(root,"database node shouldn't be null");
        
        String dburi = DOMUtil.getChild(root, "dbUri").getTextContent().trim();
        String userName = DOMUtil.getChild(root, "userName").getTextContent().trim();
        String password = DOMUtil.getChild(root, "password").getTextContent().trim();
        String dbName = DOMUtil.getChild(root, "dbName").getTextContent().trim();
        
        DbConfigParser sqlScriptGenerator = new DbConfigParser();
        
        sqlScriptGenerator.parseSettingAndCreateDbScript();
                
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(dburi +dbName,userName,password);
    }
    
    public List<ModelField> getModelTableCollumns(Class<?> model) throws Exception{
        String tableName = model.getSimpleName();
        String queryString = "select * from "+ tableName.toLowerCase() + " where id < 0";
        ResultSet resultSet = con.createStatement().executeQuery(queryString);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        
        int columnCount = resultSetMetaData.getColumnCount();
        
        for(int i=0;i<columnCount;i++){
            System.out.print(resultSetMetaData.getColumnName(i + 1) + " \t");
            System.out.print(resultSetMetaData.isNullable(i+1) + "\t");
            System.out.println(resultSetMetaData.getColumnTypeName(i + 1));
        }
        
        return null;
    }
    
    public static void main(String[] args) throws Exception{
        //DBMetadataParser dbMetadataParser = new DBMetadataParser();
        //dbMetadataParser.getModelTableCollumns(User.class);
        
    }
}
