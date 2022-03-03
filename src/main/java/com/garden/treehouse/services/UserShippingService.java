package com.garden.treehouse.services;

import com.garden.treehouse.model.UserShipping;
import com.garden.treehouse.repos.UserShippingRepository;
import org.springframework.stereotype.Service;

@Service
public class UserShippingService{
	
	private final UserShippingRepository userShippingRepository;

	public UserShippingService(UserShippingRepository userShippingRepository) {
		this.userShippingRepository = userShippingRepository;
	}


	public UserShipping findById(Long id) {
		return userShippingRepository.findById(id).orElse(null);
	}
	
	public void deleteById(Long id) {
		userShippingRepository.deleteById(id);
	}

}
