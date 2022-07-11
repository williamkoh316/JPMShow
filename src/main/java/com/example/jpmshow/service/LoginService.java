package com.example.jpmshow.service;

import com.example.jpmshow.model.UserEnum;


public interface LoginService {

	boolean isLogin();
	
	boolean login(String usr);
	
	void logout ();
	
	UserEnum getUser();
	
}
