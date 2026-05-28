package com.jobhelper.careerflowapi.experience.infrastructure;

import com.jobhelper.careerflowapi.experience.domain.entity.Experience;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    List<Experience> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    List<Experience> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long cursor, Pageable pageable);

    List<Experience> findAllByIdInAndUserId(List<Long> ids, Long userId);

    long countByUserId(Long userId);
}
