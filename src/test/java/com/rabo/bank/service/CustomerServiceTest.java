package com.rabo.bank.service;

import com.rabo.bank.dto.CustomerDTO;
import com.rabo.bank.entities.Customer;
import com.rabo.bank.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_shouldSaveAndReturnCustomer() {
        var customerDTO = new CustomerDTO("John", "123 Main St", "john.doe@example.com");
        var expectedCustomer = new Customer("John", "123 Main St", "john.doe@example.com");

        when(customerRepository.save(any(Customer.class))).thenReturn(expectedCustomer);

        var savedCustomer = customerService.createCustomer(customerDTO);

        assertEquals(expectedCustomer.getFirstName(), savedCustomer.getFirstName());
        assertEquals(expectedCustomer.getEmail(), savedCustomer.getEmail());
        assertEquals(expectedCustomer.getAddress(), savedCustomer.getAddress());

        verify(customerRepository).save(any(Customer.class));
    }
}