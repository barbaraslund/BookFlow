package com.bookflow.exception;

public class UnauthorizedLoanAccessException extends RuntimeException {

    public UnauthorizedLoanAccessException(Long loanId, Long memberId) {
        super("Member " + memberId + " is not authorized to access loan " + loanId);
    }
}
