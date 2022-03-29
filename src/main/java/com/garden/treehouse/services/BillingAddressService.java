package com.garden.treehouse.services;

import com.garden.treehouse.model.BillingAddress;
import com.garden.treehouse.model.UserBilling;
import org.springframework.stereotype.Service;

@Service
public class BillingAddressService{
	
	public BillingAddress setByUserBilling(UserBilling userBilling, BillingAddress billingAddress) {
		
		billingAddress.setBillingAddressStreet(userBilling.getUserBillingStreet());
		billingAddress.setBillingAddressCity(userBilling.getUserBillingCity());
		billingAddress.setBillingAddressState(userBilling.getUserBillingState());
		billingAddress.setBillingAddressCountry(userBilling.getUserBillingCountry());
		billingAddress.setBillingAddressZipcode(userBilling.getUserBillingZipcode());
		
		return billingAddress;
	}

}
