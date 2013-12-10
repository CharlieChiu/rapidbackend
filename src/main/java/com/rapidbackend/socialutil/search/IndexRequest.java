package com.rapidbackend.socialutil.search;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@SuppressWarnings("rawtypes")
public class IndexRequest implements Runnable{
    Logger logger = LoggerFactory.getLogger(IndexRequest.class);
    protected BlockingQueue<Exchanger> exchangerQueue;
    public IndexRequest(BlockingQueue<Exchanger> queue){
        exchangerQueue = queue;
    }
    protected Object record;
    public IndexRequest(Object record,BlockingQueue<Exchanger> exchangerQueue){
        this.record = record;
        this.exchangerQueue = exchangerQueue;
    }
    @SuppressWarnings("unchecked")
    public void run(){
        try {
        Exchanger exchanger = exchangerQueue.take();
        exchanger.exchange(record);
        } catch (InterruptedException e) {
            logger.error("Interrupted in IndexRequest,",e);
        }
    }
    /*
    public static void main(String[] args){
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService executor2 = Executors.newSingleThreadExecutor();
        BlockingQueue<Exchanger> exchangerQueue = new LinkedBlockingDeque<Exchanger>();
        // start the indexer
        Indexer indexer = new Indexer(exchangerQueue);
        executor.submit(indexer);
        for(int i=0;i<20;i++){
            IndexRequest request = new IndexRequest(i+"",exchangerQueue);
            executor2.submit(request);
        }
    }*/
}
