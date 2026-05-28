package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {

    List<Career> findByUserIdOrderByStartDateDesc(Long userId);

    void deleteByUserId(Long userId);
}
