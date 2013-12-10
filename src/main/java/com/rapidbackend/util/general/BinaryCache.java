package com.rapidbackend.util.general;

import java.util.HashMap;

import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rapidbackend.cache.CacheConfig;
import com.rapidbackend.cache.local.BitStatusCache;
import com.rapidbackend.util.io.GZIPUtils;

/**
 * wrapping of fstlrucache, cache common value types
 * @author chiqiu
 *
 */
public class BinaryCache extends BitStatusCache{
	
	Logger logger = LoggerFactory.getLogger(BinaryCache.class);
	
	protected int maxByteArraySize = 0;// the largest object size in this cache
	protected int minByteArraySize = 0;// the smallest object size in the cache
	protected FastLRUCache<Integer, byte[]> byteCache;
	protected HashMap<String, FastLRUCache> cacheContainer = new HashMap<String, FastLRUCache>();
	protected String name;
	protected boolean compress = false;// needs much more cpu, this is a tradeoff to memory, turned off by default
	public BinaryCache(int size, String cacheName, boolean compressing){
	    name = cacheName;
	    compress = compressing;
	    byteCache = new FastLRUCache<Integer,byte[]>();
	    byteCache.init(size, name+"byteCache");
	    cacheContainer.put("byteCache", byteCache);
	}
	public BinaryCache(CacheConfig config){
		this(config.getCacheSize(),config.getName(),config.isCompress());
	}
	public boolean putBytes(Integer key,byte[] value){
		if(compress){
			return compressAndPutBytes(key,value);
		}
		boolean res = false;
		if(value!=null){
			res = putbyte0(key, value)!=null;
		}
		return res;
	}
	
	public boolean compressAndPutBytes(Integer key,byte[] value){
		boolean res = false;
		if(value!=null){
			byte[] compressed = GZIPUtils.zip(value);
			res = putbyte0(key, compressed)!=null;
		}
		return res;
	}
	
	public byte[] putbyte0(Integer key, byte[] data){
	    int length = data.length;
        maxByteArraySize = Math.max(length, maxByteArraySize);
        minByteArraySize = Math.min(minByteArraySize, length);
        return byteCache.put(key, data);
	}
	
	public byte[] unzipAndGetBytes(Integer key){
		byte[] value = byteCache.get(key);
		if(value!=null){
			byte[] unzipped = null;
			try{
				unzipped = GZIPUtils.unzip(value);
			}catch (Exception e) {
				// TODO: handle exception
				logger.error(getName()+" : error unzip data for key"+"key",e);
				byteCache.remove(key);
			}
			return unzipped;
		}else{
			return null;
		}
	}
	public byte[] getBytes(Integer key){
		if(compress){
			return unzipAndGetBytes(key);
		}
		return byteCache.get(key);
	}
	
	public String getStatistics(){
		return byteCache.getStatistics().toString();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    public int getMaxByteArraySize() {
        return maxByteArraySize;
    }
    public int getMinByteArraySize() {
        return minByteArraySize;
    }
    
    
}
