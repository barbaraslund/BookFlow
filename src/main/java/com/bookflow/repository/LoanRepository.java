package com.bookflow.repository;

import com.bookflow.domain.Loan;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface LoanRepository extends ListCrudRepository<Loan, Long> {

    List<Loan> findByMemberIdAndReturnedAtIsNull(Long memberId);
}
