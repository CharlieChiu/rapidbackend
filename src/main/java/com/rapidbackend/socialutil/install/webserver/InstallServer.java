package com.rapidbackend.socialutil.install.webserver;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstallServer {
    Logger logger = LoggerFactory.getLogger(getClass());
    
    public static String contextPath = "/";
    public static String resurceBaseFoler = "src/main/resources/install/web";
    public static Integer port = 10888;
    
    protected Server server;
    
    public InstallServer(){
        try {
            WebAppContext context = new WebAppContext();
            context.setContextPath(contextPath);
            context.setResourceBase(resurceBaseFoler);
            
            server = new Server(port);
            server.setHandler(context);
            System.out.println("Starting server...");
            server.start();
            
        } catch (Exception e) {
            logger.error("error creating install server",e);
            System.exit(10);
        }
        
    }
    
    public static void main(String[] arges){
        InstallServer installServer = new InstallServer();
    }
}
