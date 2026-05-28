package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.entity.Award;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AwardRepository extends JpaRepository<Award, Long> {

    List<Award> findByUserIdOrderByStartDateDesc(Long userId);

    @Modifying
    @Query("DELETE FROM Award a WHERE a.user.id = :userId")
    void deleteByUserId(Long userId);
}
