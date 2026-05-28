package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LicenseRepository extends JpaRepository<License, Long> {

    List<License> findByUserIdOrderByLicenseNameAsc(Long userId);

    @Modifying
    @Query("DELETE FROM License l WHERE l.user.id = :userId")
    void deleteByUserId(Long userId);
}
