package com.example.banking.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.entity.Client;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.repository.ClientRepository;
import com.example.banking.service.ClientService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Create a new client with initial data (unsecured).
     */
    @PostMapping("/create")
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        Client createdClient = clientService.createClient(client);
        return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
    }

    /**
     * Get a client by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientService.getClientById(id);
        return client.map(ResponseEntity::ok)
                     .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
    }

    /**
     * Update client details. Only phones and emails are modifiable.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client clientDetails) {
        clientDetails.setId(id);
        Client updatedClient = clientService.updateClient(clientDetails);
        return ResponseEntity.ok(updatedClient);
    }

    /**
     * Delete a client by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search clients with filters and pagination.
     */
    @GetMapping("/search")
    public ResponseEntity<List<Client>> searchClients(
            @RequestParam(required = false) String dateOfBirth,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

    	LocalDate parsedDateOfBirth = null;
        if (dateOfBirth != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                parsedDateOfBirth = LocalDate.parse(dateOfBirth, formatter);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        List<Client> clients = clientService.searchClients(parsedDateOfBirth, phone, name, email);
        return ResponseEntity.ok(clients);
    }

    /**
     * Update a phone number for a client.
     */
    @PutMapping("/{id}/phones")
    public ResponseEntity<Void> updatePhone(@PathVariable Long id, @RequestParam String oldPhone, @RequestParam String newPhone) {
        clientService.updatePhone(id, oldPhone, newPhone);
        return ResponseEntity.ok().build();
    }

    /**
     * Update an email for a client.
     */
    @PutMapping("/{id}/emails")
    public ResponseEntity<Void> updateEmail(@PathVariable Long id, @RequestParam String oldEmail, @RequestParam String newEmail) {
        clientService.updateEmail(id, oldEmail, newEmail);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a phone number for a client.
     */
    @DeleteMapping("/{id}/phones")
    public ResponseEntity<Void> deletePhone(@PathVariable Long id, @RequestParam String phone) {
        clientService.deletePhone(id, phone);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete an email for a client.
     */
    @DeleteMapping("/{id}/emails")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id, @RequestParam String email) {
        clientService.deleteEmail(id, email);
        return ResponseEntity.noContent().build();
    }

    
    @Scheduled(fixedRate = 60000) 
    @Transactional
    public void incrementBalance() {
        List<Client> clients = clientRepository.findAll();

        for (Client client : clients) {
            BigDecimal initialBalance = client.getAccount().getInitialBalance();
            BigDecimal currentBalance = client.getAccount().getBalance();
            BigDecimal maxBalance = initialBalance.multiply(new BigDecimal("2.07"));
            BigDecimal increment = currentBalance.multiply(new BigDecimal("0.05"));

            if (currentBalance.add(increment).compareTo(maxBalance) > 0) {
                client.getAccount().setBalance(maxBalance);
            } else {
                client.getAccount().setBalance(currentBalance.add(increment));
            }

            clientRepository.save(client);
        }
    }

    /**
     * Transfer money between accounts. 
     */
    @Transactional
    public void transferMoney(Long fromClientId, Long toClientId, BigDecimal amount) {
        // Locking to prevent race conditions
        Client fromClient = clientRepository.findById(fromClientId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender client not found"));
        Client toClient = clientRepository.findById(toClientId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipient client not found"));

        synchronized (fromClient) {
            synchronized (toClient) {
                if (fromClient.getAccount().getBalance().compareTo(amount) < 0) {
                    throw new IllegalArgumentException("Insufficient funds");
                }

                fromClient.getAccount().setBalance(fromClient.getAccount().getBalance().subtract(amount));
                toClient.getAccount().setBalance(toClient.getAccount().getBalance().add(amount));

                clientRepository.save(fromClient);
                clientRepository.save(toClient);
            }
        }
    }
}
