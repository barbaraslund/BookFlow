package com.bookflow.service;

import com.bookflow.domain.Loan;
import com.bookflow.exception.AlreadyReturnedException;
import com.bookflow.exception.LoanNotFoundException;
import com.bookflow.exception.UnauthorizedLoanAccessException;
import com.bookflow.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;

    public LoanService(LoanRepository loanRepository, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    public List<Loan> getCurrentLoansForMember(Long memberId) {
        return loanRepository.findByMemberIdAndReturnedAtIsNull(memberId);
    }

    public Loan loanBook(Long bookId, Long memberId) {
        bookService.decrementAvailableCopies(bookId);

        Loan loan = new Loan(bookId, memberId, LocalDateTime.now());
        return loanRepository.save(loan);
    }

    public Loan returnBook(Long loanId, Long memberId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        if (!loan.getMemberId().equals(memberId)) {
            throw new UnauthorizedLoanAccessException(loanId, memberId);
        }

        if (loan.isReturned()) {
            throw new AlreadyReturnedException(loanId);
        }

        loan.markReturned(LocalDateTime.now());
        Loan savedLoan = loanRepository.save(loan);

        bookService.incrementAvailableCopies(loan.getBookId());

        return savedLoan;
    }
}
