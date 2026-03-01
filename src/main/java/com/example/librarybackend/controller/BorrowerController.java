package com.example.librarybackend.controller;

import com.example.librarybackend.entity.Borrower;
import com.example.librarybackend.entity.LoanStatus;
import com.example.librarybackend.repository.BorrowerRepository;
import com.example.librarybackend.repository.LoanRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/borrowers")
@CrossOrigin
public class BorrowerController {

    private final BorrowerRepository borrowerRepository;

    private final LoanRepository loanRepository;

    public BorrowerController(BorrowerRepository borrowerRepository,
                              LoanRepository loanRepository) {
        this.borrowerRepository = borrowerRepository;
        this.loanRepository = loanRepository;
    }


    @GetMapping
    public List<Borrower> getAll() {
        return borrowerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Borrower getById(@PathVariable Long id) {
        return borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người mượn id=" + id));
    }

    @PostMapping
    public Borrower create(@RequestBody Borrower req) {
        if (req.getFullName() == null || req.getFullName().trim().isEmpty()) {
            throw new RuntimeException("fullName không được để trống");
        }
        if (req.getPhone() == null || req.getPhone().trim().isEmpty()) {
            throw new RuntimeException("phone không được để trống");
        }
        String phone = req.getPhone().trim();
        if (borrowerRepository.existsByPhone(phone)) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        req.setId(null);
        req.setPhone(phone);

        if (req.getCreatedAt() == null) req.setCreatedAt(LocalDateTime.now());
        return borrowerRepository.save(req);
    }

    @PutMapping("/{id}")
    public Borrower update(@PathVariable Long id, @RequestBody Borrower req) {
        Borrower b = borrowerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người mượn id=" + id));

        if (req.getFullName() != null && !req.getFullName().trim().isEmpty()) {
            b.setFullName(req.getFullName().trim());
        }
        if (req.getEmail() != null) b.setEmail(req.getEmail());
        if (req.getAddress() != null) b.setAddress(req.getAddress());

        if (req.getPhone() != null) {
            String newPhone = req.getPhone().trim();
            if (!newPhone.isEmpty() && !newPhone.equals(b.getPhone())) {
                if (borrowerRepository.existsByPhone(newPhone)) {
                    throw new RuntimeException("Số điện thoại đã tồn tại");
                }
                b.setPhone(newPhone);
            }
        }

        return borrowerRepository.save(b);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        if (!borrowerRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy borrower id=" + id);
        }

        if (loanRepository.existsByBorrowerIdAndStatus(id, LoanStatus.BORROWING)) {
            throw new RuntimeException("Không thể xoá borrower vì đang có phiếu mượn chưa trả");
        }

        borrowerRepository.deleteById(id);
        return "OK";
    }

}
