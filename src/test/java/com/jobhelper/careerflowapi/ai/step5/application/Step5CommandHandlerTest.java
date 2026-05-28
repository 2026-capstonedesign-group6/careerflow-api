package com.jobhelper.careerflowapi.ai.step5.application;

import com.jobhelper.careerflowapi.ai.client.AiServerClient;
import com.jobhelper.careerflowapi.ai.client.dto.RecommendationAiResponse;
import com.jobhelper.careerflowapi.ai.step5.presentation.dto.request.RecommendRequest;
import com.jobhelper.careerflowapi.ai.step5.presentation.dto.response.RecommendResponse;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Step5CommandHandlerTest {

    @Mock private AiServerClient aiServerClient;
    @Mock private ExperienceRepository experienceRepository;

    @InjectMocks
    private Step5CommandHandler step5CommandHandler;

    @Test
    void recommend_경험을_조회하고_AI서버에_추천을_요청한다() {
        Experience exp = mockExperience(1L);
        given(experienceRepository.findAllByIdInAndUserId(eq(List.of(1L)), eq(1L))).willReturn(List.of(exp));
        given(aiServerClient.recommendActivities(any()))
                .willReturn(new RecommendationAiResponse(List.of(
                        new RecommendationAiResponse.RecommendedExperience(1L, 92)
                )));

        RecommendResponse result = step5CommandHandler.recommend(
                new RecommendRequest("지원 동기를 서술하시오.", List.of(1L)), 1L);

        verify(experienceRepository).findAllByIdInAndUserId(List.of(1L), 1L);
        verify(aiServerClient).recommendActivities(any());
        assertThat(result.recommendations()).hasSize(1);
        assertThat(result.recommendations().get(0).relevance()).isEqualTo(92);
    }

    @Test
    void recommend_AI응답에_경험_제목을_매핑하여_반환한다() {
        Experience exp = mockExperience(1L);
        given(experienceRepository.findAllByIdInAndUserId(any(), any())).willReturn(List.of(exp));

        given(aiServerClient.recommendActivities(any()))
                .willReturn(new RecommendationAiResponse(List.of(
                        new RecommendationAiResponse.RecommendedExperience(1L, 85)
                )));

        RecommendResponse result = step5CommandHandler.recommend(
                new RecommendRequest("문항", List.of(1L)), 1L);

        assertThat(result.recommendations().get(0).title()).isEqualTo("캡스톤 팀 리더");
    }

    private Experience mockExperience(Long id) {
        Experience exp = mock(Experience.class);
        given(exp.getId()).willReturn(id);
        given(exp.getTitle()).willReturn("캡스톤 팀 리더");
        given(exp.getSituation()).willReturn("팀 리더로서");
        given(exp.getTask()).willReturn("일정 관리");
        given(exp.getAction()).willReturn("매주 회의 진행");
        given(exp.getResult()).willReturn("A+ 달성");
        return exp;
    }
}
