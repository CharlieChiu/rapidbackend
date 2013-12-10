package com.rapidbackend.socialutil.dbinstall;



import org.junit.Test;

import com.rapidbackend.TestcaseBase;
import com.rapidbackend.socialutil.install.dbinstall.DbConfigParser;
import com.rapidbackend.socialutil.install.dbinstall.DbInstaller;

public class TestSqlScriptGenerator extends TestcaseBase{
	
	@Test
	public void testFreeMarker() throws Exception{
	    DbConfigParser sqlScriptGenerator = new DbConfigParser();
	    sqlScriptGenerator.parseSettingAndCreateDbScript();
	    DbInstaller dbInstaller = new DbInstaller();
	    dbInstaller.installDB();
        //System.out.println("db Install is doing what way");
	}
}
