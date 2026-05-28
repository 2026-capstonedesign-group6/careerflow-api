package com.jobhelper.careerflowapi.ai.step6.application;

import com.jobhelper.careerflowapi.ai.client.AiServerClient;
import com.jobhelper.careerflowapi.ai.client.dto.EssayGenerationAiResponse;
import com.jobhelper.careerflowapi.ai.step6.presentation.dto.request.EssayGenerateRequest;
import com.jobhelper.careerflowapi.ai.step6.presentation.dto.request.WritingTone;
import com.jobhelper.careerflowapi.ai.step6.presentation.dto.response.EssayGenerateResponse;
import com.jobhelper.careerflowapi.experience.domain.entity.Experience;
import com.jobhelper.careerflowapi.experience.infrastructure.ExperienceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Step6CommandHandlerTest {

    @Mock private AiServerClient aiServerClient;
    @Mock private ExperienceRepository experienceRepository;

    @InjectMocks
    private Step6CommandHandler step6CommandHandler;

    @Test
    void generateEssay_경험을_조회하고_AI서버에_자소서_생성을_요청한다() {
        Experience exp = mockExperience(1L);
        given(experienceRepository.findAllByIdInAndUserId(any(), any())).willReturn(List.of(exp));
        given(aiServerClient.generateEssay(any()))
                .willReturn(new EssayGenerationAiResponse("저는 2024년...", 487));

        EssayGenerateResponse result = step6CommandHandler.generateEssay(generateRequest(), 1L);

        verify(experienceRepository).findAllByIdInAndUserId(any(), any());
        verify(aiServerClient).generateEssay(any());
        assertThat(result.essay()).isEqualTo("저는 2024년...");
        assertThat(result.wordCount()).isEqualTo(487);
    }

    @Test
    void generateEssay_tone을_대문자_문자열로_AI서버에_전달한다() {
        Experience exp = mockExperience(1L);
        given(experienceRepository.findAllByIdInAndUserId(any(), any())).willReturn(List.of(exp));
        given(aiServerClient.generateEssay(any())).willReturn(new EssayGenerationAiResponse("내용", 100));

        step6CommandHandler.generateEssay(generateRequest(), 1L);

        verify(aiServerClient).generateEssay(argThat(req -> "FORMAL".equals(req.tone())));
    }

    @Test
    void generateEssay_경험_항목이_AI요청에_올바르게_매핑된다() {
        Experience exp = mockExperience(1L);
        given(experienceRepository.findAllByIdInAndUserId(any(), any())).willReturn(List.of(exp));
        given(aiServerClient.generateEssay(any())).willReturn(new EssayGenerationAiResponse("내용", 100));

        step6CommandHandler.generateEssay(generateRequest(), 1L);

        verify(aiServerClient).generateEssay(argThat(req ->
                req.experiences().size() == 1 &&
                req.experiences().get(0).title().equals("캡스톤 팀 리더")
        ));
    }

    private Experience mockExperience(Long id) {
        Experience exp = mock(Experience.class);
        given(exp.getId()).willReturn(id);
        given(exp.getTitle()).willReturn("캡스톤 팀 리더");
        given(exp.getSituation()).willReturn("상황");
        given(exp.getTask()).willReturn("과제");
        given(exp.getAction()).willReturn("행동");
        given(exp.getResult()).willReturn("결과");
        return exp;
    }

    private EssayGenerateRequest generateRequest() {
        return new EssayGenerateRequest("지원 동기를 서술하시오.", List.of(1L), 500, WritingTone.FORMAL);
    }
}
