package ru.alishev.springcourse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;
import ru.alishev.springcourse.repositories.BookRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {
    private static final String ATTR_YEAR = "year";
    private final BookRepository bookRepository;

    @Autowired
    public BooksService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll(boolean isSort) {
        return isSort
                ? bookRepository.findAll(Sort.by(ATTR_YEAR))
                : bookRepository.findAll();
    }

    public List<Book> findWithPagination(Integer page, Integer booksCount, boolean isSort) {
        return isSort
                ? bookRepository.findAll(PageRequest.of(page, booksCount, Sort.by(ATTR_YEAR))).getContent()
                : bookRepository.findAll(PageRequest.of(page, booksCount)).getContent();
    }

    public Book findOne(int id) {
        Optional<Book> book = bookRepository.findById(id);
        return book.orElse(null);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updatedBook) {
        updatedBook.setId(id);
        bookRepository.save(updatedBook);
    }

    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    public Person getBookOwner(int bookId) {
        return bookRepository.findById(bookId).map(Book::getPerson).orElse(null);
    }

    @Transactional
    public void assignBook(int bookId, Person owner) {
        bookRepository.findById(bookId).ifPresent(book -> {
            book.setPerson(owner);
            book.setExpireDate(new Date());
        });
    }

    @Transactional
    public void returnBook(int bookId) {
        bookRepository.findById(bookId).ifPresent(book -> {
            book.setPerson(null);
            book.setExpireDate(null);
        });
    }

    public List<Book> findBookByName(String bookName) {
        return bookRepository.findByNameStartingWith(bookName);
    }
}
