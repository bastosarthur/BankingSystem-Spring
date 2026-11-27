package org.example.controllers;

import org.example.services.AccountService;
import org.example.dto.AccResponseRequest;
import org.example.dto.CreateAccountRequest;
import org.example.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accService;

    public AccountController(AccountService accService) {
        this.accService = accService;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest createAccRequest) {

        int AccountNumber = accService.createAccount(createAccRequest.getAccount(), createAccRequest.getPassword());

        if (AccountNumber == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cpf already registered.");
        } else if (AccountNumber == -2) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Password could not be persisted.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(AccountNumber);
    }

    @PostMapping("/login")
    public ResponseEntity<?> accountLogin(@RequestBody LoginRequest request) {

        AccResponseRequest accResponse = accService.accountLogin(request.getAccountNumber(), request.getPassword());

        if (accResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid account number or password"); // 401
        }

        return ResponseEntity.status(HttpStatus.OK).body(accResponse);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getBalance(@PathVariable int accountNumber) {

        BigDecimal balance = accService.getBalance(accountNumber);

        if (balance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(balance);
    }

    @GetMapping
    public ResponseEntity<?> getAccountsByBalance(@RequestParam BigDecimal balance) {

        List<AccResponseRequest> accList = accService.accountsByBalance(balance);

        if (accList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No accounts with this balance or higher");
        }

        return ResponseEntity.status(HttpStatus.OK).body(accList);
    }
}
