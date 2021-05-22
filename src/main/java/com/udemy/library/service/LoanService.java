package com.udemy.library.service;

import com.udemy.library.domain.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loanClass);

    Optional<Loan> getById(Long bookId);

    Loan update(Loan loan);
}
