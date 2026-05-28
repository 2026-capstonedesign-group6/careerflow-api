package com.jobhelper.careerflowapi.resume.infrastructure;

import com.jobhelper.careerflowapi.resume.domain.entity.Resume;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    List<Resume> findByUserIdAndIdLessThanOrderByIdDesc(Long userId, Long cursor, Pageable pageable);

    long countByUserId(Long userId);
}
