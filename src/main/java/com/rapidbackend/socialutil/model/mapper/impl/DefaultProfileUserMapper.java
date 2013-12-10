package com.rapidbackend.socialutil.model.mapper.impl;

/**
@deprecated

import com.rapidbackend.core.BackendErrorCodes;
import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.socialutil.model.Profile;
import com.rapidbackend.socialutil.model.User;
import com.rapidbackend.socialutil.model.mapper.ProfileUserMapper;

public class DefaultProfileUserMapper implements ProfileUserMapper{
	public User createUser(Profile profile) throws BackendRuntimeException{
		User user = new User();
		try{
			user.setScreenName(profile.getScreenName());
			user.setPropagateRule(0);
			user.setSubscribeRule(0);
			user.setId(null);
			user.setUserUrl(profile.getProfileurl());
		}catch(Exception e){
			throw new BackendRuntimeException(BackendErrorCodes.InternalDataBaseObjectConvertError, "error at converting profile to user", e);
		}
		return user;
	}
}
*/