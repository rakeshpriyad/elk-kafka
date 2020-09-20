package com.d2d.elastic.service.impl;

import com.d2d.elastic.model.Book;
import com.d2d.elastic.repository.BookRepository;
import com.d2d.elastic.service.BookService;
import com.d2d.elastic.service.exception.BookNotFoundException;
import com.d2d.elastic.service.exception.DuplicateIsbnException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultBookService implements BookService {

    private final BookRepository bookRepository;

    @Autowired
    private final ElasticsearchRestTemplate elasticsearchTemplate;

    public DefaultBookService(BookRepository bookRepository, ElasticsearchRestTemplate elasticsearchTemplate) {
        this.bookRepository = bookRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Optional<Book> getByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        bookRepository.findAll().forEach(book -> books.add(book));
        return books;
    }

    @Override
    public List<Book> findByAuthor(String authorName) {
        return bookRepository.findByAuthorName(authorName);
    }

    @Override
    public List<Book> findByTitleAndAuthor(String title, String author) {
        BoolQueryBuilder criteria = QueryBuilders.boolQuery();
        List<MatchQueryBuilder> l = new ArrayList();
        l.add(QueryBuilders.matchQuery("authorName", author));
        l.add(QueryBuilders.matchQuery("title", title));
        criteria.must().addAll(l);
        NativeSearchQuery x = new NativeSearchQueryBuilder().withQuery(criteria).build();
        return elasticsearchTemplate.queryForList(x, Book.class, IndexCoordinates.of("books"));
    }

    @Override
    public Book create(Book book) throws DuplicateIsbnException {

        if (!getByIsbn(book.getIsbn()).isPresent()) {
            return bookRepository.save(book);
        }
        throw new DuplicateIsbnException(String.format("The provided ISBN: %s already exists. Use update instead!", book.getIsbn()));
    }

    @Override
    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book update(String id, Book book) throws BookNotFoundException {
        Book oldBook = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("There is not book associated with the given id"));
        oldBook.setIsbn(book.getIsbn());
        oldBook.setAuthorName(book.getAuthorName());
        oldBook.setPublicationYear(book.getPublicationYear());
        oldBook.setTitle(book.getTitle());
        return bookRepository.save(oldBook);
    }
}
