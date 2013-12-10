package com.rapidbackend.client.http;

public class HttpClientConf {
    protected int socketTimeout = 60*1000*5;
    protected int socketBufferSize = 8*1024;
    protected int connectionTimeout = 60*1000*5;
    protected int httpPort = 8080;
    protected int sslPort = 8443;
    public int getSocketTimeout() {
        return socketTimeout;
    }
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
    public int getSocketBufferSize() {
        return socketBufferSize;
    }
    public void setSocketBufferSize(int socketBufferSize) {
        this.socketBufferSize = socketBufferSize;
    }
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
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
    
}
