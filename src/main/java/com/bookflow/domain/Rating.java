package com.bookflow.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("RATINGS")
public class Rating {
    @Id
    private Long id;

    private Long bookId;
    private Long memberId;
    private int score;
    private String comment;

    public Rating(Long bookId, Long memberId, int score, String comment){
        this.bookId = bookId;
        this.memberId = memberId;
        this.score = score;
        this.comment = comment;
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



    public int getScore() {
        return score;
    }



    public String getComment() {
        return comment;
    }



    public void update(int score, String comment) {
        this.score = score;
        this.comment = comment;
    }
    
}
