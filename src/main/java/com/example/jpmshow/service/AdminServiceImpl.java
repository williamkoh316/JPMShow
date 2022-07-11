package com.example.jpmshow.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.jpmshow.model.Show;
import com.example.jpmshow.model.Ticket;
import com.example.jpmshow.repository.ShowRepository;
import com.example.jpmshow.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
	
	private final ShowRepository showRepository;
	private final TicketRepository ticketRepository;
	
	public final String UPDATE_SHOW = "Update show: ";
	public final String CREATE_SHOW = "Create show: ";
	public final String INVALID_SHOW = "You have entered invalid show inputs. Please take note that max seats per row is 10 and max rows are 26";;
	
	public final String NO_DATA = "No data found for Show number: ";
	public final String NO_SOLD = "No ticket sold for Show number: ";
	public final String TICKET_SOLD = "Ticket sold for Show number:";
	
	
	@Override
	public String addShow(int show_num, int row_num, int num_seat_per_row, int cancel_window_min) {
		String result = "";
		
		if (validShow(row_num, num_seat_per_row)) {
			List<Ticket> tickets = new ArrayList<>(); 
			Set<String> availaleSeats = new HashSet<>();
			Set<Integer> bookedPhone = new HashSet<>(); 
			
			String row;
			for (int i =0; i < row_num; i++) {
				row = (char)('A' + i) + "";
				for (int j=1; j<=num_seat_per_row; j++ ) {
					availaleSeats.add(row + j);
				}
			}
			
			boolean exist = showRepository.findById(show_num).isPresent();
			result =  exist ? UPDATE_SHOW + show_num : CREATE_SHOW + show_num;
			
			Show show = new Show (show_num, row_num, num_seat_per_row, 
					cancel_window_min, tickets, availaleSeats, bookedPhone);
			showRepository.save(show);
		} 
		else {
			result = INVALID_SHOW;
		}
		
		return result;
	}
	
	@Override
	public String getShow (int show_num) {
		String result = ""; 
		
		Optional<Show> optShow = showRepository.findById(show_num);
		if (!optShow.isPresent()) {
			result = NO_DATA + show_num;
		}
		else {
			List<Ticket> tickets = new ArrayList<>();
			ticketRepository.findAll().forEach(t -> {
				if (t.getShow().getShow_num() == show_num)
					tickets.add(t);
			});			
			
			if (tickets == null || tickets.isEmpty()) {
				result = NO_SOLD + show_num;
			} else {
				result = TICKET_SOLD + show_num;
				for (Ticket t : tickets) {
					result += String.format("%nTicket number: %d Phone number: %d Seat number: %s", 
							t.getId(), t.getPhone(), t.getSeat());
				}
			}
		}
		
		return result;
	}
	
	private boolean validShow (int row_num, int num_seat_per_row) {
		return row_num > 0 && row_num <=26 && num_seat_per_row >0 && num_seat_per_row <= 10;
	}
	

}
