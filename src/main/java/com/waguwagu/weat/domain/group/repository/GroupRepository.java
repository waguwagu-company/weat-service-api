package com.waguwagu.weat.domain.group.repository;

import com.waguwagu.weat.domain.group.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String> {
}
