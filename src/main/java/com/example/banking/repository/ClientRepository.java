package com.example.banking.repository;

import com.example.banking.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

	
    List<Client> findByDateOfBirthGreaterThanEqual(LocalDate dateOfBirth);    
    List<Client> findByPhonesContaining(String phone);    
    List<Client> findByNameStartingWith(String name);    
    List<Client> findByEmailsContaining(String email);    
    boolean existsByUsername(String username);    
    boolean existsByEmailsContaining(String email); 
    boolean existsByPhonesContaining(String phone); 
}
