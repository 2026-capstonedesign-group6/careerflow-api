package com.jobhelper.careerflowapi.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record LanguageCertificateRequest(
        @Schema(example = "영어") String language,
        @Schema(example = "TOEIC") String examName,
        @Schema(example = "950") String score,
        @Schema(example = "2024-05-01") LocalDate examDate
) {}
