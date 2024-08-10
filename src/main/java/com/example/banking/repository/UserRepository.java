package com.example.banking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.banking.entity.MyUser;

public interface UserRepository extends JpaRepository<MyUser, Long>{

	
	Optional<MyUser> findByUsername(String myUser);
}
