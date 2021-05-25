package com.udemy.library.service;

import com.udemy.library.domain.Loan;
import com.udemy.library.web.rest.dto.LoanFilterDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loanClass);

    Optional<Loan> getById(Long bookId);

    Loan update(Loan loan);

    Page<Loan> find(int pagina, int tamanho, LoanFilterDTO any);
}
