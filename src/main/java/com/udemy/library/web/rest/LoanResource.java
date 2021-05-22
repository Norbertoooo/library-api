package com.udemy.library.web.rest;

import com.udemy.library.domain.Book;
import com.udemy.library.domain.Loan;
import com.udemy.library.service.BookService;
import com.udemy.library.service.LoanService;
import com.udemy.library.web.rest.dto.BookDTO;
import com.udemy.library.web.rest.dto.LoanDTO;
import com.udemy.library.web.rest.dto.ReturnedLoanDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Log4j2
public class LoanResource {

    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody @Valid LoanDTO loanDTO) {
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));

        Loan loan = Loan.builder()
                .book(book)
                .loanDate(LocalDate.now())
                .customer(loanDTO.getCustomer())
                .build();

        return loanService.save(loan).getId();
    }

    @PatchMapping("/{bookId}")
    public void returnBook(@RequestBody @Valid ReturnedLoanDTO returnedLoanDTO, @PathVariable Long bookId) {
        Loan loan = loanService.getById(bookId)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));

        loan.setReturned(returnedLoanDTO.getReturned());

        loanService.update(loan);
    }
}
