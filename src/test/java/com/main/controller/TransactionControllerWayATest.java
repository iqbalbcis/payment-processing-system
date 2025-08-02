package com.main.controller;

import com.main.entity.Transaction;
import com.main.enums.TransactionType;
import com.main.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.main.constants.CommonConstant.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MockTransactionServiceConfig.class) // mock service created
@ExtendWith(SpringExtension.class) // when use mock service
public class TransactionControllerWayATest {

    private static final String URL_FOR_DEPOSIT = "/api/transaction/deposit/{accountNumber}/{amount}";
    private static final String URL_FOR_PAY = "/api/transaction/pay/{fromAccount}/{toAccount}/{amount}";
    private static final String URL_FOR_FIND_ALL_TRANSACTION = "/api/transaction/transactions";
    private static final Long TO_ACCOUNT_NUMBER = 12345678L;
    private static final Long FROM_ACCOUNT_NUMBER = 23456789L;
    private static final Long INVALID_ACCOUNT = 1234567L;
    private static final double AMOUNT = 1000.0;
    private static final double INVALID_VALUE = 0.0;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionService transactionService;
    // no database connection need as service layer is mocked

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeposit_shouldReturn201_whenValidRequest() throws Exception {

        when(transactionService.deposit(TO_ACCOUNT_NUMBER, AMOUNT)).thenReturn(true);

        mockMvc.perform(post(URL_FOR_DEPOSIT, TO_ACCOUNT_NUMBER, AMOUNT))
                .andExpect(status().isCreated())
                .andExpect(content().string(PAYMENT_SUCCESSFUL));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeposit_shouldReturn400_whenAmountIsInvalid() throws Exception {
        mockMvc.perform(post(URL_FOR_DEPOSIT, TO_ACCOUNT_NUMBER, INVALID_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_AMOUNT));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void deposit_shouldReturn400_whenAccountNumberIsInvalid() throws Exception {
        mockMvc.perform(post(URL_FOR_DEPOSIT, INVALID_ACCOUNT, AMOUNT))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_ACCOUNT_NUMBER));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testPay_shouldReturn201_whenValidRequest() throws Exception {

        when(transactionService.pay(FROM_ACCOUNT_NUMBER, TO_ACCOUNT_NUMBER, AMOUNT))
                .thenReturn(true);

        mockMvc.perform(post(URL_FOR_PAY, FROM_ACCOUNT_NUMBER, TO_ACCOUNT_NUMBER, AMOUNT))
                .andExpect(status().isCreated())
                .andExpect(content().string(PAYMENT_SUCCESSFUL));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testPay_shouldReturn400_whenAmountIsInvalid() throws Exception {

        mockMvc.perform(post(URL_FOR_PAY, FROM_ACCOUNT_NUMBER, TO_ACCOUNT_NUMBER, INVALID_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_AMOUNT));

    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testPay_shouldReturn400_whenAccountNumberInvalid() throws Exception {
        mockMvc.perform(post(URL_FOR_PAY, FROM_ACCOUNT_NUMBER, INVALID_ACCOUNT, AMOUNT))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_ACCOUNT_NUMBER));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetTransactions_shouldReturn200_whenTransactionsExist() throws Exception {
        List<Transaction> transactions = List.of(
                new Transaction(1L, FROM_ACCOUNT_NUMBER, TO_ACCOUNT_NUMBER, AMOUNT,
                        TransactionType.DEPOSIT, LocalDateTime.now())
        );

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get(URL_FOR_FIND_ALL_TRANSACTION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetTransactions_shouldReturn204_whenNoTransactions() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(URL_FOR_FIND_ALL_TRANSACTION))
                .andExpect(status().isNoContent());
    }
}
