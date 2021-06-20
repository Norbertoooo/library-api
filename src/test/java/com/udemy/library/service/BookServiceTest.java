package com.udemy.library.service;

import com.udemy.library.domain.Book;
import com.udemy.library.exception.BusinessException;
import com.udemy.library.repository.BookRepository;
import com.udemy.library.service.impl.BookServiceImpl;
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

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Log4j2
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    public Book createValidBook() {
        return Book.builder().id(10L).author("vitu").title("desgraça").isbn(123231L).build();
    }

    @Test
    @DisplayName("Should save one valid book")
    public void saveBookTest() {

        Book book = Book.builder().id(10L).author("vitu").title("desgraça").isbn(123231L).build();

        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book);

        Book savedBook = bookService.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());

    }

    @Test
    @DisplayName("Should throw a exception when try to save a book that isbn already exist")
    public void ShouldNotcreateBookWithDuplicateIsbnTest() throws Exception {

        Book book = createValidBook();

        when(bookRepository.existsByIsbn(anyLong())).thenReturn(true);

        Throwable throwable = Assertions.catchThrowable(() -> bookService.save(book));

        assertThat(throwable).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrada");

        Mockito.verify(bookRepository, never()).save(book);

    }

    @Test
    @DisplayName("Should get book by id")
    public void ShouldGetBookByIdTest() throws Exception {

        Book book = createValidBook();

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        Optional<Book> bookReturned = bookService.findById(book.getId());

        Mockito.verify(bookRepository, times(1)).findById(book.getId());

        assertThat(bookReturned.isPresent()).isTrue();
        assertThat(book.getId()).isEqualTo(bookReturned.get().getId());
        assertThat(book.getAuthor()).isEqualTo(bookReturned.get().getAuthor());
        assertThat(book.getTitle()).isEqualTo(bookReturned.get().getTitle());
        assertThat(book.getIsbn()).isEqualTo(bookReturned.get().getIsbn());

    }

    @Test
    @DisplayName("Should throw a exception when try to get a book that dont exist")
    public void ShouldNotFoundBookByIdTest() throws Exception {

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Book> bookReturned = bookService.findById(anyLong());

        Mockito.verify(bookRepository, times(1)).findById(anyLong());

        assertThat(bookReturned.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Should delete book by id")
    public void ShouldDeleteBookByIdTest() throws Exception {

        Book book = createValidBook();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(book));

        Mockito.verify(bookRepository, times(1)).delete(book);

    }


    @Test
    @DisplayName("Should throw a exception when try to delete a book that dont exist")
    public void ShouldNotDeleteBookByIdTest() throws Exception {

        //Throwable throwable = Assertions.catchThrowable( () -> bookService.delete(new Book()));

        //assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Book id cant be null.");

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(new Book()));
        Mockito.verify(bookRepository, never()).delete(new Book());
    }

    @Test
    @DisplayName("Should update book ")
    public void ShouldUpdateBookTest() throws Exception {

        Book book = createValidBook();

        when(bookRepository.save(book)).thenReturn(book);

        Book bookUpdated = bookService.update(book);

        Mockito.verify(bookRepository, times(1)).save(book);

        assertThat(bookUpdated).isNotNull();

    }

    @Test
    @DisplayName("Should throw a exception when try to update a book that dont exist")
    public void ShouldNotUpdateBookByIdTest() throws Exception {

         Throwable throwable = Assertions.catchThrowable(() -> bookService.update(new Book()));

        assertThat(throwable).isInstanceOf(IllegalArgumentException.class).hasMessage("Book id.");

        Mockito.verify(bookRepository, never()).save(new Book());
    }

    @Test
    @DisplayName("Should filter books")
    public void ShouldFilterBookTest() throws Exception {
        // cenario
        Book book = createValidBook();

        Page<Book> page = new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 10), 1);

        when(bookRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(page);

        // execução
        Page<Book> books = bookService.find(0, 10, book);
        log.info(books.toString());

        verify(bookRepository, times(1)).findAll(any(Example.class), any(PageRequest.class));

        // verificação
        assertThat(books.getTotalElements()).isEqualTo(1);
        assertThat(books.getContent()).isEqualTo(Arrays.asList(book));
        assertThat(books.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(books.getPageable().getPageSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("Should get book by isbn")
    void getBookByIsbn() {

        Book book = createValidBook();

        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));

        Optional<Book> bookReturned = bookService.getBookByIsbn(book.getIsbn());

        Mockito.verify(bookRepository, times(1)).findByIsbn(book.getIsbn());

        assertThat(bookReturned.isPresent()).isTrue();
        assertThat(book.getId()).isEqualTo(bookReturned.get().getId());
        assertThat(book.getAuthor()).isEqualTo(bookReturned.get().getAuthor());
        assertThat(book.getTitle()).isEqualTo(bookReturned.get().getTitle());
        assertThat(book.getIsbn()).isEqualTo(bookReturned.get().getIsbn());
    }
}
