package com.bookflow.repository;

import com.bookflow.domain.Member;
import org.springframework.data.repository.ListCrudRepository;

public interface MemberRepository extends ListCrudRepository<Member, Long> {
}
