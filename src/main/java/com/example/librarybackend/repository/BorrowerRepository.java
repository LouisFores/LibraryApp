package com.example.librarybackend.repository;

import com.example.librarybackend.entity.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    boolean existsByPhone(String phone);
}
