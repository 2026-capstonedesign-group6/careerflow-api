package com.jobhelper.careerflowapi.resume.application;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.resume.domain.entity.Resume;
import com.jobhelper.careerflowapi.resume.generator.DocxResumeGenerator;
import com.jobhelper.careerflowapi.resume.generator.PdfResumeGenerator;
import com.jobhelper.careerflowapi.resume.infrastructure.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeDownloadService {

    private final ResumeRepository resumeRepository;
    private final PdfResumeGenerator pdfResumeGenerator;
    private final DocxResumeGenerator docxResumeGenerator;

    @Transactional(readOnly = true)
    public byte[] download(Long userId, Long resumeId, String format) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND));

        if (!resume.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.RESUME_ACCESS_DENIED);
        }

        return switch (format.toLowerCase()) {
            case "pdf"  -> pdfResumeGenerator.generate(resume);
            case "docx" -> docxResumeGenerator.generate(resume);
            default     -> throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        };
    }
}
