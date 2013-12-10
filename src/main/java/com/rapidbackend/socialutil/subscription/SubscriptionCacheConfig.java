package com.rapidbackend.socialutil.subscription;

import com.rapidbackend.cache.CacheConfig;

public class SubscriptionCacheConfig extends CacheConfig{
	String name = "FriendshipCache";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
