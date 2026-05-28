package com.jobhelper.careerflowapi.user.presentation.dto.response;

import com.jobhelper.careerflowapi.user.domain.entity.LanguageCertificate;

import java.time.LocalDate;

public record LanguageCertificateResponse(
        Long id,
        String language,
        String examName,
        String score,
        LocalDate examDate
) {
    public static LanguageCertificateResponse from(LanguageCertificate entity) {
        return new LanguageCertificateResponse(
                entity.getId(),
                entity.getLanguage(),
                entity.getExamName(),
                entity.getScore(),
                entity.getExamDate()
        );
    }
}
