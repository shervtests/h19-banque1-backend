package com.grokonez.spring.restapi.mongodb.repo;

import java.util.List;


import com.grokonez.spring.restapi.mongodb.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository <Customer, Long>{
	List<Customer> findByAge(int age);
}
