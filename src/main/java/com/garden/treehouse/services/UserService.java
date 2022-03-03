package com.garden.treehouse.services;

import com.garden.treehouse.model.*;
import com.garden.treehouse.model.security.PasswordResetToken;
import com.garden.treehouse.model.security.UserRole;
import com.garden.treehouse.repos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
public class UserService{
	
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserPaymentRepository userPaymentRepository;
	private final UserShippingRepository userShippingRepository;
	private final PasswordResetTokenRepository passwordResetTokenRepository;

	public UserService(UserRepository userRepository, RoleRepository roleRepository,
					   UserPaymentRepository userPaymentRepository, UserShippingRepository userShippingRepository,
					   PasswordResetTokenRepository passwordResetTokenRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userPaymentRepository = userPaymentRepository;
		this.userShippingRepository = userShippingRepository;
		this.passwordResetTokenRepository = passwordResetTokenRepository;
	}

	public PasswordResetToken getPasswordResetToken(final String token) {
		return passwordResetTokenRepository.findByToken(token).orElse(null);
	}
	
	public void createPasswordResetTokenForUser(final User user, final String token) {
		final PasswordResetToken myToken = new PasswordResetToken(token, user);
		passwordResetTokenRepository.save(myToken);
	}

	public User findById(Long id){
		return userRepository.findById(id).orElse(null);
	}
	
	public User findByEmail (String email) {
		return userRepository.findByEmail(email).orElse(null);
	}
	
	@Transactional
	public User createUser(User user, Set<UserRole> userRoles){
		User localUser = userRepository.findByEmail(user.getEmail()).orElse(null);
		
		if(localUser != null) {
			LOG.info("user {} already exists. Nothing will be done.", user.getEmail());
		} else {
			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}
			
			user.getUserRoles().addAll(userRoles);
			
			ShoppingCart shoppingCart = new ShoppingCart();
			shoppingCart.setUser(user);
			user.setShoppingCart(shoppingCart);
			
			user.setUserShippingList(new ArrayList<UserShipping>());
			user.setUserPaymentList(new ArrayList<UserPayment>());
			
			localUser = userRepository.save(user);
		}
		
		return localUser;
	}
	
	public User save(User user) {
		return userRepository.save(user);
	}
	
	public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user) {
		userPayment.setUser(user);
		userPayment.setUserBilling(userBilling);
		userPayment.setDefaultPayment(true);
		userBilling.setUserPayment(userPayment);
		user.getUserPaymentList().add(userPayment);
		save(user);
	}
	
	public void updateUserShipping(UserShipping userShipping, User user){
		userShipping.setUser(user);
		userShipping.setUserShippingDefault(true);
		user.getUserShippingList().add(userShipping);
		save(user);
	}
	
	public void setUserDefaultPayment(Long userPaymentId, User user) {
		List<UserPayment> userPaymentList = (List<UserPayment>) userPaymentRepository.findAll();
		
		for (UserPayment userPayment : userPaymentList) {
			if(userPaymentId.equals(userPayment.getId())) {
				userPayment.setDefaultPayment(true);
				userPaymentRepository.save(userPayment);
			} else {
				userPayment.setDefaultPayment(false);
				userPaymentRepository.save(userPayment);
			}
		}
	}
	
	public void setUserDefaultShipping(Long userShippingId, User user) {
		List<UserShipping> userShippingList = (List<UserShipping>) userShippingRepository.findAll();
		
		for (UserShipping userShipping : userShippingList) {
			if(userShipping.getId() == userShippingId) {
				userShipping.setUserShippingDefault(true);
				userShippingRepository.save(userShipping);
			} else {
				userShipping.setUserShippingDefault(false);
				userShippingRepository.save(userShipping);
			}
		}
	}

}
