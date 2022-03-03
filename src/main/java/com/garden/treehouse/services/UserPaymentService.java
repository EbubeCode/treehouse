package com.garden.treehouse.services;

import com.garden.treehouse.model.UserPayment;
import com.garden.treehouse.repos.UserPaymentRepository;
import org.springframework.stereotype.Service;


@Service
public class UserPaymentService{

	private final UserPaymentRepository userPaymentRepository;

	public UserPaymentService(UserPaymentRepository userPaymentRepository) {
		this.userPaymentRepository = userPaymentRepository;
	}

	public UserPayment findById(Long id) {
		return userPaymentRepository.findById(id).orElse(null);
	}
	
	public void deleteById(Long id) {
		userPaymentRepository.deleteById(id);
	}
} 
