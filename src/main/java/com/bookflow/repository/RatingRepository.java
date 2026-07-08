package com.bookflow.repository;

import com.bookflow.domain.Rating;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends ListCrudRepository<Rating, Long> {

    List<Rating> findByBookId(Long bookId);

    Optional<Rating> findByBookIdAndMemberId(Long bookId, Long memberId);
}
