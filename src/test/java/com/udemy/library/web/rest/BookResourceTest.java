package com.udemy.library.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.library.domain.Book;
import com.udemy.library.exception.BusinessException;
import com.udemy.library.service.BookService;
import com.udemy.library.web.rest.dto.BookDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookResource.class)
@AutoConfigureMockMvc
public class BookResourceTest {

    static String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @DisplayName("Should create a book with sucess")
    public void createBookTest() throws Exception {

        BookDTO bookDTO = BookDTO.builder().author("vitu").title("desgraça").isbn(123231L).build();
        Book book = Book.builder().id(10L).author("vitu").title("desgraça").isbn(123231L).build();

        given(bookService.save(Mockito.any(Book.class))).willReturn(book);

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(book.getId()))
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()))
                .andDo(print());

    }

    @Test
    @DisplayName("Should throw validation exception when try to save a book")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(3)))
                .andDo(print());

    }

    @Test
    @DisplayName("Should throw exception when try to save a book with isbn that already exist")
    public void createBookWithDuplicateIsbnTest() throws Exception {

        BookDTO bookDTO = BookDTO.builder().author("vitu").title("desgraça").isbn(123231L).build();

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException("Isbn já cadastrada"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Isbn já cadastrada"))
                .andDo(print());

    }

    @Test
    @DisplayName("Should get book information")
    public void getBookInformaticions() throws Exception {
        // cénario (given, dado).
        Book book = createValidBook();
        given(bookService.findById(book.getId())).willReturn(Optional.of(book));

        // execução (when, quando)
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(BOOK_API.concat("/" + book.getId()))
                .accept(MediaType.APPLICATION_JSON);

        //
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(book.getId()))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return resource not found when search for a book that dont exist")
    public void bookNotFoundTest() throws Exception {
        // cénario (given, dado).
        given(bookService.findById(anyLong())).willReturn(Optional.empty());

        // execução (when, quando)
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(BOOK_API.concat("/" + anyLong()))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("Should delete book")
    public void deleteBookTest() throws Exception {
        // cénario (given, dado).
        Book book = createValidBook();
        given(bookService.findById(book.getId())).willReturn(Optional.of(book));

        // execução (when, quando)
        MockHttpServletRequestBuilder requestBuilder = delete(BOOK_API.concat("/" + book.getId()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("Should return resource not found when delete a book that dont exist")
    public void bookNotFoundTestWhenDeleteBook() throws Exception {
        // cénario (given, dado).
        given(bookService.findById(anyLong())).willReturn(Optional.empty());

        // execução (when, quando)
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete(BOOK_API.concat("/" + anyLong()))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("Should update book")
    public void updateBook() throws Exception {
        // cénario (given, dado)
        Book book = createValidBook();
        Book bookReturned = Book.builder().id(1L).isbn(123321L).author("annhanham").title("houly").build();
        String json = new ObjectMapper().writeValueAsString(book);

        given(bookService.findById(bookReturned.getId())).willReturn(Optional.of(bookReturned));
        given(bookService.update(book)).willReturn(book);

        // execução (when, quando)
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(BOOK_API.concat("/" + bookReturned.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(bookReturned.getId()))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()))
                .andDo(print());
    }

    @Test
    @DisplayName("Should return resource not found when update a book that dont exist")
    public void bookNotFoundTestWhenUpdateBook() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createValidBook());

        // cénario (given, dado).
        given(bookService.findById(anyLong())).willReturn(Optional.empty());

        // execução (when, quando)
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put(BOOK_API.concat("/" + anyLong()))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("Should return a paga book")
    public void findBooksTest() throws Exception {

        Book book = createValidBook();
        // cénario (given, dado).
        given(bookService.find(anyInt(),anyInt(),Mockito.any(Book.class))).willReturn(new PageImpl<>(Arrays.asList(book), PageRequest.of(0,100),1));

        // execução (when, quando)
        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0))
                .andDo(print());
    }


    private Book createValidBook() {
        return Book.builder()
                .id(1L)
                .isbn(123321L)
                .title("o carrasco")
                .author("draven")
                .build();
    }

}
