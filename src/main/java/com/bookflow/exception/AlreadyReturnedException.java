package com.bookflow.exception;

public class AlreadyReturnedException extends RuntimeException {

    public AlreadyReturnedException(Long loanId) {
        super("Loan already returned: id=" + loanId);
    }
}
