package com.bookflow.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("loans")
public class Loan {
    @Id
    private Long id;

    private Long bookId;
    private Long memberId;
    private LocalDateTime loanedAt;
    private LocalDateTime returnedAt;

    public Loan(Long bookId, Long memberId, LocalDateTime loanedAt){
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanedAt = loanedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public LocalDateTime getLoanedAt() {
        return loanedAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public boolean isReturned(){
        return returnedAt != null;
    }

    public void markReturned(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }

}
