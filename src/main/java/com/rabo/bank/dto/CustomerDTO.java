package com.rabo.bank.dto;

import com.rabo.bank.entities.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerDTO(@NotNull(message = "First name cannot be null") String firstName,
                          @NotNull(message = "Address cannot be null") String address,
                          @NotNull(message = "E-mail cannot be null") @Email String emailAddress) {

    public Customer toEntity() {
        return new Customer(firstName, address, emailAddress);
    }
}
