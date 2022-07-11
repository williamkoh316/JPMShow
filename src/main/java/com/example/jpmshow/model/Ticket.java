package com.example.jpmshow.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Ticket")
@Getter
@NoArgsConstructor
public class Ticket {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
//	private int show_num;
	private int phone;
	private String seat;
	private Timestamp bookingTime;
	
	@ManyToOne
    @JoinColumn(name="show_num")
	private Show show;
    
    
    public Ticket(int phone, String seat, Timestamp bookingTime, Show show) {
    	super();
    	this.phone = phone;
    	this.seat = seat;
    	this.bookingTime = bookingTime;
    	this.show = show;
    }
	
}
