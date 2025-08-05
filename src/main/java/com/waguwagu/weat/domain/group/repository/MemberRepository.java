package com.waguwagu.weat.domain.group.repository;

import com.waguwagu.weat.domain.group.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
