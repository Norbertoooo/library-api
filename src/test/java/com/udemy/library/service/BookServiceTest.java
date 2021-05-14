package com.udemy.library.service;

import com.udemy.library.domain.Book;
import com.udemy.library.exception.BusinessException;
import com.udemy.library.repository.BookRepository;
import com.udemy.library.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
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

        Throwable throwable = Assertions.catchThrowable( () -> bookService.save(book));

        assertThat(throwable).isInstanceOf(BusinessException.class).hasMessage("Isbn já cadastrada");

        Mockito.verify(bookRepository, never()).save(book);

    }
}
