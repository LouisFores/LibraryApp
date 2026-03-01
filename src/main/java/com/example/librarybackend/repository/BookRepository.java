package com.example.librarybackend.repository;

import com.example.librarybackend.entity.Book;
import com.example.librarybackend.entity.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByIsbn(String isbn);
    long countByStatus(BookStatus status);
}