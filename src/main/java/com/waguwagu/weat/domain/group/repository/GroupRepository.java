package com.waguwagu.weat.domain.group.repository;

import com.waguwagu.weat.domain.group.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupRepository extends JpaRepository<Group, String> {

    @Query(value = 
    """
    SELECT COUNT(m.*)
    FROM member m
    JOIN "group" g ON g.group_id = m.group_id
    WHERE g.is_single_member_group = true
    """, 
    nativeQuery = true)
    long countSingleMember();

    @Query(value = 
    """
    SELECT COUNT(m.*)
    FROM member m
    JOIN "group" g ON g.group_id = m.group_id
    WHERE g.is_single_member_group = false
    """, 
    nativeQuery = true)
    long countMultiMember();

}
