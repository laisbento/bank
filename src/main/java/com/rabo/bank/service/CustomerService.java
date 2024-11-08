package com.rabo.bank.service;

import com.rabo.bank.dto.CustomerDTO;
import com.rabo.bank.entities.Customer;
import com.rabo.bank.repository.CustomerRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(@NotNull CustomerDTO customerDTO) {
        return customerRepository.save(customerDTO.toEntity());
    }
}
