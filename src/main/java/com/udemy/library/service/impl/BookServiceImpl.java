package com.udemy.library.service.impl;

import com.udemy.library.domain.Book;
import com.udemy.library.exception.BusinessException;
import com.udemy.library.repository.BookRepository;
import com.udemy.library.service.BookService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrada");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null.");
        }
        bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null.");
        }
        return bookRepository.save(book);
    }

    @Override
    public Page<Book> find(Integer page, Integer size, Book filter) {
        Example<Book> example = Example.of(filter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        Pageable pageable = PageRequest.of(page, size);
        return bookRepository.findAll(example, pageable);
    }

}
