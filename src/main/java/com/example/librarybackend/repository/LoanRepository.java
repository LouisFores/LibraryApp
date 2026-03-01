package com.example.librarybackend.repository;

import com.example.librarybackend.entity.Loan;
import com.example.librarybackend.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByStatus(LoanStatus status);

    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);

    long countByStatus(LoanStatus status);

    long countByStatusAndDueDateBefore(LoanStatus status, LocalDate date);

    boolean existsByBorrowerIdAndStatus(Long borrowerId, LoanStatus status);

    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDate date);

}
