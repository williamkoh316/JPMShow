package com.example.jpmshow.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.jpmshow.model.Show;
import com.example.jpmshow.model.Ticket;
import com.example.jpmshow.repository.ShowRepository;
import com.example.jpmshow.repository.TicketRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements BuyerService {
	
	private final ShowRepository showRepository;
	private final TicketRepository ticketRepository;

	public final String AVAILABLE = "Availability for show number: ";
	public final String NO_SHOW_RECORD = "No record found for show number: ";
	public final String PHONE_NOT_8_Digits = "Please use valid 8 digits number as phone number";
	public final String PHONE_USED = "Please use another phone number for this booking, ";
	public final String INVALID_SEAT = "No valid seat for this booking, ";
	public final String BOOKED = "Ticket(s) booked: ";
	public final String NO_TICKET_RECORD = "No record found for ticket number: ";
	public final String INCORRECT_PHONE = "Incorrect phone number";
	public final String CANNOT_CANCEL = "Unable to cancel ticket number: ";
	public final String CANCEL = "Cancelled Ticket Number: ";
	
	@Override
	public String getAvailability (int show_num) {
		String result = "";
		if (showRepository.findById(show_num).isPresent()) {
			result = AVAILABLE + show_num; 
			SortedSet set =	new TreeSet<String>(showRepository.findById(show_num).get().getAvailaleSeats());
			result += "\n" + set.toString();
		} else{
			result = NO_SHOW_RECORD + show_num;
		}
		return result;
	}

	@Override
	public String booking(int show_num, int phone, String seats) {
		String result = "";
		List<Ticket> tickets = new ArrayList<>();
		Optional<Show> optShow = showRepository.findById(show_num);
		
		if (!optShow.isPresent()) {
			return NO_SHOW_RECORD + show_num;
		}
		
		Show show = optShow.get();
		Set<String> availaleSeats = show.getAvailaleSeats();
	    Set<Integer> bookedPhone = show.getBookedPhone();
	    
	    if (Integer.toString(phone).length() != 8) {
			return  PHONE_NOT_8_Digits ;
	    }
		
		if (bookedPhone.contains(phone)) {
			return  PHONE_USED + String.format(
					"phone number (%d) have already used for show number %d", phone, show_num) ;
		}

		List<String> list = Arrays.asList(seats.split(",", -1));
		list.forEach( seat -> {
			seat = seat.trim().toUpperCase();
			if (!seat.isEmpty()) {				
				if (availaleSeats.contains(seat)) {
					availaleSeats.remove(new String(seat));
					Ticket ticket = new Ticket( phone, seat, new Timestamp(System.currentTimeMillis()), show);
					tickets.add(ticket);
				}
			}
		});
		
		if (!tickets.isEmpty()) {
			bookedPhone.add(phone);
		} else {
			return INVALID_SEAT;
		}

		Show savedShow = showRepository.save(new Show (show_num, show.getRow_num(), show.getNum_seat_per_row(), 
				show.getCancel_window_min(), tickets, availaleSeats, bookedPhone));
		
		List<Ticket> ticketsWithId = savedShow.getTickets().stream()
				.filter(t -> t.getPhone() == phone).collect(Collectors.toList());
		
		result = BOOKED;
		for (Ticket t : ticketsWithId) {
			result += String.format("%nShow number: %d Ticket number: %d Seat number: %s",show_num, t.getId(), t.getSeat());
		}
		
		return result;
	}

	@Override
	public String cancel( int ticket_num, int phone_num) {
		String result;
		
		Optional<Ticket> optTicket = ticketRepository.findById(ticket_num);
		if (!optTicket.isPresent()) {
			result = NO_TICKET_RECORD + ticket_num;
			
		} else {
			Ticket ticket = optTicket.get();
			
			if(ticket.getPhone() != phone_num) {
				return String.format(INCORRECT_PHONE +"(%d) for ticket number: %d", phone_num, ticket_num);
			}
			
			Show show = ticket.getShow();
			
			Timestamp expiredCancellationTime = new Timestamp(
					ticket.getBookingTime().getTime() + TimeUnit.MINUTES.toMillis(show.getCancel_window_min()));
			Timestamp now = new Timestamp (System.currentTimeMillis());
			
			if (expiredCancellationTime.before(now)) {
				return String.format(CANNOT_CANCEL +"%d due to past cancellation time", ticket_num);
			}
			
			List<Ticket> tickets = show.getTickets();
			SortedSet<String> availaleSeats = new TreeSet<String>(show.getAvailaleSeats());
		    Set<Integer> bookedPhone = show.getBookedPhone();
		    
		    tickets.remove(ticket);
		    availaleSeats.add(ticket.getSeat());
		    
		    boolean found = tickets.stream().filter(t -> t.getPhone() == phone_num).findAny().isPresent();

		    if (!found) {
		    	bookedPhone.remove(Integer.valueOf(phone_num));
		    }

			showRepository.save(new Show (show.getShow_num(), show.getRow_num(), show.getNum_seat_per_row(), 
					show.getCancel_window_min(), tickets, availaleSeats, bookedPhone));
			
			result = CANCEL + ticket_num;
		}
		
		return result;
	}

}
