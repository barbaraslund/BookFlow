package com.bookflow.dto;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        int totalCopies,
        int availableCopies,
        double averageRating) {
}
