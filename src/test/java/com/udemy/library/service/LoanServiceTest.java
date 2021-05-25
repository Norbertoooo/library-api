package com.udemy.library.service;

import com.udemy.library.domain.Book;
import com.udemy.library.domain.Loan;
import com.udemy.library.exception.BusinessException;
import com.udemy.library.repository.LoanRepository;
import com.udemy.library.service.impl.LoanServiceImpl;
import com.udemy.library.web.rest.dto.LoanDTO;
import com.udemy.library.web.rest.dto.LoanFilterDTO;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Log4j2
class LoanServiceTest {

    @MockBean
    LoanRepository loanRepositoryMock;

    LoanService loanService;

    @BeforeEach
    public void setUp() {
        this.loanService = new LoanServiceImpl(loanRepositoryMock);
    }

    @Test
    @DisplayName("Should save loan")
    public void saveLoanTest() {

        Book book = Book.builder().id(10L).author("vitu").title("desgraça").isbn(123231L).build();

        Loan loan = Loan.builder().loanDate(LocalDate.now()).customer("vitor").book(book).build();

        Loan savedLoan = Loan.builder().id(1L).loanDate(LocalDate.now()).customer("vitor").book(book).build();

        when(loanRepositoryMock.existsByBookAndNotReturned(book)).thenReturn(false);

        when(loanRepositoryMock.save(loan)).thenReturn(savedLoan);

        Loan loanSaved = loanService.save(loan);

        assertThat(loanSaved.getId()).isEqualTo(savedLoan.getId());
        assertThat(loanSaved.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
        assertThat(loanSaved.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loanSaved.getBook()).isEqualTo(savedLoan.getBook());

    }

    @Test
    @DisplayName("Should throw a exception when try to loan a book that dont is already loaned")
    public void ShouldNotLoanABookTest() {

        Book book = Book.builder().id(1L).isbn(123L).build();
        Loan loan = Loan.builder().book(book).customer("voto").build();

        when(loanRepositoryMock.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> loanService.save(loan));

        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        Mockito.verify(loanRepositoryMock, never()).save(new Loan());
    }


    @Test
    @DisplayName("Should return loan by id")
    public void ShouldReturnLoanByIdTest() {

        Book book = Book.builder().id(10L).author("vitu").title("desgraça").isbn(123231L).build();

        Loan loan = Loan.builder().id(1L).loanDate(LocalDate.now()).customer("vitor").book(book).build();

        Loan savedLoan = Loan.builder().id(1L).loanDate(LocalDate.now()).customer("vitor").book(book).build();

        when(loanRepositoryMock.findById(loan.getId())).thenReturn(Optional.of(loan));

        Optional<Loan> loanSaved = loanService.getById(loan.getId());

        assertThat(loanSaved.isPresent()).isTrue();
        assertThat(loanSaved.get().getLoanDate()).isEqualTo(savedLoan.getLoanDate());
        assertThat(loanSaved.get().getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loanSaved.get().getBook()).isEqualTo(savedLoan.getBook());

        Mockito.verify(loanRepositoryMock, times(1)).findById(anyLong());

    }


    @Test
    @DisplayName("Should update loan")
    public void updateLoanTest() {
        Book book = Book.builder().id(10L).author("vitu").title("desgraça").isbn(123231L).build();

        Loan loan = Loan.builder().id(1L).loanDate(LocalDate.now()).returned(true).customer("vitor").book(book).build();

        when(loanRepositoryMock.save(loan)).thenReturn(loan);

        Loan loanSaved = loanService.update(loan);

        assertThat(loanSaved.getReturned()).isTrue();

        Mockito.verify(loanRepositoryMock, times(1)).save(loan);

    }

    @Test
    @DisplayName("Should return loan using filters")
    public void ShouldFilterLoanTest() throws Exception {
        // cenario
        Book book = Book.builder().id(10L).author("vitu").title("desgraça").isbn(123231L).build();

        Loan loan = Loan.builder().id(1L).loanDate(LocalDate.now()).returned(true).customer("vitor").book(book).build();

        Page<Loan> page = new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1);

        when(loanRepositoryMock.findByBook_IsbnOrCustomer(any(Long.class),any(String.class), any(PageRequest.class))).thenReturn(page);

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().isbn(123231L).customer("vitor").build();
        // execução
        Page<Loan> loanDTOS = loanService.find(0, 10,loanFilterDTO );
        log.info(loanDTOS.toString());

        verify(loanRepositoryMock, times(1)).findByBook_IsbnOrCustomer(any(Long.class),any(String.class), any(PageRequest.class));

        // verificação
        assertThat(loanDTOS.getTotalElements()).isEqualTo(1);
        assertThat(loanDTOS.getContent()).isEqualTo(Arrays.asList(loan));
        assertThat(loanDTOS.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(loanDTOS.getPageable().getPageSize()).isEqualTo(10);

    }
}
