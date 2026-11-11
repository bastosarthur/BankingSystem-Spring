package org.example;

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
}
