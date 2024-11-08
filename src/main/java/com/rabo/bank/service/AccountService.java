package com.rabo.bank.service;

import com.rabo.bank.dto.AccountDTO;
import com.rabo.bank.dto.BalanceDTO;
import com.rabo.bank.dto.CustomerDTO;
import com.rabo.bank.dto.TransactionDTO;
import com.rabo.bank.entities.Account;
import com.rabo.bank.entities.Customer;
import com.rabo.bank.exception.AccountCreationException;
import com.rabo.bank.exception.TransactionNotAllowedException;
import com.rabo.bank.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.hibernate.exception.ConstraintViolationException;
import org.iban4j.Iban;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerService customerService;

    public AccountService(AccountRepository accountRepository, CustomerService customerService) {
        this.accountRepository = accountRepository;
        this.customerService = customerService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AccountDTO openAccount(@NotNull CustomerDTO customerDTO) {
        try {
            var customer = customerService.createCustomer(customerDTO);

            var account = accountRepository.save(generateNewAccount(customer));

            return new AccountDTO(account.getAccountId(), customer.getAddress(), account.getIban());
        } catch (ConstraintViolationException ex) {
            throw new AccountCreationException("Customer already exists");
        }
    }

    @Transactional(readOnly = true)
    public BalanceDTO getBalance(@NotNull String iban) {
        var account = getAccount(iban);
        return new BalanceDTO(account.getBalance());
    }

    @Transactional
    public void deposit(@NotNull String iban, @NotNull TransactionDTO transactionDTO) {
        var account = getAccount(iban);

        var newBalance = account.getBalance().add(transactionDTO.amount());
        account.setBalance(newBalance);

        accountRepository.save(account);
    }

    @Transactional
    public void withdraw(@NotNull String iban, @NotNull TransactionDTO transactionDTO) {
        var account = getAccount(iban);

        var newBalance = account.getBalance().subtract(transactionDTO.amount());

        if (isNegativeBalance(newBalance)) {
            throw new TransactionNotAllowedException("Insufficient balance");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    @NotNull
    private Account getAccount(String iban) {
        var account = accountRepository.findByIban(iban);

        if (account.isEmpty()) {
            throw new EntityNotFoundException("Account not found");
        }

        return account.get();
    }

    private boolean isNegativeBalance(BigDecimal newBalance) {
        return newBalance.compareTo(BigDecimal.ZERO) < 0;
    }

    private Account generateNewAccount(Customer customer) {
        return new Account(customer.getId(), Iban.random().toString(), BigDecimal.ZERO);
    }
}
