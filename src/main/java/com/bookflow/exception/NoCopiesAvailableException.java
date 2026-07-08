package com.bookflow.exception;

public class NoCopiesAvailableException extends RuntimeException {

    public NoCopiesAvailableException(Long bookId) {
        super("No copies available for book: id=" + bookId);
    }
}
