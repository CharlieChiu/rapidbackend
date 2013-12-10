package com.rapidbackend.socialutil.monitor;

import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.context.AppContextAware;




public class RapidbackendMBeanServer extends AppContextAware{
    private static final Logger logger = LoggerFactory.getLogger(RapidbackendMBeanServer.class
            .getName());
    ConcurrentHashMap<String,SocialServiceMBean> serviceMBeans = new ConcurrentHashMap<String,SocialServiceMBean>();
    ConcurrentHashMap<String,SocialHandlerMBean> handerMBeans = new ConcurrentHashMap<String,SocialHandlerMBean>();
    private static String DEFAULT_DOMAIN = "Rapidbackend";
    private MBeanServer mBeanServer = null;
    private JMXConnectorServer jmxConnectorServer = null;
    
    private boolean enableJmx = false;
    
    private static RapidbackendMBeanServer instance = null;
    
    
    
    /**
     * @return the enableJmx
     */
    public boolean isEnableJmx() {
        return enableJmx;
    }

    /**
     * @param enableJmx the enableJmx to set
     */
    public void setEnableJmx(boolean enableJmx) {
        this.enableJmx = enableJmx;
    }

    /**
     * @return the mBeanServer
     */
    public MBeanServer getmBeanServer() {
        return mBeanServer;
    }

    /**
     * @param mBeanServer the mBeanServer to set
     */
    public void setmBeanServer(MBeanServer mBeanServer) {
        this.mBeanServer = mBeanServer;
    }

    /**
     * @return the jmxConnectorServer
     */
    public JMXConnectorServer getJmxConnectorServer() {
        return jmxConnectorServer;
    }

    /**
     * @param jmxConnectorServer the jmxConnectorServer to set
     */
    public void setJmxConnectorServer(JMXConnectorServer jmxConnectorServer) {
        this.jmxConnectorServer = jmxConnectorServer;
    }
    
    public synchronized static RapidbackendMBeanServer getInstance() {
        if(instance == null){
            instance = new RapidbackendMBeanServer();
        }
        return instance;
    }

    private RapidbackendMBeanServer(){
        try{
            MBeanConfig conf = (MBeanConfig)getApplicationContext().getBean("mBeanConfig");
            String seveiceURL = conf.getServiceUrl();
            boolean enableJmx = conf.isJmxEnabled();
            int port = conf.getPort();
            if(enableJmx){
                logger.info("trying to start jmx server");
                           
                
                mBeanServer = ManagementFactory.getPlatformMBeanServer();
                Registry registry = null;
                try {
                    registry = LocateRegistry.getRegistry(port);
                    registry.list();
                } catch (Exception e) {
                    registry = null;
                }
                if (null == registry) {
                    registry = LocateRegistry.createRegistry(port);
                }
                JMXServiceURL jmxServiceURL = new JMXServiceURL(seveiceURL);
                jmxConnectorServer = JMXConnectorServerFactory.
                        newJMXConnectorServer(jmxServiceURL, null, mBeanServer);
                jmxConnectorServer.start();
                logger.info("jmx server started on "+jmxServiceURL);
            }
        }catch(Exception e){
            logger.error("",e);
            throw new RuntimeException("Cannot start jmx monitor",e);
        }
    }
    /**
     * register an MBean object to this server
     * @param mbean which should implement SocialHandlerMBean, or SocialServiceMBean
     * @throws Exception
     */
    public void register(Object mbean) throws Exception{
        if(mbean instanceof SocialHandlerMBean){
            SocialHandlerMBean socialHandlerMBean = (SocialHandlerMBean)mbean;
            handerMBeans.put(socialHandlerMBean.getName(), socialHandlerMBean);
            StandardMBean standardMBean = new StandardMBean(socialHandlerMBean, SocialHandlerMBean.class);
            registerMBean(socialHandlerMBean,standardMBean);
        }else if(mbean instanceof SocialServiceMBean){
            SocialServiceMBean socialServiceMBean = (SocialServiceMBean)mbean;
            serviceMBeans.put(socialServiceMBean.getName(), socialServiceMBean);
            StandardMBean standardMBean = new StandardMBean(socialServiceMBean, SocialServiceMBean.class);
            registerMBean(socialServiceMBean,standardMBean);
        }else{
            discard(mbean,"register");
        }
    }
    public  <T> void registerMBean(SocialMBean socialMBean,StandardMBean standardMBean){
        try{
            ObjectName name = getObjectName(socialMBean);
            if(mBeanServer.isRegistered(name)){
                mBeanServer.unregisterMBean(name);
            }
            mBeanServer.registerMBean(standardMBean, name);
            
        }catch(Exception e){
            logger.error("fail to register mbean",socialMBean.getName());
            logger.error("",e);
        }
        
    }
    public void unregisterMBean(SocialMBean socialMBean){
        try{
            ObjectName name = getObjectName(socialMBean);
            if(mBeanServer.isRegistered(name)){
                mBeanServer.unregisterMBean(name);
            }else{
                logger.info("this mbean has not been registered"+socialMBean.getName());
            }
        }catch(Exception e){
            logger.error("fail to unregister mbean",socialMBean.getName());
            logger.error("",e);
            throw new BackendRuntimeException(BackendRuntimeException.INTERNAL_SERVER_ERROR,"fail to unregister mbean",e);
        }
    }
    /**
     * unregister a MBean from the server
     * @param mbean
     */
    public void unregister(Object mbean){
        if(mbean instanceof SocialHandlerMBean){
            SocialHandlerMBean socialHandlerMBean = (SocialHandlerMBean)mbean;
            handerMBeans.remove(socialHandlerMBean.getName(), socialHandlerMBean);
            unregisterMBean(socialHandlerMBean);
        }else if(mbean instanceof SocialServiceMBean){
            SocialServiceMBean socialServiceMBean = (SocialServiceMBean)mbean;
            serviceMBeans.remove(socialServiceMBean.getName(), socialServiceMBean);
            unregisterMBean(socialServiceMBean);
        }else{
            discard(mbean,"unregister");
        }
    }
    /**
     * 
     * @param socialMBean
     * @return
     * @throws Exception
     */
    ObjectName getObjectName(SocialMBean socialMBean) throws Exception{
        Hashtable<String , String> table = new Hashtable<String,String>();
        table.put("type", socialMBean.getName());
        return ObjectName.getInstance(DEFAULT_DOMAIN, table);
    }
    /**
     * 
     * @param object
     * @param caller method which calls discard
     */
    public void discard(Object object,String caller){
        logger.error(caller+": discarding object, not a valid mbean : "+object.toString());
    }
    
    public void stop(){
        try {
            jmxConnectorServer.stop();
        } catch (Exception e) {
            logger.error("error during stop JMX MBean server", e);
        }
        
    }
}
