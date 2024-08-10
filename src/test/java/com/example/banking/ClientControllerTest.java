// package com.example.banking.controller;

// import com.example.banking.entity.Account;
// import com.example.banking.entity.Client;
// import com.example.banking.exception.ResourceNotFoundException;
// import com.example.banking.repository.ClientRepository;
// import com.example.banking.service.ClientService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;

// import java.math.BigDecimal;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.*;

// public class ClientControllerTest {

//     @InjectMocks
//     private ClientController clientController;

//     @Mock
//     private ClientService clientService;

//     @Mock
//     private ClientRepository clientRepository;

//     @BeforeEach
//     public void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     public void testTransferMoney_Success() {
//         // Arrange
//         Long fromClientId = 1L;
//         Long toClientId = 2L;
//         BigDecimal amount = new BigDecimal("100.00");

//         Client fromClient = new Client();
//         fromClient.setId(fromClientId);
//         Account fromAccount = new Account();
//         fromAccount.setBalance(new BigDecimal("500.00"));
//         fromClient.setAccount(fromAccount);

//         Client toClient = new Client();
//         toClient.setId(toClientId);
//         Account toAccount = new Account();
//         toAccount.setBalance(new BigDecimal("200.00"));
//         toClient.setAccount(toAccount);

//         when(clientRepository.findById(fromClientId)).thenReturn(Optional.of(fromClient));
//         when(clientRepository.findById(toClientId)).thenReturn(Optional.of(toClient));
//         when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         // Act
//         clientController.transferMoney(fromClientId, toClientId, amount);

//         // Assert
//         verify(clientRepository, times(2)).save(any(Client.class));
//         assertEquals(new BigDecimal("400.00"), fromClient.getAccount().getBalance());
//         assertEquals(new BigDecimal("300.00"), toClient.getAccount().getBalance());
//     }

//     @Test
//     public void testTransferMoney_InsufficientFunds() {
//         // Arrange
//         Long fromClientId = 1L;
//         Long toClientId = 2L;
//         BigDecimal amount = new BigDecimal("600.00");

//         Client fromClient = new Client();
//         fromClient.setId(fromClientId);
//         Account fromAccount = new Account();
//         fromAccount.setBalance(new BigDecimal("500.00"));
//         fromClient.setAccount(fromAccount);

//         Client toClient = new Client();
//         toClient.setId(toClientId);
//         Account toAccount = new Account();
//         toAccount.setBalance(new BigDecimal("200.00"));
//         toClient.setAccount(toAccount);
//         when(clientRepository.findById(fromClientId)).thenReturn(Optional.of(fromClient));
//         when(clientRepository.findById(toClientId)).thenReturn(Optional.of(toClient));
//         IllegalArgumentException thrown = org.junit.jupiter.api.Assertions.assertThrows(
//                 IllegalArgumentException.class,
//                 () -> clientController.transferMoney(fromClientId, toClientId, amount)
//         );
//         assertEquals("Insufficient funds", thrown.getMessage());
//     }

//     @Test
//     public void testTransferMoney_ClientNotFound() {
//         Long fromClientId = 1L;
//         Long toClientId = 2L;
//         BigDecimal amount = new BigDecimal("100.00");

//         when(clientRepository.findById(fromClientId)).thenReturn(Optional.empty());
//         when(clientRepository.findById(toClientId)).thenReturn(Optional.of(new Client()));

//         // Act & Assert
//         ResourceNotFoundException thrown = org.junit.jupiter.api.Assertions.assertThrows(
//                 ResourceNotFoundException.class,
//                 () -> clientController.transferMoney(fromClientId, toClientId, amount)
//         );
//         assertEquals("Sender client not found", thrown.getMessage());
//     }
// }
