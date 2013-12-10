package com.rapidbackend.socialutil.search;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.apache.solr.common.params.SolrParams;

/**
 * convert social util search request into local solr request
 * @author chiqiu
 *
 */
@SuppressWarnings("rawtypes")
public class SolrRequestBuilder {
    public static int defaultStart = 0;
    public static int defaultLimit = 20;
    public static Map<String, String[]> defaultParams = new HashMap<String, String[]>();
    
    static{
        defaultParams.put("sort", new String[]{"created desc"});
        defaultParams.put(CommonParams.START, new String[]{"0"});
        defaultParams.put(CommonParams.ROWS, new String[]{"20"});
    }
    public static SolrRequest buildRequest(String query,String start,String limit){
        SolrParams params = makeParams(query, start, limit);
        QueryRequest request = new QueryRequest(params);
        return request;
    }
    protected static SolrParams makeParams(String query, String qtype, String start, String limit, Map args) {
        Map<String,String[]> map = new HashMap<String,String[]>();
        for (Iterator iter = args.entrySet().iterator(); iter.hasNext();) {
          Map.Entry e = (Map.Entry)iter.next();
          String k = e.getKey().toString();
          Object v = e.getValue();
          if (v instanceof String[]) map.put(k,(String[])v);
          else map.put(k,new String[]{v.toString()});
        }
        if (query!=null) map.put(CommonParams.Q, new String[]{query});
        if (qtype!=null) map.put(CommonParams.QT, new String[]{qtype});
        if(start!=null)
            map.put(CommonParams.START, new String[]{start});
        if(limit!=null)
            map.put(CommonParams.ROWS, new String[]{limit});
        return new MultiMapSolrParams(map);
    }
    protected static SolrParams makeParams(String query, String start, String limit, Map args) {
        return makeParams(query,null,start,limit,args);
    }
    protected static SolrParams makeParams(String query, String start, String limit) {
        return makeParams(query,null,start,limit,defaultParams);
    }
}
