package com.rapidbackend.socialutil.install.webserver;
/**
 * only for testing purpose during development, if the configuration has changed, you have to recompile the whole project
 * in eclipse 
 * @author chiqiu
 *
 */
public class StandaloneInstaller {
    public static void main(String[] args) throws Exception{
        InstallServlet.installModels();
        InstallServlet.installServices();
    }
}