package com.rapidbackend.socialutil.search;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.socialutil.model.reserved.FeedContentBase;
@SuppressWarnings("rawtypes")
@Deprecated
public abstract class Indexer implements Runnable{
    
    protected EmbeddedSolrServer solrServer;
    
    protected BlockingQueue<Exchanger> exchangerQueue;
    protected Exchanger exchanger = new Exchanger();
    protected AtomicLong recievedRequest = new AtomicLong(0);
    protected long lastCommitTime = 0l;
    protected int commitRate = 100;
    protected long maxWaitingTime = 3600*1000l;// commits every hour
    
    public int getCommitRate() {
        return commitRate;
    }
    public void setCommitRate(int commitRate) {
        this.commitRate = commitRate;
    }
    public long getMaxWaitingTime() {
        return maxWaitingTime;
    }
    public void setMaxWaitingTime(long maxWaitingTime) {
        this.maxWaitingTime = maxWaitingTime;
    }
    public Indexer(EmbeddedSolrServer solrServer,BlockingQueue<Exchanger> queue){
        exchangerQueue = queue;
    }
    /**
     * @return true if commit conditions are triggered
     */
    public boolean shouldCommit(){
        return (System.currentTimeMillis()-lastCommitTime)>maxWaitingTime
               ||recievedRequest.longValue()%commitRate==0;
    }
    
    public void run(){
        while(true){
            try{
                exchangerQueue.put(exchanger);
                Object record = null;
                try {
                    record = exchanger.exchange(this);
                    index(record);
                } catch (InterruptedException e) {
                    continue;
                }
            }catch (InterruptedException e) {
            }
            
        }
    }
    
    public abstract void index(Object record);
    
    public static class FeedIndexer extends Indexer{
        Logger logger = LoggerFactory.getLogger(FeedIndexer.class);
        public FeedIndexer(EmbeddedSolrServer solrServer,BlockingQueue<Exchanger> queue){
            super(solrServer, queue);
        }
        @Override
        public void index(Object record){
            FeedContentBase feed = (FeedContentBase)record;
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", feed.getId().toString());
            document.addField("content", feed.getContent());
            document.addField("created", feed.getCreated().toString());
            try {
                solrServer.add(document);
                if(shouldCommit()){
                    logger.debug("commit the "+recievedRequest.longValue()+"th index feed request");
                    solrServer.commit();
                }
            } catch (SolrServerException e) {
                logger.error("error indexing feed "+feed.getId(), e);
            }catch (IOException e) {
                logger.error("error indexing feed "+feed.getId(), e);
            }
        }
    }
    
    public static class UserIndexer extends Indexer{
        
        public UserIndexer(EmbeddedSolrServer solrServer,BlockingQueue<Exchanger> queue){
            super(solrServer, queue);
        }
        @Override
        public void index(Object record){
            //TODO
        }
    }
}
