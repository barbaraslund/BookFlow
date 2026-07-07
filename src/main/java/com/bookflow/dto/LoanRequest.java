package com.bookflow.dto;

import jakarta.validation.constraints.NotNull;

public record LoanRequest(@NotNull Long bookId) {
}
