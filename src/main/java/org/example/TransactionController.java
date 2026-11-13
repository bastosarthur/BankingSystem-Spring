package org.example;

import org.example.dto.DepositRequest;
import org.example.dto.FindTransactionResponse;
import org.example.dto.TransferRequest;
import org.example.dto.WithdrawRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> accountTransactionHistory(@PathVariable int accountNumber) {

        List<FindTransactionResponse> requestResponseList = accService.getAccountTransactions(accountNumber);

        if (requestResponseList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transactions found."); // Status 404
        }
        return ResponseEntity.status(HttpStatus.OK).body(requestResponseList);
    }

    @PutMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransferRequest request) {

        int transferId = accService.transfer(
                request.getSenderAccount(),
                request.getRecipientAccount(),
                request.getAmount());

        switch (transferId) {
            case -1 -> { // Status 409
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error. Transfer to same account.");
            }
            case -2 -> { // Status 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender Account not found.");
            }
            case -3 -> { // Status 409
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Insufficient balance");
            }
            case -4 -> { // Status 404
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipient Account not found.");
            }
            case 0 -> { // Status 500
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error .Transaction rolled back");
            }
            default -> {
                return ResponseEntity.status(HttpStatus.OK).body(transferId);
            }
        }
    }

    @GetMapping("/{accountNumber}/{date}")
    public ResponseEntity<?> transactionsByDateList(
            @PathVariable int accountNumber, @PathVariable LocalDate date) {

        List<FindTransactionResponse> transactionsByDateList =
                accService.searchTransactionsByDate(accountNumber, date);

        if (transactionsByDateList == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No transactions found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(transactionsByDateList);
    }

    @GetMapping("/deposits/{accountNumber}")
    public ResponseEntity<?> depositList(@PathVariable int accountNumber) {

        List<FindTransactionResponse> depositList = accService.depositList(accountNumber);
        if (depositList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No deposits found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(depositList);
    }

}
