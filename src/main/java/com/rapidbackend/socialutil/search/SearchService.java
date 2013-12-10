package com.rapidbackend.socialutil.search;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.util.NamedList;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.core.model.util.ModelList;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;

/**
 * @author chiqiu
 *
 */
public class SearchService extends ClusterableService{
    
    Logger logger = LoggerFactory.getLogger(SearchService.class);
    protected static String defaultSearchConfName = "searchConf";
    
    protected Server solrAdminServer;
    
    protected EmbeddedSolrServer feedSearchServer;
    protected EmbeddedSolrServer userSearchServer;
    
    public Server getSolrAdminServer() {
        return solrAdminServer;
    }
    public void setSolrAdminServer(Server solrAdminServer) {
        this.solrAdminServer = solrAdminServer;
    }
        
    @Override
    public void doStart() throws Exception{
        System.out.println("Initializing solr admin server...");
        
        SearchConf searchConf = (SearchConf)getApplicationContext().getBean("SearchConfiguration");
        Assert.notNull(searchConf, "search configuration is null");
        String solrHome = searchConf.getSolrHome();
        String solrAdminAppBase = searchConf.getAdminResourceBase();
        int solrAdminPort = searchConf.getSolrAdminPort();
        System.setProperty("solr.solr.home", solrHome);
        
        WebAppContext context = new WebAppContext();
        context.setContextPath("/solr");
        context.setResourceBase(solrAdminAppBase);
        solrAdminServer = new Server(solrAdminPort);
        solrAdminServer.setHandler(context);
        
        /*
         * set the zookeeper configuration
         * 
         */
        boolean zkRun = searchConf.isZkRun();
        String zkHost = searchConf.getZookeeperHost();
        if(!StringUtils.isEmpty(zkHost))
            System.setProperty("zkHost", zkHost);
        if(zkRun){
            System.setProperty("zkRun","");
        }else {
            if(StringUtils.isEmpty(zkHost)){
                throw new RuntimeException("try to start solr with invalid zookeeper configuration");
            }
        }
        
        System.setProperty("collection.configName", defaultSearchConfName);
        System.setProperty("bootstrap_confdir",solrHome+"/conf");
        
        System.out.println("Starting server...");
        
        solrAdminServer.start();
        
        feedSearchServer = SolrInitializeFilter.getSolrServerContainer().getFeedSearchServer();
        userSearchServer = SolrInitializeFilter.getSolrServerContainer().getUserSearchServer();
    }
    @Override
    public void doStop(){
        try{
            solrAdminServer.stop();
        }catch (Exception ignored) {
        }
    }
    
    public void indexFeed(FeedContentBase feed){
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", feed.getId().toString());
        document.addField("content", feed.getContent());
        document.addField("created", feed.getCreated().toString());
        try {
            feedSearchServer.add(document);
        } catch (Exception e) { 
            logger.error("error indexing feed "+feed.getId(),e);
        }
    }
    /**
     * when user changes screen name, this method should be called again, 
     * using the same id to replace the existing document in index
     * @param user
     */
    public void indexUser(UserBase user){
        SolrInputDocument document = new SolrInputDocument();
        document.addField("id", user.getId().toString());
        document.addField("name", user.getScreenName());
        try {
            userSearchServer.add(document);
        } catch (Exception e) {
            logger.error("error indexing user "+user.getId(),e);
        }
    }
    
    public void deleteUser(UserBase user){
        try {
            userSearchServer.deleteById(user.getId().toString());
            logger.info("user" + user.getId() + " deleted");
        } catch (Exception e) {
            logger.error("error deleting user "+user.getId(),e);
        }
    }
    
    public void deleteFeed(FeedContentBase feed){
        try {
            feedSearchServer.deleteById(feed.getId().toString());
            logger.info("feed" + feed.getId() + " deleted");
        } catch (Exception e) {
            logger.error("error deleting feed "+feed.getId(),e);
        }
    }
    
    public ModelList<FeedContentBase> searchFeed(String contentKeywords,String start,String limit){
        ModelList<FeedContentBase> result = new ModelList<FeedContentBase>();
        SolrRequest request = SolrRequestBuilder.buildRequest("content:"+contentKeywords, start,limit);
        try {
            NamedList<Object> solrResult = feedSearchServer.request(request);
            SolrDocumentList solrDocumentList = (SolrDocumentList)solrResult.get("response");
            if(solrDocumentList.size()>0){
                setResponseInfo(result,solrResult,solrDocumentList);
                for(SolrDocument document:solrDocumentList){
                    FeedContentBase feed = new FeedContentBase();
                    feed.setContent(document.getFieldValue("content").toString());
                    feed.setId(Integer.valueOf(document.getFieldValue("id").toString()));
                    feed.setCreated(Long.valueOf(document.getFieldValue("created").toString()));
                    result.add(feed);
                }
            }
        } catch (Exception e) {
            logger.error("error searching "+ contentKeywords, e);
        }
        return result;
    }
    
    @SuppressWarnings("rawtypes")
    protected void setResponseInfo( ModelList result, NamedList<Object> solrResult,SolrDocumentList solrDocumentList){
        result.setCount(solrDocumentList.getNumFound());
        result.setLimit(solrDocumentList.size());
        result.setStart(solrDocumentList.getStart());
        result.setAdditionalInfo(solrResult.get("responseHeader").toString());
    }
    
}
