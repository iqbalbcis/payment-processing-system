package com.main.controller;

import com.main.entity.Transaction;
import com.main.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.main.constants.CommonConstant.*;

@RestController
@RequestMapping(value = "/api/transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "This operation is used to deposit money")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Money deposit successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @PostMapping(value = "/deposit/{accountNumber}/{amount}")
    public ResponseEntity<String> deposit(@PathVariable(value = "accountNumber")
                                              Long accountNumber,
                                         @PathVariable(value = "amount") double amount) {
        if (String.valueOf(accountNumber).length() != 8) {
            return ResponseEntity.badRequest().body(INVALID_ACCOUNT_NUMBER);
        }

        if (amount <= 0) {
            return ResponseEntity.badRequest().body(INVALID_AMOUNT);
        }
        String result = transactionService.deposit(accountNumber, amount) ?
                PAYMENT_SUCCESSFUL : PAYMENT_FAILURE;
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "This operation is used to pay money")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment successful"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @PostMapping(value = "/pay/{fromAccount}/{toAccount}/{amount}")
    public ResponseEntity<String> pay(
            @PathVariable(value = "fromAccount") Long fromAccount,
                      @PathVariable(value = "toAccount") Long toAccount,
                      @PathVariable(value = "amount") double amount) {

        if (String.valueOf(fromAccount).length() != 8
                || String.valueOf(toAccount).length() != 8) {
            return ResponseEntity.badRequest().body(INVALID_ACCOUNT_NUMBER);
        }

        if (amount <= 0) {
            return ResponseEntity.badRequest().body(INVALID_AMOUNT);
        }
        String result = transactionService.pay(fromAccount, toAccount, amount) ?
                PAYMENT_SUCCESSFUL : PAYMENT_FAILURE;
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "This operation is used to find all transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Find all transactions"),
            @ApiResponse(responseCode = "204", description = "No transaction found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Exception occurred while serving the request")})
    @GetMapping(value = "/transactions", produces = {"application/json"})
    public ResponseEntity<List<Transaction>> getTransactions() {
        List<Transaction> list = transactionService.getAllTransactions();
        if(!list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(list);
        }
        return ResponseEntity.noContent().build();
    }
}
