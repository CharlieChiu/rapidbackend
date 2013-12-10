package com.rapidbackend.socialutil.install.webserver;

public class GenerateServices {
    
    public static void main(String[] agrs){
        try{
            InstallServlet.installServices();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
