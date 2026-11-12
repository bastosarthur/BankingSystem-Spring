package org.example;

import org.example.dto.DepositRequest;
import org.example.dto.FindTransactionResponse;
import org.example.dto.WithdrawRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final AccountService accService;

    public TransactionController(AccountService accService) {
        this.accService = accService;
    }

    @PutMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody DepositRequest request) {

        int transactionId = accService.deposit(request.getAccountNumber(), request.getAmount());
        if (transactionId == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found."); // status 404
        }
        else if (transactionId == -2) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error. Transaction rolled back");
        }

        return ResponseEntity.status(HttpStatus.OK).body(transactionId);
    }

    @PutMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody WithdrawRequest request) {

        int transactionId = accService.withdraw(request.getAccountNumber(), request.getAmount());

        if (transactionId == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found."); // Status 404
        } else if (transactionId == -2) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Not enough balance."); // Status 409
        } else if (transactionId == -3) { // Status 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error. Transaction rolled back");
        }

        return ResponseEntity.status(HttpStatus.OK).body(transactionId);
    }

    @GetMapping
    public ResponseEntity<?> findTransaction(
            @RequestParam int accountNumber, @RequestParam int transactionId) {

        FindTransactionResponse requestResponse =
                accService.getTransaction(accountNumber, transactionId);

        if (requestResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(requestResponse);

    }
}
