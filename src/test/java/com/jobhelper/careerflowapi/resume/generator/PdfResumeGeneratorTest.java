package com.jobhelper.careerflowapi.resume.generator;

import com.jobhelper.careerflowapi.resume.domain.entity.EssayItem;
import com.jobhelper.careerflowapi.resume.domain.entity.Resume;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class PdfResumeGeneratorTest {

    private final PdfResumeGenerator generator = new PdfResumeGenerator();

    @Test
    void generate_이력서_바이트를_반환한다() {
        Resume resume = mockResume("네이버 공채 지원", List.of(
                mockEssay("지원 동기를 서술하시오.", "저는 서비스 개발에 열정을 가지고 있습니다.")
        ));

        byte[] result = generator.generate(resume);

        assertThat(result).isNotEmpty();
    }

    @Test
    void generate_에세이가_없어도_바이트를_반환한다() {
        Resume resume = mockResume("빈 이력서", List.of());

        byte[] result = generator.generate(resume);

        assertThat(result).isNotEmpty();
    }

    @Test
    void generate_여러_에세이가_있어도_바이트를_반환한다() {
        Resume resume = mockResume("카카오 공채", List.of(
                mockEssay("지원 동기", "카카오의 비전에 공감합니다."),
                mockEssay("본인의 강점", "문제 해결 능력이 뛰어납니다."),
                mockEssay("입사 후 포부", "핵심 엔지니어로 성장하겠습니다.")
        ));

        byte[] result = generator.generate(resume);

        assertThat(result).isNotEmpty();
    }

    private Resume mockResume(String title, List<EssayItem> essays) {
        Resume resume = mock(Resume.class);
        given(resume.getTitle()).willReturn(title);
        given(resume.getEssays()).willReturn(essays);
        return resume;
    }

    private EssayItem mockEssay(String question, String content) {
        EssayItem essay = mock(EssayItem.class);
        given(essay.getQuestion()).willReturn(question);
        given(essay.getContent()).willReturn(content);
        return essay;
    }
}
