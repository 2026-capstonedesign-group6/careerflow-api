package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByUserIdOrderByStartDateDesc(Long userId);

    @Modifying
    @Query("DELETE FROM Training t WHERE t.user.id = :userId")
    void deleteByUserId(Long userId);
}
