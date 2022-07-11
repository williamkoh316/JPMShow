package com.example.jpmshow.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.jpmshow.model.Show;

@Repository
public interface ShowRepository extends CrudRepository<Show, Integer>{
	
}
