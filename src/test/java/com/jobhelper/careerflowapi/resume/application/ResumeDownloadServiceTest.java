package com.jobhelper.careerflowapi.resume.application;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.resume.domain.entity.Resume;
import com.jobhelper.careerflowapi.resume.generator.DocxResumeGenerator;
import com.jobhelper.careerflowapi.resume.generator.PdfResumeGenerator;
import com.jobhelper.careerflowapi.resume.infrastructure.ResumeRepository;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResumeDownloadServiceTest {

    @Mock private ResumeRepository resumeRepository;
    @Mock private PdfResumeGenerator pdfResumeGenerator;
    @Mock private DocxResumeGenerator docxResumeGenerator;

    @InjectMocks
    private ResumeDownloadService resumeDownloadService;

    @Test
    void download_이력서가_없으면_RESUME_NOT_FOUND_예외를_던진다() {
        given(resumeRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> resumeDownloadService.download(1L, 99L, "pdf"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESUME_NOT_FOUND);
    }

    @Test
    void download_다른_유저의_이력서이면_RESUME_ACCESS_DENIED_예외를_던진다() {
        Resume resume = mockResume(1L, 2L);
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));

        assertThatThrownBy(() -> resumeDownloadService.download(99L, 1L, "pdf"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESUME_ACCESS_DENIED);
    }

    @Test
    void download_pdf_형식이면_PdfGenerator를_호출한다() {
        Resume resume = mockResume(1L, 1L);
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));
        given(pdfResumeGenerator.generate(resume)).willReturn(new byte[]{1, 2, 3});

        byte[] result = resumeDownloadService.download(1L, 1L, "pdf");

        verify(pdfResumeGenerator).generate(resume);
        assertThat(result).isNotEmpty();
    }

    @Test
    void download_docx_형식이면_DocxGenerator를_호출한다() {
        Resume resume = mockResume(1L, 1L);
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));
        given(docxResumeGenerator.generate(resume)).willReturn(new byte[]{1, 2, 3});

        byte[] result = resumeDownloadService.download(1L, 1L, "docx");

        verify(docxResumeGenerator).generate(resume);
        assertThat(result).isNotEmpty();
    }

    @Test
    void download_지원하지_않는_형식이면_INVALID_INPUT_VALUE_예외를_던진다() {
        Resume resume = mockResume(1L, 1L);
        given(resumeRepository.findById(1L)).willReturn(Optional.of(resume));

        assertThatThrownBy(() -> resumeDownloadService.download(1L, 1L, "txt"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INPUT_VALUE);
    }

    private Resume mockResume(Long resumeId, Long ownerId) {
        User owner = mock(User.class);
        given(owner.getId()).willReturn(ownerId);
        Resume resume = mock(Resume.class);
        given(resume.getId()).willReturn(resumeId);
        given(resume.getUser()).willReturn(owner);
        return resume;
    }
}
