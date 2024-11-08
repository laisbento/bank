package com.rabo.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabo.bank.dto.CustomerDTO;
import com.rabo.bank.dto.TransactionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Configurações antes de cada teste, se necessário
    }

    @Test
    void openAccount_shouldReturnCreatedAccount() throws Exception {
        var customerDTO = new CustomerDTO("John", "123 Main St", "email@example.com");

        mockMvc.perform(post("/account")
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("admin:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("123 Main St"));
    }

    @Test
    void getBalance_shouldReturnBalance() throws Exception {
        var iban = "NL49RABO0417164300";

        mockMvc.perform(get("/account/{iban}/balance", iban)
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("admin:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").isNotEmpty());
    }

    @Test
    void deposit_shouldIncreaseBalance() throws Exception {
        var iban = "NL49RABO0417164300";
        var transactionDTO = new TransactionDTO(BigDecimal.valueOf(500));

        mockMvc.perform(post("/account/{iban}/deposit", iban)
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("admin:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("1500.00"));
    }

    @Test
    void withdraw_shouldDecreaseBalance() throws Exception {
        var iban = "NL49RABO0417164300";
        var transactionDTO = new TransactionDTO(BigDecimal.valueOf(500));

        mockMvc.perform(post("/account/{iban}/withdraw", iban)
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("admin:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("500.00"));
    }

    @Test
    void withdraw_shouldReturnError_whenInsufficientBalance() throws Exception {
        var iban = "NL49RABO0417164300";
        var transactionDTO = new TransactionDTO(BigDecimal.valueOf(2000));

        mockMvc.perform(post("/account/{iban}/withdraw", iban)
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("admin:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance"));
    }

    @Test
    void getBalance_shouldReturnNotFound_whenAccountNotExists() throws Exception {
        var iban = "NL91ABNA0000000000";

        mockMvc.perform(get("/account/{iban}/balance", iban)
                        .header("Authorization", "Basic " + Base64Utils.encodeToString("admin:password".getBytes()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}