package com.rapidbackend.socialutil.search;

import org.springframework.beans.factory.annotation.Required;
/**
 * This is a simple solr setting. Currently we expose the solr admin site
 * to our users. 
 * User is encouraged to modify solr settings by themselves. 
 * Current design for the search module only supports replication.
 * The search module should inherit all solr's functionalities. In current
 * release, we use solr's zookeeper server. The first socialutilty
 * server with search configured will have a zookeeper server run by default.
 * Then if user want to use replication, they should start 
 * another socialutility server with only search service enabled and set the zookeeper server
 * to the first one's zookeeper server.
 * If user wants to go further to cluster and more, that will be another story......<br/>
 * 
 * Note: Commit rate and softcommit should be configured in solrconfig.xml
 * @author chiqiu
 *
 */
public class SearchConf {
    protected String solrHome;//flder 'solrHome'
    protected int solrAdminPort;// default 8079
    protected boolean zkRun = true;// by default the embedded zookeeper server will be started
    protected String zookeeperHost;// this must be set with a valid value if zkRun is set to false
    protected String adminResourceBase;//webapp/web
    protected int numShards = 1;//By default we only do replications
    
    
    public String getSolrHome() {
        return solrHome;
    }
    @Required
    public void setSolrHome(String solrHome) {
        this.solrHome = solrHome;
    }
    public int getSolrAdminPort() {
        return solrAdminPort;
    }
    @Required
    public void setSolrAdminPort(int solrAdminPort) {
        this.solrAdminPort = solrAdminPort;
    }
    public String getAdminResourceBase() {
        return adminResourceBase;
    }
    @Required
    public void setAdminResourceBase(String adminResourceBase) {
        this.adminResourceBase = adminResourceBase;
    }
    public String getZookeeperHost() {
        return zookeeperHost;
    }
    public void setZookeeperHost(String zookeeperHost) {
        this.zookeeperHost = zookeeperHost;
    }
    public boolean isZkRun() {
        return zkRun;
    }
    public void setZkRun(boolean zkRun) {
        this.zkRun = zkRun;
    }
    public int getNumShards() {
        return numShards;
    }
    public void setNumShards(int numShards) {
        this.numShards = numShards;
    }
}
