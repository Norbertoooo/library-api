package com.udemy.library.web.rest;

import com.udemy.library.domain.Book;
import com.udemy.library.service.BookService;
import com.udemy.library.web.rest.dto.BookDTO;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@Log4j2
public class BookResource {

    private final BookService bookService;
    private final ModelMapper modelMapper;

    public BookResource(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO) {
        log.info("Request to save book: {}", bookDTO);
        Book book = bookService.save(modelMapper.map(bookDTO, Book.class));
        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping("/{id}")
    public BookDTO getById(@PathVariable Long id) {
        log.info("Request to find book by id: {}", id);
        return bookService.findById(id)
                .map(book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping()
    public Page<BookDTO> find(Integer page, Integer size, BookDTO bookDTO) {
        log.info("Request to find book:");
        Page<Book> result = bookService.find(page, size, modelMapper.map(bookDTO, Book.class));
        List<BookDTO> bookDTOS = result.getContent().stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDTO>(bookDTOS, PageRequest.of(page, size), result.getTotalElements());
    }

    @PutMapping("/{id}")
    public BookDTO update(@RequestBody @Valid BookDTO bookDTO, @PathVariable Long id) {
        log.info("Request to update book: {}", bookDTO);
        return bookService.findById(id)
                .map(book -> {
                    book.setAuthor(bookDTO.getAuthor());
                    book.setTitle(bookDTO.getTitle());
                    book.setIsbn(bookDTO.getIsbn());
                    return modelMapper.map(bookService.update(book), BookDTO.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        log.info("Request to delete book by id: {}", id);
        Book book = bookService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

}
