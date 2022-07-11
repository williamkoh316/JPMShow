package com.example.jpmshow.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.jpmshow.model.Ticket;

@Repository
public interface TicketRepository extends CrudRepository<Ticket, Integer>{
	
}
