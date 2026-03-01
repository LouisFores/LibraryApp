package com.example.librarybackend.controller;

import com.example.librarybackend.dto.LoanRequest;
import com.example.librarybackend.entity.Loan;
import com.example.librarybackend.entity.LoanStatus;
import com.example.librarybackend.repository.LoanRepository;
import com.example.librarybackend.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin
public class LoanController {

    private final LoanService loanService;
    private final LoanRepository loanRepository;

    public LoanController(LoanService loanService, LoanRepository loanRepository) {
        this.loanService = loanService;
        this.loanRepository = loanRepository;
    }

    @GetMapping
    public List<Loan> getAll(@RequestParam(required = false) String status) {
        if (status == null || status.isBlank()) {
            return loanRepository.findAll();
        }

        if ("OVERDUE".equalsIgnoreCase(status)) {
            return loanRepository.findByStatusAndDueDateBefore(
                    LoanStatus.BORROWING,
                    LocalDate.now()
            );
        }

        try {
            return loanRepository.findByStatus(
                    LoanStatus.valueOf(status.toUpperCase())
            );
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Status không hợp lệ");
        }
    }

    @PostMapping
    public Loan borrow(@RequestBody LoanRequest req) {
        return loanService.borrowBook(req);
    }

    // ✅ Trả theo loanId
    @PostMapping("/{id}/return")
    public Loan returnLoan(@PathVariable Long id) {
        return loanService.returnBook(id);
    }

}
