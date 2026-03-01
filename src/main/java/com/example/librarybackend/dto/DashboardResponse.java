package com.example.librarybackend.dto;
public record DashboardResponse(
        long totalBooks,
        long borrowedBooks,
        long availableBooks,
        long totalBorrowers,
        long borrowingLoans,
        long overdueLoans
) {}
