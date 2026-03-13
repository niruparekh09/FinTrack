package com.fintrack.api.transaction;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions")
public class TransactionController {

    TransactionService service;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction
            (@Valid @RequestBody TransactionRequest request) {
        TransactionResponse transactionResponse = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction
            (@Valid @RequestBody TransactionRequest request, @PathVariable Long id) {
        TransactionResponse transactionResponse = service.update(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(transactionResponse);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTransaction
            (@PathVariable Long id) {
        String delete = service.delete(id);

        return ResponseEntity.status(HttpStatus.OK).body(delete);
    }


    // GET /api/transactions?type=EXPENSE&category=Food&page=0&size=5&sort=date,desc
    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer month,

            Pageable pageable
    ) {
        Page<TransactionResponse> transactionResponses = service.getTransactions(type, category, month, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(transactionResponses);
    }
}
