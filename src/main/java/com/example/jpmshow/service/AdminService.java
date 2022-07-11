package com.example.jpmshow.service;

public interface AdminService {
	
	String addShow(int show_num, int row_num, int num_seat_per_row, int cancel_window_min);
	
	String getShow (int show_num);
	
}
