package com.rabo.bank.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record BalanceDTO(@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#0.00")
                         BigDecimal balance) {
}
