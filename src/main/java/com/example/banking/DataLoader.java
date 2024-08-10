package com.example.banking;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.banking.entity.MyUser;
import com.example.banking.repository.UserRepository;



@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) throws Exception {
		MyUser user = new MyUser();
		user.setUsername("user");
		user.setPassword(passwordEncoder.encode("password"));
		user.setRoles("USER");
		userRepository.save(user);
	}
}