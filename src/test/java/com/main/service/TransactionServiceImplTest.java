package com.main.service;

import com.main.entity.Transaction;
import com.main.entity.User;
import com.main.enums.TransactionType;
import com.main.exception.InsufficientFundsException;
import com.main.exception.InvalidPaymentException;
import com.main.repository.TransactionRepository;
import com.main.repository.UserRepository;
import com.main.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.main.constants.ErrorsConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    private static final Long TO_ACCOUNT_NUMBER = 12345678L;
    private static final Long FROM_ACCOUNT_NUMBER = 23456789L;
    private static final double BALANCE = 2000.0;
    private static final double AMOUNT = 1000.0;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    public void testDeposit_when_Succeed() {
        User user = new User();
        user.setAccountNumber(TO_ACCOUNT_NUMBER);
        user.setBalance(BALANCE);

        when(userRepository.findById(TO_ACCOUNT_NUMBER)).thenReturn(Optional.of(user));

        boolean result = transactionService.deposit(TO_ACCOUNT_NUMBER, AMOUNT);

        assertTrue(result);
        assertEquals(3000, user.getBalance());
        verify(userRepository, times(1)).save(user);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testDeposit_shouldThrow_whenAmountIsZero() {
        Long accountNumber = 1L;

        InvalidPaymentException ex = assertThrows(InvalidPaymentException.class, () ->
                transactionService.deposit(accountNumber, 0)
        );
        assertEquals(INVALID_PAYMENT_AMOUNT_ZERO, ex.getMessage());
    }

    @Test
    public void testDeposit_shouldThrow_whenUserNotFound() {

        when(userRepository.findById(TO_ACCOUNT_NUMBER)).thenReturn(Optional.empty());

        InvalidPaymentException ex = assertThrows(InvalidPaymentException.class, () ->
                transactionService.deposit(TO_ACCOUNT_NUMBER, AMOUNT)
        );

        assertEquals(SOURCE_ACCOUNT_NOT_FOUND, ex.getMessage());
    }

    @Test
    public void testPay_when_Succeed_Transaction() {
        User fromUser = new User();
        fromUser.setAccountNumber(FROM_ACCOUNT_NUMBER);
        fromUser.setBalance(BALANCE);

        User toUser = new User();
        toUser.setAccountNumber(TO_ACCOUNT_NUMBER);
        toUser.setBalance(BALANCE);

        when(userRepository.findById(FROM_ACCOUNT_NUMBER)).thenReturn(
                Optional.of(fromUser));
        when(userRepository.findById(TO_ACCOUNT_NUMBER)).thenReturn(
                Optional.of(toUser));

        boolean result = transactionService.pay(FROM_ACCOUNT_NUMBER,
                TO_ACCOUNT_NUMBER, AMOUNT);

        assertTrue(result);
        assertEquals(1000.0, fromUser.getBalance());
        assertEquals(3000.0, toUser.getBalance());

        verify(userRepository, times(1)).save(fromUser);
        verify(userRepository, times(1)).save(toUser);
        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }

    @Test
    void pay_shouldThrow_whenInsufficientFunds() {
        User fromUser = new User();
        fromUser.setAccountNumber(FROM_ACCOUNT_NUMBER);
        fromUser.setBalance(BALANCE);

        User toUser = new User();
        toUser.setAccountNumber(TO_ACCOUNT_NUMBER);
        toUser.setBalance(BALANCE);

        when(userRepository.findById(FROM_ACCOUNT_NUMBER)).thenReturn(
                Optional.of(fromUser));
        when(userRepository.findById(TO_ACCOUNT_NUMBER)).thenReturn(
                Optional.of(toUser));

        InsufficientFundsException ex = assertThrows(InsufficientFundsException.class, () ->
                transactionService.pay(FROM_ACCOUNT_NUMBER, TO_ACCOUNT_NUMBER, 3000)
        );

        assertEquals(INVALID_PAYMENT_INSUFFICIENT_AMOUNT, ex.getMessage());

    }

    @Test
    void testPay_shouldThrow_whenFromAndToAccountsAreSame() {

        InvalidPaymentException ex = assertThrows(InvalidPaymentException.class, () ->
                transactionService.pay(FROM_ACCOUNT_NUMBER, FROM_ACCOUNT_NUMBER,
                        AMOUNT)
        );

        assertEquals(BOTH_NOT_SAME_ACCOUNT, ex.getMessage());
    }

    @Test
    public void testGetAllTransactions() {
        List<Transaction> mockTransactions = List.of(
                new Transaction(1L, FROM_ACCOUNT_NUMBER, TO_ACCOUNT_NUMBER,
                        AMOUNT, TransactionType.PAYMENT.PAYMENT,
                        LocalDateTime.now())
        );

        when(transactionRepository.findAll()).thenReturn(mockTransactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        verify(transactionRepository).findAll();
    }
}
