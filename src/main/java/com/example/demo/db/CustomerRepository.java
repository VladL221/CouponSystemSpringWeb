package com.example.demo.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.beans.Customer;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	//finds customer by name
	Customer findByFirstName(String name);
	

}
