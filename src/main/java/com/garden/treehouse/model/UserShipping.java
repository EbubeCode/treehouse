package com.garden.treehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class UserShipping {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String userShippingName;
	private String userShippingStreet;
	private String userShippingCity;
	private String userShippingState;
	private String userShippingCountry;
	private String userShippingPostcode;
	private boolean userShippingDefault;
	
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonIgnore
	private User user;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getUserShippingName() {
		return userShippingName;
	}


	public void setUserShippingName(String userShippingName) {
		this.userShippingName = userShippingName;
	}


	public String getUserShippingStreet() {
		return userShippingStreet;
	}


	public void setUserShippingStreet(String userShippingStreet1) {
		this.userShippingStreet = userShippingStreet1;
	}


	public String getUserShippingCity() {
		return userShippingCity;
	}


	public void setUserShippingCity(String userShippingCity) {
		this.userShippingCity = userShippingCity;
	}


	public String getUserShippingState() {
		return userShippingState;
	}


	public void setUserShippingState(String userShippingState) {
		this.userShippingState = userShippingState;
	}


	public String getUserShippingCountry() {
		return userShippingCountry;
	}


	public void setUserShippingCountry(String userShippingCountry) {
		this.userShippingCountry = userShippingCountry;
	}


	public String getUserShippingPostcode() {
		return userShippingPostcode;
	}


	public void setUserShippingPostcode(String userShippingZipcode) {
		this.userShippingPostcode = userShippingZipcode;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public boolean isUserShippingDefault() {
		return userShippingDefault;
	}


	public void setUserShippingDefault(boolean userShippingDefault) {
		this.userShippingDefault = userShippingDefault;
	}
	
	
	
	
}
