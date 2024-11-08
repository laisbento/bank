package com.rabo.bank.controller;

import com.rabo.bank.dto.AccountDTO;
import com.rabo.bank.dto.BalanceDTO;
import com.rabo.bank.dto.CustomerDTO;
import com.rabo.bank.dto.TransactionDTO;
import com.rabo.bank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/account")
@SecurityRequirement(name = "basicAuth")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Open a new account", description = "Creates a new bank account for the provided customer details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public AccountDTO openAccount(@RequestBody @Validated CustomerDTO customerDTO) {
        return accountService.openAccount(customerDTO);
    }

    @Operation(summary = "Get account balance", description = "Retrieves the balance for the specified account IBAN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Balance retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalanceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping(value = "/{iban}/balance")
    public BalanceDTO getBalance(@PathVariable String iban) {
        return accountService.getBalance(iban);
    }

    @Operation(summary = "Deposit into account", description = "Deposits a specified amount into the account with the provided IBAN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalanceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping(value = "/{iban}/deposit")
    public BalanceDTO deposit(@PathVariable String iban, @RequestBody TransactionDTO transactionDTO) {
        accountService.deposit(iban, transactionDTO);
        return accountService.getBalance(iban);
    }

    @Operation(summary = "Withdraw from account", description = "Withdraws a specified amount from the account with the provided IBAN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalanceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Account not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Insufficient balance")
    })
    @PostMapping(value = "/{iban}/withdraw")
    public BalanceDTO withdraw(@PathVariable String iban, @RequestBody TransactionDTO transactionDTO) {
        accountService.withdraw(iban, transactionDTO);
        return accountService.getBalance(iban);
    }
}
