package com.example.librarybackend.controller;

import com.example.librarybackend.entity.Book;
import com.example.librarybackend.entity.BookStatus;
import com.example.librarybackend.exception.BusinessException;
import com.example.librarybackend.repository.BookRepository;
import com.example.librarybackend.service.FileStorageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {

    private final BookRepository bookRepository;

    private final FileStorageService fileStorageService;

    public BookController(BookRepository bookRepository, FileStorageService fileStorageService) {
        this.bookRepository = bookRepository;
        this.fileStorageService = fileStorageService;
    }


    @GetMapping
    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách id=" + id));
    }

    @PostMapping
    public Book create(@RequestBody Book request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Title không được để trống");
        }

        if (request.getIsbn() != null && !request.getIsbn().trim().isEmpty()) {
            String isbn = request.getIsbn().trim();
            if (bookRepository.existsByIsbn(isbn)) {
                throw new RuntimeException("ISBN đã tồn tại");
            }
            request.setIsbn(isbn);
        } else {
            request.setIsbn(null);
        }

        // ép default để KHÔNG BAO GIỜ null
        if (request.getStatus() == null) request.setStatus(BookStatus.AVAILABLE);
        if (request.getCreatedAt() == null) request.setCreatedAt(LocalDateTime.now());

        request.setId(null);
        return bookRepository.save(request);
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @RequestBody Book request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách id=" + id));

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            book.setTitle(request.getTitle().trim());
        }
        if (request.getAuthor() != null) book.setAuthor(request.getAuthor());
        if (request.getCategory() != null) book.setCategory(request.getCategory());
        if (request.getPreviewText() != null) book.setPreviewText(request.getPreviewText());
        if (request.getCoverImageUrl() != null) book.setCoverImageUrl(request.getCoverImageUrl()); // optional

        // update isbn nhưng vẫn unique
        if (request.getIsbn() != null) {
            String newIsbn = request.getIsbn().trim();
            String oldIsbn = book.getIsbn();

            if (!newIsbn.isEmpty() && (oldIsbn == null || !newIsbn.equals(oldIsbn))) {
                if (bookRepository.existsByIsbn(newIsbn)) {
                    throw new RuntimeException("ISBN đã tồn tại");
                }
                book.setIsbn(newIsbn);
            } else if (newIsbn.isEmpty()) {
                book.setIsbn(null);
            }
        }

        // status chỉ cho update nếu gửi lên
        if (request.getStatus() != null) book.setStatus(request.getStatus());

        return bookRepository.save(book);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách id=" + id));

        if (book.getStatus() == BookStatus.BORROWED) {
            throw new BusinessException("Không thể xoá sách đang được mượn");
        }

        bookRepository.delete(book);
        return "OK";
    }



    @PostMapping("/{id}/cover")
    public Book uploadCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws Exception {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sách id=" + id));

        String url = fileStorageService.saveBookCover(id, file);
        book.setCoverImageUrl(url);

        return bookRepository.save(book);
    }


}