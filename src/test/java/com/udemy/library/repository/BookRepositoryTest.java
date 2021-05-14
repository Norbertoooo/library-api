package com.udemy.library.repository;

import com.udemy.library.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
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

}
