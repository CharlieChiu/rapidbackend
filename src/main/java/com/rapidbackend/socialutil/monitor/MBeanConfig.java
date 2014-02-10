package com.rapidbackend.socialutil.monitor;

public class MBeanConfig {
    protected static final int DEFAULT_PORT = 7119;
    protected static final String DEFAULT_HOSTNAME="localhost";
    protected static final String DEFAULT_SERVICENAME="service:jmx:rmi:///jndi/rmi://127.0.0.1:7119/RapidbackendMBeanServer";
    boolean jmxEnabled = false;
    int port = DEFAULT_PORT;
    String hostname = DEFAULT_HOSTNAME;
    String serviceUrl = DEFAULT_SERVICENAME;
    /**
     * @return the jmxEnabled
     */
    public boolean isJmxEnabled() {
        return jmxEnabled;
    }
    /**
     * @param jmxEnabled the jmxEnabled to set
     */
    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }
    
    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }
    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    /**
     * @return the serviceUrl
     * TODO need use hostname?
     */
    public String getServiceUrl() {
        return serviceUrl;
    }
    /**
     * @param serviceUrl the serviceUrl to set
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
