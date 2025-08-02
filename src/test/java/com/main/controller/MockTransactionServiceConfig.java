package com.main.controller;

import com.main.service.TransactionService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockTransactionServiceConfig {
    @Bean
    public TransactionService transactionService() {
        return Mockito.mock(TransactionService.class);
    }
}
