package com.example.librarybackend.dto;

import java.time.LocalDate;

public record LoanRequest(
        Long borrowerId,
        Long bookId,
        LocalDate dueDate
) {}
