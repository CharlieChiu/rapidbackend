package com.rapidbackend.webserver.netty;

import org.springframework.beans.factory.annotation.Required;

/**
 * 
 * @author chiqiu
 *
 */
public class NettyConf {
    
    public static int DefaultHttpPort = 8080;
    public static int DefaultHttpsPort = 8443;
    
    protected int maxInitialLineLength = 4096;
    protected int maxHeaderSize = 8192;
    protected int maxChunkSize = 256*1024;
    protected int maxContentLength = 256*1024;
    protected int httpPort = DefaultHttpPort;
    protected int sslPort = DefaultHttpsPort;
    protected String serverName = "rapidbackend";
    protected String sslProtocol = "TLS";
    protected String keyStoreType = "jks";
    protected String keyStorePassword = "changeit";
    protected String certificatePassword = "changeit";
    protected int timeoutSeconds = 60*5;//seconds
    protected boolean useSSL = true;
    protected int customHttpHanderThreadNumber = 10;
    protected boolean forkHandlerThread = true;
    /**
     * the default file folder for static files
     */
    protected String staticFileFolder = "files";
    
    protected String staticFileRequestUri = "/static/";
    
    public String getStaticFileRequestUri() {
        return staticFileRequestUri;
    }
    public void setStaticFileRequestUri(String staticFileRequestUri) {
        if(staticFileRequestUri ==null){
            throw new RuntimeException("staticFileRequestUri cannot be null!");
        }
        if(!staticFileRequestUri.endsWith("/")){
            throw new RuntimeException("staticFileRequestUri must end with a '/', please check your configuration!");
        }
        
        this.staticFileRequestUri = staticFileRequestUri;
    }
    public String getStaticFileFolder() {
        return staticFileFolder;
    }
    public void setStaticFileFolder(String staticFileFolder) {
        this.staticFileFolder = staticFileFolder;
    }
    
    public boolean isForkHandlerThread() {
        return forkHandlerThread;
    }
    public void setForkHandlerThread(boolean forkHandlerThread) {
        this.forkHandlerThread = forkHandlerThread;
    }
    protected UploadConf uploadConf;
    
    public UploadConf getUploadConf() {
        return uploadConf;
    }
    @Required
    public void setUploadConf(UploadConf uploadConf) {
        this.uploadConf = uploadConf;
    }
    /**
     *  please override this filename if you want to use a real domain.
     *  example command to generate this file
     *  keytool -genkey -alias securehttp -keysize 2048 -validity 36500 -keyalg RSA -dname "CN=securehttp" -keypass changeit -storepass changeit -keystore certificate.jks
     */
    protected String sslKeyStoreFile = "src/main/resources/ssl/certificate.jks";
    
    public int getMaxInitialLineLength() {
        return maxInitialLineLength;
    }
    public void setMaxInitialLineLength(int maxInitialLineLength) {
        this.maxInitialLineLength = maxInitialLineLength;
    }
    public int getMaxHeaderSize() {
        return maxHeaderSize;
    }
    public void setMaxHeaderSize(int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }
    public int getMaxChunkSize() {
        return maxChunkSize;
    }
    public void setMaxChunkSize(int maxChunkSize) {
        this.maxChunkSize = maxChunkSize;
    }
    public boolean isUseSSL() {
        return useSSL;
    }
    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }
    public int getMaxContentLength() {
        return maxContentLength;
    }
    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }
    public String getSslKeyStoreFile() {
        return sslKeyStoreFile;
    }
    public void setSslKeyStoreFile(String sslKeyStoreFile) {
        this.sslKeyStoreFile = sslKeyStoreFile;
    }
    public int getHttpPort() {
        return httpPort;
    }
    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }
    public int getSslPort() {
        return sslPort;
    }
    public void setSslPort(int sslPort) {
        this.sslPort = sslPort;
    }
    public String getSslProtocol() {
        return sslProtocol;
    }
    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }
    public String getKeyStoreType() {
        return keyStoreType;
    }
    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
    public String getKeyStorePassword() {
        return keyStorePassword;
    }
    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }
    public String getCertificatePassword() {
        return certificatePassword;
    }
    public void setCertificatePassword(String certificatePassword) {
        this.certificatePassword = certificatePassword;
    }
    public int getCustomHttpHanderThreadNumber() {
        return customHttpHanderThreadNumber;
    }
    public void setCustomHttpHanderThreadNumber(int customHttpHanderThreadNumber) {
        this.customHttpHanderThreadNumber = customHttpHanderThreadNumber;
    }
    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }
    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    public int getServerPort(){
        if(isUseSSL()){
            return sslPort;
        }else {
            return httpPort;
        }
    }
}
