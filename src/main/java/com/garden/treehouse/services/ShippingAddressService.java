package com.garden.treehouse.services;

import com.garden.treehouse.model.ShippingAddress;
import com.garden.treehouse.model.UserShipping;
import org.springframework.stereotype.Service;

@Service
public class ShippingAddressService{
	
	public ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAddress) {
		
		shippingAddress.setShippingAddressStreet(userShipping.getUserShippingStreet());
		shippingAddress.setShippingAddressCity(userShipping.getUserShippingCity());
		shippingAddress.setShippingAddressState(userShipping.getUserShippingState());
		shippingAddress.setShippingAddressCountry(userShipping.getUserShippingCountry());
		shippingAddress.setShippingAddressPostcode(userShipping.getUserShippingPostcode());
		
		return shippingAddress;
	}
}
