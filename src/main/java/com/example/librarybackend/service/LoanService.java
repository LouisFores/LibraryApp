package com.example.librarybackend.service;

import com.example.librarybackend.dto.LoanRequest;
import com.example.librarybackend.entity.Book;
import com.example.librarybackend.entity.BookStatus;
import com.example.librarybackend.entity.Borrower;
import com.example.librarybackend.entity.Loan;
import com.example.librarybackend.entity.LoanStatus;
import com.example.librarybackend.repository.BookRepository;
import com.example.librarybackend.repository.BorrowerRepository;
import com.example.librarybackend.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    public LoanService(LoanRepository loanRepository,
                       BookRepository bookRepository,
                       BorrowerRepository borrowerRepository) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    @Transactional
    public Loan borrowBook(LoanRequest req) {
        // Validate input
        if (req == null) {
            throw new RuntimeException("Request không được để trống");
        }
        if (req.borrowerId() == null || req.bookId() == null) {
            throw new RuntimeException("borrowerId và bookId không được để trống");
        }
        if (req.dueDate() == null) {
            throw new RuntimeException("dueDate không được để trống");
        }

        // Load borrower
        Borrower borrower = borrowerRepository.findById(req.borrowerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy borrowerId=" + req.borrowerId()));

        // Load book
        Book book = bookRepository.findById(req.bookId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bookId=" + req.bookId()));

        // Defensive: nếu dữ liệu cũ bị null status thì ép về AVAILABLE
        if (book.getStatus() == null) {
            book.setStatus(BookStatus.AVAILABLE);
        }

        // ❌ Chặn mượn nếu sách đang BORROWED
        if (book.getStatus() == BookStatus.BORROWED) {
            throw new RuntimeException("Sách đang được mượn, không thể mượn tiếp");
        }

        // ❌ Chặn mượn nếu đã có loan BORROWING cho cuốn này (chống dữ liệu lệch)
        if (loanRepository.existsByBookIdAndStatus(book.getId(), LoanStatus.BORROWING)) {
            throw new RuntimeException("Sách đang có phiếu mượn BORROWING");
        }

        // Tạo loan mới
        Loan loan = new Loan();
        loan.setBorrower(borrower);
        loan.setBook(book);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(req.dueDate());
        loan.setStatus(LoanStatus.BORROWING);

        // Cập nhật trạng thái sách -> BORROWED
        book.setStatus(BookStatus.BORROWED);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        if (loanId == null) {
            throw new RuntimeException("loanId không được để trống");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loanId=" + loanId));

        // ❌ Chặn trả 2 lần
        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new RuntimeException("Phiếu mượn đã trả rồi");
        }

        // Book đi kèm loan
        Book book = loan.getBook();
        if (book == null) {
            throw new RuntimeException("Loan không có book đi kèm");
        }

        // Update loan
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());

        // Update book -> AVAILABLE
        if (book.getStatus() == null) {
            book.setStatus(BookStatus.AVAILABLE);
        } else {
            book.setStatus(BookStatus.AVAILABLE);
        }
        bookRepository.save(book);

        return loanRepository.save(loan);
    }
}
