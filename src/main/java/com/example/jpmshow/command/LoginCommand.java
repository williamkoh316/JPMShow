package com.example.jpmshow.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.example.jpmshow.service.LoginServiceImpl;

import lombok.RequiredArgsConstructor;

@ShellComponent
@RequiredArgsConstructor
public class LoginCommand {
	private final LoginServiceImpl login;

    @Autowired
    private ApplicationContext context;
	
	public final String VAILD_LOGIN = "Login as ";
	public final String INVAILD_LOGIN = "Invalid User: ";
	public final String LOGOUT = "GoodBye, see you again!";
	
	@ShellMethod ("Login to the JPMShow")
	public String login (String username) {
		String returnStr ="";
		boolean validLogin = login.login(username);
		
		 if (validLogin) {
			 returnStr = VAILD_LOGIN + username;
		 } else {
			 returnStr = INVAILD_LOGIN + username;
		 }
		 return returnStr;
	}
	
	Availability loginAvailability() {
		return !this.login.isLogin()?
				Availability.available() : Availability.unavailable("You have already login ...");
	}
	
	@ShellMethod ("Logout from the JPMShow")
	public String logout () {
		this.login.logout();
		return LOGOUT;
	}
	
	Availability logoutAvailability() {
		return this.login.isLogin()?
				Availability.available() : Availability.unavailable("You have not login yet ...");
	}
	
}
