package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.entity.LanguageCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LanguageCertificateRepository extends JpaRepository<LanguageCertificate, Long> {

    List<LanguageCertificate> findByUserIdOrderByExamDateDesc(Long userId);

    @Modifying
    @Query("DELETE FROM LanguageCertificate lc WHERE lc.user.id = :userId")
    void deleteByUserId(Long userId);
}
