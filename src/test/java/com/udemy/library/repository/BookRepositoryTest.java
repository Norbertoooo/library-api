package com.udemy.library.repository;

import com.udemy.library.domain.Book;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@Log4j2
public class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Should return true when search for a book by isbn that already exist")
    public void returnTrueWhenIsbnExists() {

        testEntityManager.persist(Book.builder().isbn(123L).build());

        Boolean resposta = bookRepository.existsByIsbn(123L);

        assertThat(resposta).isTrue();
    }

    @Test
    @DisplayName("Should return false when search for a book by isbn that dont exist")
    public void returnFalseWhenIsbnDontExists() {

        Boolean resposta = bookRepository.existsByIsbn(123L);

        assertThat(resposta).isFalse();
    }

    @Test
    @DisplayName("Should return true when search for a book by id that already exist")
    public void returnTrueWhenIdExists() {

        Book bookPersisted = Book.builder().title("o carrasco").author("draven").isbn(1233L).build();

        testEntityManager.persist(bookPersisted);

        Optional<Book> book = bookRepository.findById(bookPersisted.getId());

        assertThat(book.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Should return false when search for a book by id that dont exist")
    public void returnFalseWhenBookIdDontExists() {

        Optional<Book> resposta = bookRepository.findById(123L);

        assertThat(resposta.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should return true when search for a book by id that already exist")
    public void saveBookTest() {

        Book bookPersisted = Book.builder().title("o carrasco").author("draven").isbn(1233L).build();

        testEntityManager.persist(bookPersisted);

        Book book = bookRepository.save(bookPersisted);

        assertThat(book).isNotNull();
        assertThat(book.getId()).isNotNull();

    }

    @Test
    @DisplayName("Should delete book")
    public void deleteBookTest() {

        Book book = Book.builder().title("o carrasco").author("draven").isbn(1233L).build();

        log.info(book);

        Book BookPersisted = testEntityManager.persist(book);

        Book bookFounded = testEntityManager.find(Book.class, BookPersisted.getId());

        log.info(bookFounded);

        bookRepository.delete(bookFounded);

        Book bookDeleted = testEntityManager.find(Book.class, BookPersisted.getId());

        log.info(bookDeleted);

        assertThat(bookDeleted).isNull();

    }

}
