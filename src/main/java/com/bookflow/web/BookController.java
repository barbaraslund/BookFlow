package com.bookflow.web;

import com.bookflow.domain.Book;
import com.bookflow.dto.BookResponse;
import com.bookflow.service.BookService;
import com.bookflow.service.RatingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;
    private final RatingService ratingService;

    public BookController(BookService bookService, RatingService ratingService) {
        this.bookService = bookService;
        this.ratingService = ratingService;
    }

    @GetMapping
    public Mono<List<BookResponse>> getAllBooks() {
        return Mono.fromCallable(() -> bookService.getAllBooks().stream()
                        .map(this::toBookResponse)
                        .toList())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/{bookId}")
    public Mono<BookResponse> getBook(@PathVariable Long bookId) {
        return Mono.fromCallable(() -> toBookResponse(bookService.getBookById(bookId)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private BookResponse toBookResponse(Book book) {
        double averageRating = ratingService.getAverageRating(book.getId());
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                averageRating);
    }
}
