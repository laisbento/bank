package com.rabo.bank.service;

import com.rabo.bank.dto.CustomerDTO;
import com.rabo.bank.dto.TransactionDTO;
import com.rabo.bank.entities.Account;
import com.rabo.bank.entities.Customer;
import com.rabo.bank.exception.TransactionNotAllowedException;
import com.rabo.bank.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private AccountService accountService;

    @Test
    void openAccount_shouldCreateAccount() {
        var customerDTO = new CustomerDTO("John", "123 Main St", "john.doe@example.com");
        var customer = new Customer("John", "123 Main St", "john.doe@example.com");

        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(customer);
        when(accountRepository.save(any(Account.class))).thenReturn(new Account(customer.getId(), "NL91RABO0417164300", BigDecimal.ZERO));

        var account = accountService.openAccount(customerDTO);

        assertNotNull(account);
        assertEquals("123 Main St", account.address());
        assertEquals("NL91RABO0417164300", account.iban());
    }

    @Test
    void deposit_shouldIncreaseBalance() {
        var iban = "NL91RABO0417164300";
        var transactionDTO = new TransactionDTO(BigDecimal.valueOf(500));

        var account = new Account(1L, iban, BigDecimal.valueOf(1000));
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));

        accountService.deposit(iban, transactionDTO);

        assertEquals(BigDecimal.valueOf(1500), account.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void withdraw_shouldThrowException_whenInsufficientBalance() {
        var iban = "NL91RABO0417164300";
        var transactionDTO = new TransactionDTO(BigDecimal.valueOf(2000));

        var account = new Account(1L, iban, BigDecimal.valueOf(1000));
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));

        assertThrows(TransactionNotAllowedException.class, () -> accountService.withdraw(iban, transactionDTO));
    }

    @Test
    void deposit_shouldThrowException_whenAccountNotFound() {
        var iban = "NL91RABO0417164300";
        var transactionDTO = new TransactionDTO(BigDecimal.valueOf(500));

        when(accountRepository.findByIban(iban)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.deposit(iban, transactionDTO));
    }

    @Test
    void withdraw_shouldDecreaseBalance() {
        var iban = "NL91RABO0417164300";
        var transactionDTO = new TransactionDTO(BigDecimal.valueOf(500));

        var account = new Account(1L, iban, BigDecimal.valueOf(1000));
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));

        accountService.withdraw(iban, transactionDTO);

        assertEquals(BigDecimal.valueOf(500), account.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testOptimisticLockingException() {
        var iban = "NL91RABO0417164300";

        var account = new Account(1L, iban, BigDecimal.valueOf(3000));
        when(accountRepository.findByIban(account.getIban())).thenReturn(Optional.of(account));

        doThrow(new OptimisticLockException("Simulated Optimistic Locking Failure"))
                .when(accountRepository).save(account);

        assertThrows(TransactionNotAllowedException.class, () -> {
            accountService.withdraw(account.getIban(), new TransactionDTO(BigDecimal.valueOf(1500)));
        });
    }
}
