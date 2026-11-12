package org.example;

import org.example.dto.DepositRequest;
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

        int success = accService.deposit(request.getAccountNumber(), request.getAmount());
        if (success == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found."); // status 404
        }
        else if (success == -2) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error.Transaction rolled back");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Deposit completed successfully");
    }
}
