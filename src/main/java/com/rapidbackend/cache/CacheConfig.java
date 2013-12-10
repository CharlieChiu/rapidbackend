package com.rapidbackend.cache;

public abstract  class CacheConfig {
	protected int cacheSize = 512;
	protected boolean compress = false;
	public int getCacheSize() {
		return cacheSize;
	}
	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	public boolean isCompress() {
		return compress;
	}
	public void setCompress(boolean compress) {
		this.compress = compress;
	}
	public abstract String getName();
	
}
