package com.example.jpmshow.command;

import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;

import com.example.jpmshow.model.UserEnum;
import com.example.jpmshow.service.AdminServiceImpl;
import com.example.jpmshow.service.LoginServiceImpl;

import lombok.RequiredArgsConstructor;

@ShellComponent
@RequiredArgsConstructor
class AdminCommand {
	private final LoginServiceImpl loginService;
	private final AdminServiceImpl adminService;
	
	@ShellMethod ("Create/Update Show (Default Cancellation Window = 2min) >>setup  <Show Number> <Number of Rows> <Number of seats per row>  <(Optional) Cancellation window in minutes> ")
	@ShellMethodAvailability("isAdminLogin")
	public String setup (int show_num, int row_num, int num_seat_per_row, @ShellOption(defaultValue = "2") int cancel_window_min) {
		return adminService.addShow(show_num, row_num, num_seat_per_row, cancel_window_min);
	}
	
	@ShellMethod ("View Seat Allocation >>view <Show Number>")
	@ShellMethodAvailability("isAdminLogin")
	public String view (int show_num) {
		return adminService.getShow(show_num);
	}
	
	Availability isAdminLogin() {
		return loginService.getUser() == UserEnum.ADMIN ?
				Availability.available() : Availability.unavailable("You have not login as Admin ...");
	}
	
	
	
}
