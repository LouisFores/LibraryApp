package com.example.librarybackend.controller;

import com.example.librarybackend.dto.DashboardSummary;
import com.example.librarybackend.entity.BookStatus;
import com.example.librarybackend.entity.LoanStatus;
import com.example.librarybackend.repository.BookRepository;
import com.example.librarybackend.repository.BorrowerRepository;
import com.example.librarybackend.repository.LoanRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final LoanRepository loanRepository;

    public DashboardController(BookRepository bookRepository,
                               BorrowerRepository borrowerRepository,
                               LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
    }

    @GetMapping("/summary")
    public DashboardSummary summary() {
        long totalBooks = bookRepository.count();
        long availableBooks = bookRepository.countByStatus(BookStatus.AVAILABLE);
        long borrowedBooks = bookRepository.countByStatus(BookStatus.BORROWED);

        long totalBorrowers = borrowerRepository.count();

        long borrowingLoans = loanRepository.countByStatus(LoanStatus.BORROWING);
        long overdueLoans = loanRepository.countByStatusAndDueDateBefore(LoanStatus.BORROWING, LocalDate.now());

        return new DashboardSummary(
                totalBooks,
                availableBooks,
                borrowedBooks,
                totalBorrowers,
                borrowingLoans,
                overdueLoans
        );
    }
}
