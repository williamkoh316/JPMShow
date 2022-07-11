package com.example.jpmshow.command;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import com.example.jpmshow.model.UserEnum;
import com.example.jpmshow.service.BuyerServiceImpl;
import com.example.jpmshow.service.LoginServiceImpl;

import lombok.RequiredArgsConstructor;

@ShellComponent
@RequiredArgsConstructor
class BuyerCommand {
	private final LoginServiceImpl loginService;
	private final BuyerServiceImpl bookingService;
	
	@ShellMethod ("Display the available seat for the Show >>availability  <Show Number>")
	@ShellMethodAvailability("isBuyerLogin")
	public String availability (int show_num) {
		return bookingService.getAvailability(show_num);
	}
	
	@ShellMethod ("To book seat(s) >>book  <Show Number> <Phone#> <Comma separated list of seats> ")
	@ShellMethodAvailability("isBuyerLogin")
	public String book(int show_num, int phone, String seats) {
		return bookingService.booking(show_num, phone, seats);
	}
	
	@ShellMethod ("To cancel ticket >>cancel  <Ticket#>  <Phone#>")
	@ShellMethodAvailability("isBuyerLogin")
	public String cancel( int ticket_num, int phone_num) {
		return bookingService.cancel(ticket_num, phone_num);
	}
	
	Availability isBuyerLogin() {
		return loginService.getUser() == UserEnum.BUYER ?
				Availability.available() : Availability.unavailable("You have not login as Admin ...");
	}
	
	
	
}
