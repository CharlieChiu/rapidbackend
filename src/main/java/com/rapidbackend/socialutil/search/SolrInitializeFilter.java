package com.rapidbackend.socialutil.search;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.servlet.SolrDispatchFilter;

public class SolrInitializeFilter extends SolrDispatchFilter{
    protected static EmbeddedSolrServer feedSearchServer = null;
    protected static EmbeddedSolrServer userSearchServer = null;
    protected static String feedSearchCoreName = "feedSearch";
    protected static String userSearchCoreName = "userSearch";
    protected static SolrInitializeFilter solrServerContainer;
    
    public SolrInitializeFilter(){
        solrServerContainer = this;// ugly 
    }
    
    public static SolrInitializeFilter getSolrServerContainer() {
        return solrServerContainer;
    }

    public static void setSolrServerContainer(
            SolrInitializeFilter solrServerContainer) {
        SolrInitializeFilter.solrServerContainer = solrServerContainer;
    }

    public EmbeddedSolrServer getFeedSearchServer(){
        if (feedSearchServer==null) {
            if(getCores()!=null){
                feedSearchServer = new EmbeddedSolrServer(getCores(),feedSearchCoreName);
            }else {
                throw new RuntimeException("no solr core inited");
            }
        }
        return feedSearchServer;
    }
    
    public EmbeddedSolrServer getUserSearchServer(){
        if (userSearchServer==null) {
            if(getCores()!=null){
                userSearchServer = new EmbeddedSolrServer(getCores(),userSearchCoreName);
            }else {
                throw new RuntimeException("solr core inited");
            }
        }
        return userSearchServer;
    }
}
