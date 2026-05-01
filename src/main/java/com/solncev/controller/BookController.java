package com.solncev.controller;

import com.solncev.api.generated.api.BookApi;
import com.solncev.api.generated.dto.Book;
import com.solncev.api.generated.dto.CreateBookRequest;
import com.solncev.api.generated.dto.UpdateBookRequest;
import com.solncev.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController implements BookApi {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.findAll().stream()
                .map(BookController::toDto)
                .toList();
        return ResponseEntity.ok(books);
    }

    @Override
    public ResponseEntity<Book> getBookById(Long id) {
        return bookService.findById(id)
                .map(BookController::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Book> createBook(CreateBookRequest request) {
        com.solncev.entity.Book created = bookService.create(
                request.getTitle(),
                request.getAuthor(),
                request.getYear()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    @Override
    public ResponseEntity<Book> updateBook(Long id, UpdateBookRequest request) {
        return bookService.update(id, request.getTitle(), request.getAuthor(), request.getYear())
                .map(BookController::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Void> deleteBook(Long id) {
        return bookService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private static Book toDto(com.solncev.entity.Book entity) {
        return new Book(entity.getId(), entity.getTitle(), entity.getAuthor())
                .year(entity.getYear());
    }
}
