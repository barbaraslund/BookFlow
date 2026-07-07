package com.bookflow.service;

import com.bookflow.domain.Loan;
import com.bookflow.exception.AlreadyReturnedException;
import com.bookflow.exception.UnauthorizedLoanAccessException;
import com.bookflow.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookService bookService;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loanRepository, bookService);
    }

    @Test
    void returnBook_whenCallerIsNotTheLoanOwner_throwsUnauthorizedLoanAccessException() {
        Loan loan = new Loan(5L, 1L, LocalDateTime.now());
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnBook(10L, 2L))
                .isInstanceOf(UnauthorizedLoanAccessException.class);
    }

    @Test
    void returnBook_whenAlreadyReturned_throwsAlreadyReturnedException() {
        Loan loan = new Loan(5L, 1L, LocalDateTime.now());
        loan.markReturned(LocalDateTime.now());
        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.returnBook(10L, 1L))
                .isInstanceOf(AlreadyReturnedException.class);
    }

    @Test
    void loanBook_decrementsInventoryBeforeSavingTheLoan() {
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.loanBook(5L, 1L);

        verify(bookService).decrementAvailableCopies(5L);
        assertThat(result.getBookId()).isEqualTo(5L);
        assertThat(result.getMemberId()).isEqualTo(1L);
        assertThat(result.isReturned()).isFalse();
    }
}
