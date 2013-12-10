package com.rapidbackend.socialutil.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.core.ClusterableService;
import com.rapidbackend.core.Rapidbackend;
import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
import com.rapidbackend.socialutil.model.reserved.UserBase;

/**
 * index content into solr when new model's been posted.
 * 
 * TODO we might need to move the index request to kestrel if we want better scalablity
 * @author chiqiu
 */
@SuppressWarnings("rawtypes")
@Deprecated
public class IndexService extends ClusterableService{
    
    Logger logger = LoggerFactory.getLogger(IndexService.class);
    
    protected EmbeddedSolrServer feedSearchServer;
    protected EmbeddedSolrServer userSearchServer;
    
    protected ExecutorService feedIndexerExecutor = 
        Rapidbackend.getCore().getThreadManager().newSingleThreadExecutor(null, "feedIndexerExecutor");
    protected ExecutorService feedIndexRequestExecutor = 
        Rapidbackend.getCore().getThreadManager().newSingleThreadExecutor(null, "feedIndexRequestExecutor");
    
    
    protected Indexer feedIndexer;
    protected BlockingQueue<Exchanger> feedIndexExchangerQueue = new LinkedBlockingDeque<Exchanger>();
    
    @Override
    public void doStart(){
        feedSearchServer = SolrInitializeFilter.getSolrServerContainer().getFeedSearchServer();
        userSearchServer = SolrInitializeFilter.getSolrServerContainer().getUserSearchServer();
    }
    
    @Override
    public void doStop(){
    }
    /*
    public void indexFeed(Feed feed){
        IndexRequest request = new IndexRequest(feed, feedIndexExchangerQueue);
        feedIndexRequestExecutor.submit(request);
    }*/
    
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
    public void configIndexer(Indexer indexer){
        //SearchConf searchConf = (SearchConf)getApplicationContext().getBean("SearchConfiguration");
        
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
}
