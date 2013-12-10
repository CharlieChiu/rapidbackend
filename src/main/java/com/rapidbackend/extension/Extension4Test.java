package com.rapidbackend.extension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.rapidbackend.util.general.Tuple;

public class Extension4Test extends Extension{
    
    protected HashMap<String, String> commandMapping = new HashMap<String, String>();
    /**
     * init all services related to this extension
     */
    public void initServices(){
        return ;
    }
    
    public void setCommandMapping(HashMap<String, String> commandMapping) {
        this.commandMapping = commandMapping;
    }

    /**
     * 
     * @return the request-> commmand mapping
     */
    public HashMap<String, String> getCommandMapping(){
         return commandMapping;
    }
    
    private static Extension4Test instance = new Extension4Test(new String[0],new Tuple<String, String>("",""));
    
    public static void setInstance(Extension4Test instance) {
        Extension4Test.instance = instance;
    }

    public static Extension4Test getInstance(){
        return instance;
    }
    
    public Extension4Test(String[] springContexts,Tuple<String, String>... pathToCommandMappings){
        for(Tuple<String, String> commandTuple:pathToCommandMappings){
            commandMapping.put(commandTuple.getLeft(), commandTuple.getRight());
        }
        ExtensionDescriptor extensionDescriptor = new ExtensionDescriptor();
        extensionDescriptor.setExtentionName("Test");
        Set<String> contexts = new HashSet<String>();
        
        for(String context:springContexts){
           contexts.add(context);
        }
        setExtensionDescriptor(extensionDescriptor);
        extensionDescriptor.setSpringContextFiles(contexts);
    }
    
    public static void main(String[] args){
        
        Extension4Test extension = new Extension4Test(new String[]{"src/test/resources/config/override/testRequestSchema.xml"},
                new Tuple<String, String>("a","/a"));
        extension.destroy();
                
    }
}
