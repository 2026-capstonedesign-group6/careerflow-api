package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TechStackRepository extends JpaRepository<TechStack, Long> {

    List<TechStack> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM TechStack ts WHERE ts.user.id = :userId")
    void deleteByUserId(Long userId);
}
