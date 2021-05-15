package com.udemy.library.web.rest;

import com.udemy.library.domain.Book;
import com.udemy.library.exception.BusinessException;
import com.udemy.library.service.BookService;
import com.udemy.library.web.rest.dto.BookDTO;
import com.udemy.library.web.rest.errors.ApiErrors;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

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
                .map( book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        log.info("Request to delete book by id: {}", id);
        Book book = bookService.findById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);
    }
}
