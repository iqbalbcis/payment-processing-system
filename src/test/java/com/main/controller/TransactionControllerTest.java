package com.main.controller;

import com.main.entity.Transaction;
import com.main.entity.User;
import com.main.enums.TransactionType;
import com.main.repository.TransactionRepository;
import com.main.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.main.constants.CommonConstant.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionControllerTest {

    private static final String URL_FOR_DEPOSIT = "/api/transaction/deposit/{accountNumber}/{amount}";
    private static final String URL_FOR_PAY = "/api/transaction/pay/{fromAccount}/{toAccount}/{amount}";
    private static final String URL_FOR_FIND_ALL_TRANSACTION = "/api/transaction/transactions";
    private static final Long TO_ACCOUNT_NUMBER = 12345678L;
    private static final Long FROM_ACCOUNT_NUMBER = 23456789L;
    private static final Long INVALID_ACCOUNT = 1234567L;
    private static final double AMOUNT = 1000.0;
    private static final double BALANCE = 2000.0;
    private static final double INVALID_VALUE = 0.0;
    private static final String EMAIL_A = "testa@gmail.com";
    private static final String EMAIL_B = "testa@gmail.com";
    private static final String NAME_A = "IqbalA";
    private static final String NAME_B = "IqbalB";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();

        userRepository.save(User.builder()
                .accountNumber(TO_ACCOUNT_NUMBER)
                .email(EMAIL_A)
                .name(NAME_A)
                .balance(BALANCE)
                .build());
    }

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        mysql.start();
    }

    @AfterAll
    static void afterAll() {
        mysql.stop();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testDeposit_shouldReturn201_whenValidRequest() throws Exception {

        mockMvc.perform(post(URL_FOR_DEPOSIT, TO_ACCOUNT_NUMBER, AMOUNT))
                .andExpect(status().isCreated())
                .andExpect(content().string(PAYMENT_SUCCESSFUL));

        User user = userRepository.findById(TO_ACCOUNT_NUMBER).orElseThrow();
        assertEquals(BALANCE+AMOUNT, user.getBalance());
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

        userRepository.save(User.builder()
                .accountNumber(FROM_ACCOUNT_NUMBER)
                .email(EMAIL_B)
                .name(NAME_B)
                .balance(BALANCE)
                .build());

        mockMvc.perform(post(URL_FOR_PAY, FROM_ACCOUNT_NUMBER, TO_ACCOUNT_NUMBER, AMOUNT))
                .andExpect(status().isCreated())
                .andExpect(content().string(PAYMENT_SUCCESSFUL));

        User sender = userRepository.findById(FROM_ACCOUNT_NUMBER).orElseThrow();
        User receiver = userRepository.findById(TO_ACCOUNT_NUMBER).orElseThrow();

        assertEquals(1000.0, sender.getBalance());
        assertEquals(3000.0, receiver.getBalance());
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
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetTransactions_shouldReturn200_whenTransactionsExist() throws Exception {
        transactionRepository.save(new Transaction(null, FROM_ACCOUNT_NUMBER,
                TO_ACCOUNT_NUMBER, AMOUNT, TransactionType.DEPOSIT,
                LocalDateTime.now()));

        mockMvc.perform(get(URL_FOR_FIND_ALL_TRANSACTION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Order(1)
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void testGetTransactions_shouldReturn204_whenNoTransactions() throws Exception {

        mockMvc.perform(get(URL_FOR_FIND_ALL_TRANSACTION))
                .andExpect(status().isNoContent());
    }
}
