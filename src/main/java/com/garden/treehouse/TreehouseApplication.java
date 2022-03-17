package com.garden.treehouse;

import com.garden.treehouse.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TreehouseApplication implements CommandLineRunner {

	@Value("${admin-password}")
	private String adminPassword;

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(TreehouseApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(userService.createAdmin(adminPassword));
	}
}
