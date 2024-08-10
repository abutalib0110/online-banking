package com.example.banking.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.banking.entity.Client;
import com.example.banking.entity.MyUser;
import com.example.banking.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepo;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		MyUser user = userRepo.findByUsername(username).get();
		return User.builder().username(user.getUsername()).password(user.getPassword())
				.build();

	}

}