package com.udemy.library.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.library.domain.Book;
import com.udemy.library.domain.Loan;
import com.udemy.library.exception.BusinessException;
import com.udemy.library.service.BookService;
import com.udemy.library.service.LoanService;
import com.udemy.library.web.rest.dto.LoanDTO;
import com.udemy.library.web.rest.dto.ReturnedLoanDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanResource.class)
@AutoConfigureMockMvc
public class LoanResourceTest {

    static final String LOAN_URL = "/api/loans";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookServiceMock;

    @MockBean
    LoanService loanServiceMock;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception {

        LoanDTO loanDTO = LoanDTO.builder().isbn(123L).customer("vitor").build();

        Book book = Book.builder().id(1L).isbn(123L).build();

        Loan loan = Loan.builder().id(1L).customer("vitor").book(book).loanDate(LocalDate.now()).build();

        String json = new ObjectMapper().writeValueAsString(loanDTO);

        BDDMockito.given(bookServiceMock.getBookByIsbn(anyLong())).willReturn(Optional.of(book));

        BDDMockito.given(loanServiceMock.save(any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"))
                .andDo(print());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro inexistente")
    public void createLoanErrorTest() throws Exception {

        LoanDTO loanDTO = LoanDTO.builder().isbn(123L).customer("vitor").build();

        Book book = Book.builder().id(1L).isbn(123L).build();

        Loan loan = Loan.builder().id(1L).customer("vitor").book(book).loanDate(LocalDate.now()).build();

        String json = new ObjectMapper().writeValueAsString(loanDTO);

        BDDMockito.given(bookServiceMock.getBookByIsbn(anyLong())).willReturn(Optional.empty());

        BDDMockito.given(loanServiceMock.save(any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"))
                .andDo(print());
    }


    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro já emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception {

        LoanDTO loanDTO = LoanDTO.builder().isbn(123L).customer("vitor").build();

        Book book = Book.builder().id(1L).isbn(123L).build();

        Loan loan = Loan.builder().id(1L).customer("vitor").book(book).loanDate(LocalDate.now()).build();

        String json = new ObjectMapper().writeValueAsString(loanDTO);

        BDDMockito.given(bookServiceMock.getBookByIsbn(anyLong())).willReturn(Optional.of(book));

        BDDMockito.given(loanServiceMock.save(any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(LOAN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"))
                .andDo(print());
    }

    @Test
    @DisplayName("Deve retornar um livro")
    public void returnBookTest() throws Exception {

        ReturnedLoanDTO returnedLoanDTO = ReturnedLoanDTO.builder().returned(true).build();

        Book book = Book.builder().id(1L).isbn(123L).build();

        Loan loan = Loan.builder().id(1L).customer("vitor").book(book).loanDate(LocalDate.now()).build();

        String json = new ObjectMapper().writeValueAsString(returnedLoanDTO);

        BDDMockito.given(loanServiceMock.getById(anyLong())).willReturn(Optional.of(loan));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch(LOAN_URL.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andDo(print());

        verify(loanServiceMock, times(1)).update(any(Loan.class));

    }

    @Test
    @DisplayName("Deve retornar erro quando tentar devolver um livro inexistente")
    public void returnErrorWhenTryReturnBookTest() throws Exception {

        ReturnedLoanDTO returnedLoanDTO = ReturnedLoanDTO.builder().returned(true).build();

        String json = new ObjectMapper().writeValueAsString(returnedLoanDTO);

        BDDMockito.given(loanServiceMock.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch(LOAN_URL.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Loan not found"))
                .andDo(print());

        verify(loanServiceMock, never()).update(any(Loan.class));

    }
}
