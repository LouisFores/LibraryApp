package com.example.librarybackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;

    private String category;

    @Column(unique = true)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ✅ URL ảnh bìa (ví dụ: /uploads/books/2/cover.jpg)
    private String coverImageUrl;

    // ✅ đoạn xem trước nội dung (preview)
    @Column(length = 2000)
    private String previewText;

    @PrePersist
    public void prePersist() {
        if (status == null) status = BookStatus.AVAILABLE;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}