package com.udemy.library.service;

import com.udemy.library.domain.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> findById(Long id);

    void delete(Book book);

    Book update(Book book);
}
