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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
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
    public void returnTrueWhenIsbnExists() {
        Book book = Book.builder().author("vitu").title("desgraça").isbn(123231L).build();

        Loan loan = Loan.builder().customer("vitor").book(book).loanDate(LocalDate.now()).build();

        testEntityManager.persist(book);

        testEntityManager.persist(loan);

        Boolean result = loanRepository.existsByBookAndNotReturned(book);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return loan by id")
    public void returnLoanById() {
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

}
