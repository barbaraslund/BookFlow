package com.bookflow.web;

import com.bookflow.domain.Loan;
import com.bookflow.dto.LoanRequest;
import com.bookflow.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public Mono<List<Loan>> getCurrentLoans(@RequestHeader("X-Member-Id") Long memberId) {
        return Mono.fromCallable(() -> loanService.getCurrentLoansForMember(memberId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    // X-Member-Id simulates the logged-in user, per the assignment's allowance.
    // Given more time, I'd replace this with real authentication (e.g. a JWT
    // validated by a filter/gateway, with the member ID extracted from its claims
    // instead of trusted from a client-supplied header).
    @PostMapping
    public Mono<Loan> loanBook(@RequestHeader("X-Member-Id") Long memberId,
                                @Valid @RequestBody LoanRequest request) {
        return Mono.fromCallable(() -> loanService.loanBook(request.bookId(), memberId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/{loanId}/return")
    public Mono<Loan> returnBook(@RequestHeader("X-Member-Id") Long memberId,
                                  @PathVariable Long loanId) {
        return Mono.fromCallable(() -> loanService.returnBook(loanId, memberId))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
