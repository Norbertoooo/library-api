package com.udemy.library.service.impl;

import com.udemy.library.domain.Loan;
import com.udemy.library.exception.BusinessException;
import com.udemy.library.repository.LoanRepository;
import com.udemy.library.service.LoanService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if (loanRepository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already loaned");
        }
        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long bookId) {
        return loanRepository.findById(bookId);
    }

    @Override
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }
}
