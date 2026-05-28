package com.jobhelper.careerflowapi.ai.step1.application;

import com.jobhelper.careerflowapi.ai.client.AiServerClient;
import com.jobhelper.careerflowapi.ai.client.dto.ActivityGroupAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.ActivityGroupAiResponse;
import com.jobhelper.careerflowapi.ai.step1.ocr.OcrPreprocessor;
import com.jobhelper.careerflowapi.ai.step1.presentation.dto.response.GroupingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class Step1CommandHandlerTest {

    @Mock private AiServerClient aiServerClient;
    @Mock private OcrPreprocessor ocrPreprocessor;

    @InjectMocks
    private Step1CommandHandler step1CommandHandler;

    @Test
    void group_rawText만_있을때_AI서버에_텍스트를_전달한다() {
        given(aiServerClient.groupActivities(any())).willReturn(emptyAiResponse());

        step1CommandHandler.group("활동 내용입니다", null);

        ArgumentCaptor<ActivityGroupAiRequest> captor = ArgumentCaptor.forClass(ActivityGroupAiRequest.class);
        verify(aiServerClient).groupActivities(captor.capture());
        assertThat(captor.getValue().text()).contains("활동 내용입니다");
    }

    @Test
    void group_파일이_있을때_OCR_추출_후_AI서버에_전달한다() {
        MultipartFile file = mock(MultipartFile.class);
        given(ocrPreprocessor.extract(file)).willReturn("OCR 추출 텍스트");
        given(aiServerClient.groupActivities(any())).willReturn(emptyAiResponse());

        step1CommandHandler.group(null, List.of(file));

        ArgumentCaptor<ActivityGroupAiRequest> captor = ArgumentCaptor.forClass(ActivityGroupAiRequest.class);
        verify(aiServerClient).groupActivities(captor.capture());
        assertThat(captor.getValue().text()).contains("OCR 추출 텍스트");
    }

    @Test
    void group_AI응답을_GroupingResponse로_변환하여_반환한다() {
        ActivityGroupAiResponse aiResponse = new ActivityGroupAiResponse(List.of(
                new ActivityGroupAiResponse.ActivityGroup("리더십", List.of("팀장 경험", "동아리 회장"))
        ));
        given(aiServerClient.groupActivities(any())).willReturn(aiResponse);

        GroupingResponse result = step1CommandHandler.group("텍스트", null);

        assertThat(result.groups()).hasSize(1);
        assertThat(result.groups().get(0).groupName()).isEqualTo("리더십");
        assertThat(result.groups().get(0).activities()).containsExactly("팀장 경험", "동아리 회장");
    }

    private ActivityGroupAiResponse emptyAiResponse() {
        return new ActivityGroupAiResponse(List.of());
    }
}
