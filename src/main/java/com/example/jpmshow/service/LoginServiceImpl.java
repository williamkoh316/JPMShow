package com.example.jpmshow.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.jpmshow.model.UserEnum;

@Service
public class LoginServiceImpl implements LoginService {
	
	private List<UserEnum> validUser = new ArrayList<>(Arrays.asList(UserEnum.ADMIN, UserEnum.BUYER)) ;
	private UserEnum user = UserEnum.NONE;

	@Override
	public boolean isLogin() {
		return validUser.contains(user);
	}
	
	@Override
	public boolean login(String usr) {
		try {
			UserEnum userEnum = UserEnum.valueOf(usr.toUpperCase());
			if (validUser.contains(userEnum)) {
				user = userEnum;
				return true;
			} else {
				return false;
			}
		}catch (Exception e){
			return false;
		}
	}
	
	@Override
	public void logout () {
		user = UserEnum.NONE;
	}
	
	@Override
	public UserEnum getUser() {
		return user;
	}
	
}
