package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.entity.OtherActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OtherActivityRepository extends JpaRepository<OtherActivity, Long> {

    List<OtherActivity> findByUserIdOrderByStartDateDesc(Long userId);

    @Modifying
    @Query("DELETE FROM OtherActivity oa WHERE oa.user.id = :userId")
    void deleteByUserId(Long userId);
}
