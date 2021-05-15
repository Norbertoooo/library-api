package com.udemy.library.service;

import com.udemy.library.domain.Book;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> findById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Integer pagina, Integer tamanho, Book filter);
}
