package com.example.banking.service;

import com.example.banking.entity.Client;
import com.example.banking.repository.ClientRepository;
import com.example.banking.exception.DuplicateUsernameException;
import com.example.banking.exception.InsufficientFundsException;
import com.example.banking.exception.InvalidEmailException;
import com.example.banking.exception.InvalidPhoneNumberException;
import com.example.banking.exception.NegativeAccountBalanceException;
import com.example.banking.exception.ResourceNotFoundException;
import com.example.banking.exception.UsernameAlreadyExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ClientService {

	private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


	@Autowired
	private ClientRepository clientRepository;

	/**
	 * Create a new client with initial data.
	 */
	public Client createClient(Client client) {
		if (clientRepository.existsByUsername(client.getUsername())) {
			throw new DuplicateUsernameException("Username already exists: " + client.getUsername());
		}
		for (String phone : client.getPhones()) {
			if (!PHONE_PATTERN.matcher(phone).matches()) {
				throw new InvalidPhoneNumberException("Invalid phone number: " + phone);
			}
		}
		for (String email : client.getEmails()) {
			if (!EMAIL_PATTERN.matcher(email).matches()) {
				throw new InvalidEmailException("Invalid email address: " + email);
			}
		}
		if (client.getAccount().getBalance().compareTo(BigDecimal.ZERO) < 0) {
			throw new NegativeAccountBalanceException("Account balance cannot be negative");
		}
		return clientRepository.save(client);
	}

	/**
	 * Update a client.
	 */
	public Client updateClient(Client client) {
		if (!clientRepository.existsById(client.getId())) {
			throw new ResourceNotFoundException("Client not found");
		}
		if (client.getAccount().getBalance().compareTo(BigDecimal.ZERO) < 0) {
			throw new NegativeAccountBalanceException("Account balance cannot be negative");
		}
		return clientRepository.save(client);
	}

	/**
	 * Delete a client by ID.
	 */
	public void deleteClient(Long clientId) {
		if (!clientRepository.existsById(clientId)) {
			throw new ResourceNotFoundException("Client not found");
		}
		clientRepository.deleteById(clientId);
	}

	/**
	 * Get a client by ID.
	 */
	public Optional<Client> getClientById(Long clientId) {
		return clientRepository.findById(clientId);
	}

	/**
	 * Search clients by various criteria.
	 */
	public List<Client> searchClients(LocalDate dateOfBirth, String phone, String name, String email) {
		if (dateOfBirth != null) {
			return clientRepository.findByDateOfBirthGreaterThanEqual(dateOfBirth);
		} else if (phone != null) {
			return clientRepository.findByPhonesContaining(phone);
		} else if (name != null) {
			return clientRepository.findByNameStartingWith(name);
		} else if (email != null) {
			return clientRepository.findByEmailsContaining(email);
		} else {
			return clientRepository.findAll();
		}
	}

	/**
	 * Update a phone number for a client.
	 */
	public void updatePhone(Long clientId, String oldPhone, String newPhone) {
		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found"));

		List<String> phones = client.getPhones();
		int index = phones.indexOf(oldPhone);
		if (index != -1) {
			phones.set(index, newPhone);
			client.setPhones(phones);
			clientRepository.save(client);
		} else {
			throw new IllegalArgumentException("Phone number not found");
		}
	}

	/**
	 * Update an email for a client.
	 */
	public void updateEmail(Long clientId, String oldEmail, String newEmail) {
		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found"));

		List<String> emails = client.getEmails();
		int index = emails.indexOf(oldEmail);
		if (index != -1) {
			emails.set(index, newEmail);
			client.setEmails(emails);
			clientRepository.save(client);
		} else {
			throw new IllegalArgumentException("Email not found");
		}
	}

	/**
	 * Delete a phone number for a client. There must be at least one phone number
	 * left.
	 */
	public void deletePhone(Long clientId, String phone) {
		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found"));

		List<String> phones = client.getPhones();
		if (phones.size() > 1 && phones.remove(phone)) {
			client.setPhones(phones);
			clientRepository.save(client);
		} else {
			throw new IllegalArgumentException("Cannot delete the last phone number or phone not found");
		}
	}

	/**
	 * Delete an email for a client. There must be at least one email left.
	 */
	public void deleteEmail(Long clientId, String email) {
		Client client = clientRepository.findById(clientId)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found"));

		List<String> emails = client.getEmails();
		if (emails.size() > 1 && emails.remove(email)) {
			client.setEmails(emails);
			clientRepository.save(client);
		} else {
			throw new IllegalArgumentException("Cannot delete the last email or email not found");
		}
	}

	/**
	 * Search clients by date of birth greater than or equal to the given date.
	 */
	public List<Client> searchClientsByDateOfBirth(LocalDate dateOfBirth) {
		return clientRepository.findByDateOfBirthGreaterThanEqual(dateOfBirth);
	}

	/**
	 * Search clients by phone number.
	 */
	public List<Client> searchClientsByPhone(String phone) {
		return clientRepository.findByPhonesContaining(phone);
	}

	/**
	 * Search clients by name (starting with the given name).
	 */
	public List<Client> searchClientsByName(String name) {
		return clientRepository.findByNameStartingWith(name);
	}

	/**
	 * Search clients by email address.
	 */
	public List<Client> searchClientsByEmail(String email) {
		return clientRepository.findByEmailsContaining(email);
	}

	/**
	 * Transfer money between accounts. Ensures the balance never goes negative.
	 */
	@Transactional
	public void transferMoney(Long fromClientId, Long toClientId, BigDecimal amount) {
		 Client fromClient = clientRepository.findById(fromClientId)
	                .orElseThrow(() -> new ResourceNotFoundException("Sender client not found"));
	        Client toClient = clientRepository.findById(toClientId)
	                .orElseThrow(() -> new ResourceNotFoundException("Recipient client not found"));
	        synchronized (fromClient) {
	            synchronized (toClient) {
	                if (fromClient.getAccount().getBalance().compareTo(amount) < 0) {
	                    throw new InsufficientFundsException("Insufficient funds");
	                }
	                fromClient.getAccount().setBalance(fromClient.getAccount().getBalance().subtract(amount));
	                toClient.getAccount().setBalance(toClient.getAccount().getBalance().add(amount));
	                if (fromClient.getAccount().getBalance().compareTo(BigDecimal.ZERO) < 0) {
	                    throw new NegativeAccountBalanceException("Account balance cannot be negative");
	                }
	                if (toClient.getAccount().getBalance().compareTo(BigDecimal.ZERO) < 0) {
	                    throw new NegativeAccountBalanceException("Account balance cannot be negative");
	                }

	                clientRepository.save(fromClient);
	                clientRepository.save(toClient);
	            }
	        }
	}
	/**
	 * Increment the balance of all clients by 5% every minute, but not more than
	 * 207% of the initial balance.
	 */
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
}
