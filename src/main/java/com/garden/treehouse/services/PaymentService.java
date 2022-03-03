package com.garden.treehouse.services;

import com.garden.treehouse.model.Payment;
import com.garden.treehouse.model.UserPayment;
import org.springframework.stereotype.Service;

@Service
public class PaymentService{
	
	public Payment setByUserPayment(UserPayment userPayment, Payment payment) {
		
		payment.setType(userPayment.getType());
		payment.setHolderName(userPayment.getHolderName());
		payment.setCardNumber(userPayment.getCardNumber());
		payment.setExpiryMonth(userPayment.getExpiryMonth());
		payment.setExpiryYear(userPayment.getExpiryYear());
		payment.setCvc(userPayment.getCvc());
		
		return payment;
	}

}
