package com.main.service;

import com.main.entity.Transaction;

import java.util.List;

public interface TransactionService {

    boolean deposit(Long accountNumber, double amount);
    boolean pay(Long fromAccount, Long toAccount, double amount);
    List<Transaction> getAllTransactions();
}
