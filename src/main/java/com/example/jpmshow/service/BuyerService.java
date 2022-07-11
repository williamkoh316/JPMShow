package com.example.jpmshow.service;

public interface BuyerService {
	
	String getAvailability (int show_num);

	String booking(int show_num, int phone, String seats) ;

	String cancel( int ticket_num, int phone_num);

}
