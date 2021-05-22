package com.udemy.library.repository;

import com.udemy.library.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(Long isbn);

    Optional<Book> findByIsbn(Long isbn);
}
