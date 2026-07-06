package com.bookflow.service;

import com.bookflow.domain.Rating;
import com.bookflow.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating rateBook(Long bookId, Long memberId, int score, String comment) {
        return ratingRepository.findByBookIdAndMemberId(bookId, memberId)
                .map(existing -> {
                    existing.update(score, comment);
                    return ratingRepository.save(existing);
                })
                .orElseGet(() -> ratingRepository.save(new Rating(bookId, memberId, score, comment)));
    }

    public double getAverageRating(Long bookId) {
        List<Rating> ratings = ratingRepository.findByBookId(bookId);
        return ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }
}
