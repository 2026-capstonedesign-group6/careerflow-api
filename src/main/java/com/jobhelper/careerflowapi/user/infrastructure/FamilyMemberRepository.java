package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    List<FamilyMember> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM FamilyMember fm WHERE fm.user.id = :userId")
    void deleteByUserId(Long userId);
}
