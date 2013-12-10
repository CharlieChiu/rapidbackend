package com.rapidbackend.webserver.netty;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;


import com.rapidbackend.core.context.AppContextAware;
/**
 * 
 * @author chiqiu
 *
 */
public class SSLFactory extends AppContextAware{
    private final KeyStore keyStore;
    private final NettyConf conf;
    public SSLFactory() {
        try {
            conf = (NettyConf)getApplicationContext().getBean("NettyConf");
            keyStore = KeyStore.getInstance(conf.getKeyStoreType());
            FileInputStream in = new FileInputStream(new File(conf.getSslKeyStoreFile()));
            keyStore.load(in, conf.getKeyStorePassword().toCharArray());
        } catch (Exception e) {
            throw new NettyHttpServerException(e);
        }
    }
    
    public SSLContext getServerContext(){
        try {
            String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
            if (algorithm == null) {
                algorithm = "SunX509";
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(keyStore, conf.getCertificatePassword().toCharArray());
            SSLContext sslContext = SSLContext.getInstance(conf.getSslProtocol());
            sslContext.init(kmf.getKeyManagers(), null, null);
            return sslContext;
        } catch (Exception e) {
            throw new NettyHttpServerException(e);
        }
    }
    
    public SSLContext getClientContext(){
        try {
            SSLContext sslContext = SSLContext.getInstance(conf.getSslProtocol());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
            tmf.init(keyStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            sslContext.init(null, trustManagers, null);
            return sslContext;
        } catch (Exception e) {
            throw new NettyHttpServerException(e);
        }
    }
}
