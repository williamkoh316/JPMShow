package com.example.jpmshow.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Show")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Show {
	@Id
	private int show_num; 
	
	private int row_num;
	
	private int num_seat_per_row;
	
	private int cancel_window_min;
	
	@OneToMany(
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			fetch = FetchType.EAGER
	)
	@JoinColumn(name = "show_num")
	private List<Ticket> tickets = new ArrayList(); 
	
    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> availaleSeats = new HashSet(); 
	
    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> bookedPhone = new HashSet();

}
