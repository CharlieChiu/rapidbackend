package com.rapidbackend.socialutil.install;

import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;

/**
 * The install class for socialutil
 * @author chiqiu
 *
 */
@Deprecated
public class Installer {
    
    public static void main(String[] args) throws Exception{
        //System.out.println("install it");
        if(args == null || args.length==0){
            
        }else {
            String option = args[0];
            if(option.equalsIgnoreCase("install")){
                
                System.out.println("============== install database ==============");
                DbInstaller dbInstaller = new DbInstaller();
                dbInstaller.installDB();
                
            }
        }
    }
    
}
