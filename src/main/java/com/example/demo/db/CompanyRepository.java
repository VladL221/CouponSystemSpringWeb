package com.example.demo.db;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.beans.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
	//find the company by its name
	Company findByName(String name);
	

}
