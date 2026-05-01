package com.solncev.service.impl;

import com.solncev.entity.Book;
import com.solncev.repository.BookRepository;
import com.solncev.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional
    public Book create(String title, String author, Integer year) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setYear(year);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Optional<Book> update(Long id, String title, String author, Integer year) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(title);
            book.setAuthor(author);
            book.setYear(year);
            return book;
        });
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!bookRepository.existsById(id)) {
            return false;
        }
        bookRepository.deleteById(id);
        return true;
    }
}
