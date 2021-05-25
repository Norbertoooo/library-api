package com.udemy.library.repository;

import com.udemy.library.domain.Book;
import com.udemy.library.domain.Loan;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@Log4j2
public class LoanRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    LoanRepository loanRepository;

    @Test
    @DisplayName("Should return true when search for a loan")
    public void returnTrueWhenIsbnExistsTest() {
        Book book = Book.builder().author("vitu").title("desgraça").isbn(123231L).build();

        Loan loan = Loan.builder().customer("vitor").book(book).loanDate(LocalDate.now()).build();

        testEntityManager.persist(book);

        testEntityManager.persist(loan);

        Boolean result = loanRepository.existsByBookAndNotReturned(book);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return loan by id")
    public void returnLoanByIdTest() {
        Book book = Book.builder().author("vitu").title("desgraça").isbn(123231L).build();

        Loan loan = Loan.builder().customer("vitor").book(book).loanDate(LocalDate.now()).build();

        testEntityManager.persist(book);

        testEntityManager.persist(loan);

        Optional<Loan> result = loanRepository.findById(loan.getId());

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
    }

    @Test
    @DisplayName("Should return loan using filters")
    public void returnLoanUsingFilterTest() {
        Book book = Book.builder().author("vitu").title("desgraça").isbn(123231L).build();

        Loan loan = Loan.builder().customer("vitor").book(book).loanDate(LocalDate.now()).build();

        testEntityManager.persist(book);

        testEntityManager.persist(loan);

        Page<Loan> result = loanRepository.findByBook_IsbnOrCustomer(123231L,"vitor", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Arrays.asList(loan));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

}
