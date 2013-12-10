package com.rapidbackend.socialutil.install.webserver;

public class GenerateModels {
    public static void main(String[] agrs){
        try{
            InstallServlet.installModels();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
