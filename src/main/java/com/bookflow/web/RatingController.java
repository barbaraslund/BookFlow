package com.bookflow.web;

import com.bookflow.domain.Rating;
import com.bookflow.dto.RatingRequest;
import com.bookflow.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/api/books/{bookId}/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public Mono<Rating> rateBook(@RequestHeader("X-Member-Id") Long memberId,
                                  @PathVariable Long bookId,
                                  @Valid @RequestBody RatingRequest request) {
        return Mono.fromCallable(() -> ratingService.rateBook(bookId, memberId, request.score(), request.comment()))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
