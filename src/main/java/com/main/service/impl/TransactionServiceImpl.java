package com.main.service.impl;

import com.main.entity.Transaction;
import com.main.entity.User;
import com.main.enums.TransactionType;
import com.main.exception.InsufficientFundsException;
import com.main.exception.InvalidPaymentException;
import com.main.repository.TransactionRepository;
import com.main.repository.UserRepository;
import com.main.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.main.constants.ErrorsConstant.*;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public boolean deposit(Long accountNumber, double amount) {
        if (amount <= 0)
            throw new InvalidPaymentException(INVALID_PAYMENT_AMOUNT_ZERO);;

        User user = userRepository.findById(accountNumber).orElseThrow(()->
                new InvalidPaymentException(SOURCE_ACCOUNT_NOT_FOUND));

        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);

        transactionRepository.save(
                new Transaction(null, null,
                        accountNumber, amount, TransactionType.DEPOSIT,
                        LocalDateTime.now())
        );
        return true;
    }

    @Transactional
    @Override
    public boolean pay(Long fromAccount, Long toAccount, double amount) {

        if (amount <= 0)
            throw new InvalidPaymentException(INVALID_PAYMENT_AMOUNT_ZERO);

        if (fromAccount == toAccount)
            throw new InvalidPaymentException(BOTH_NOT_SAME_ACCOUNT);

        User from = userRepository.findById(fromAccount).orElseThrow(()->
                new InvalidPaymentException(SOURCE_ACCOUNT_NOT_FOUND));
        User to = userRepository.findById(toAccount).orElseThrow(()->
                new InvalidPaymentException(DESTINATION_ACCOUNT_NOT_FOUND));

        if (from.getBalance() < amount)
            throw new InsufficientFundsException(INVALID_PAYMENT_INSUFFICIENT_AMOUNT);

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        userRepository.save(from);
        userRepository.save(to);

        transactionRepository.save(
                new Transaction(null, fromAccount,
                        toAccount, amount, TransactionType.PAYMENT,
                        LocalDateTime.now())
        );
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
