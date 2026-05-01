package com.solncev.service;

import com.solncev.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {
    List<Book> findAll();

    Optional<Book> findById(Long id);

    Book create(String title, String author, Integer year);

    Optional<Book> update(Long id, String title, String author, Integer year);

    boolean delete(Long id);
}
