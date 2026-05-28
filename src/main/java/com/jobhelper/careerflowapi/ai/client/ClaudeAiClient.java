package com.jobhelper.careerflowapi.ai.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobhelper.careerflowapi.ai.client.dto.ActivityGroupAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.ActivityGroupAiResponse;
import com.jobhelper.careerflowapi.ai.client.dto.ClaudeMessage;
import com.jobhelper.careerflowapi.ai.client.dto.ClaudeRequest;
import com.jobhelper.careerflowapi.ai.client.dto.ClaudeResponse;
import com.jobhelper.careerflowapi.ai.client.dto.EssayGenerationAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.EssayGenerationAiResponse;
import com.jobhelper.careerflowapi.ai.client.dto.ExperienceItem;
import com.jobhelper.careerflowapi.ai.client.dto.RecommendationAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.RecommendationAiResponse;
import com.jobhelper.careerflowapi.ai.config.ClaudeProperties;
import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ClaudeAiClient implements AiServerClient {

    private static final String MODEL = "claude-sonnet-4-6";
    private static final String SYSTEM_PROMPT =
            "당신은 한국 취업 지원을 돕는 AI 어시스턴트입니다. 반드시 유효한 JSON만 응답하고 다른 텍스트는 절대 포함하지 마세요.";

    @Qualifier("claudeRestClient")
    private final RestClient claudeRestClient;
    private final ObjectMapper objectMapper;
    private final ClaudeProperties claudeProperties;

    @Override
    public ActivityGroupAiResponse groupActivities(ActivityGroupAiRequest request) {
        String prompt = buildGroupingPrompt(request.text());
        String json = callClaude(prompt, 1024);
        return parseJson(json, ActivityGroupAiResponse.class);
    }

    @Override
    public RecommendationAiResponse recommendActivities(RecommendationAiRequest request) {
        String prompt = buildRecommendationPrompt(request.question(), request.experiences());
        String json = callClaude(prompt, 512);
        return parseJson(json, RecommendationAiResponse.class);
    }

    @Override
    public EssayGenerationAiResponse generateEssay(EssayGenerationAiRequest request) {
        String prompt = buildEssayPrompt(
                request.question(), request.experiences(), request.targetLength(), request.tone());
        String json = callClaude(prompt, 2048);
        return parseJson(json, EssayGenerationAiResponse.class);
    }

    private String callClaude(String userPrompt, int maxTokens) {
        ClaudeRequest request = new ClaudeRequest(
                MODEL,
                maxTokens,
                List.of(new ClaudeMessage("user", userPrompt)),
                SYSTEM_PROMPT
        );
        try {
            ClaudeResponse response = claudeRestClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", claudeProperties.apiKey())
                    .header("anthropic-version", "2023-06-01")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(), (req, res) -> {
                        String body = new String(res.getBody().readAllBytes());
                        log.error("[Claude API] {} {} — body: {}", res.getStatusCode(), res.getStatusText(), body);
                        throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
                    })
                    .body(ClaudeResponse.class);
            if (response == null || response.textContent().isBlank()) {
                throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
            }
            return response.textContent();
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
        }
    }

    private <T> T parseJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
        }
    }

    private String buildGroupingPrompt(String text) {
        return """
                다음 텍스트의 활동과 경험들을 유사한 항목끼리 그룹으로 묶어주세요.
                반드시 아래 JSON 형식으로만 응답하세요:
                {"groups":[{"groupName":"그룹명","activities":["활동1","활동2"]}]}

                텍스트:
                """ + text;
    }

    private String buildRecommendationPrompt(String question, List<ExperienceItem> experiences) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음 자소서 문항에 가장 적합한 경험들을 추천하고 관련도(0~100)를 평가해주세요.\n");
        sb.append("반드시 아래 JSON 형식으로만 응답하세요:\n");
        sb.append("{\"recommendations\":[{\"experienceId\":1,\"relevance\":92}]}\n\n");
        sb.append("자소서 문항: ").append(question).append("\n\n");
        sb.append("경험 목록:\n");
        for (ExperienceItem exp : experiences) {
            sb.append("- ID: ").append(exp.id()).append(", 제목: ").append(exp.title()).append("\n");
            if (exp.situation() != null) sb.append("  상황(S): ").append(exp.situation()).append("\n");
            if (exp.task() != null) sb.append("  과제(T): ").append(exp.task()).append("\n");
            if (exp.action() != null) sb.append("  행동(A): ").append(exp.action()).append("\n");
            if (exp.result() != null) sb.append("  결과(R): ").append(exp.result()).append("\n");
        }
        return sb.toString();
    }

    private String buildEssayPrompt(String question, List<ExperienceItem> experiences,
                                    int targetLength, String tone) {
        String toneKorean = "FORMAL".equals(tone) ? "격식체(존댓말)" : "구어체";
        StringBuilder sb = new StringBuilder();
        sb.append("다음 자소서 문항과 경험을 바탕으로 ").append(targetLength)
                .append("자 내외의 자소서 본문을 ").append(toneKorean).append("로 작성해주세요.\n");
        sb.append("반드시 아래 JSON 형식으로만 응답하세요:\n");
        sb.append("{\"essay\":\"생성된 자소서 본문\",\"wordCount\":500}\n\n");
        sb.append("자소서 문항: ").append(question).append("\n\n");
        sb.append("경험 목록:\n");
        for (ExperienceItem exp : experiences) {
            sb.append("- 제목: ").append(exp.title()).append("\n");
            if (exp.situation() != null) sb.append("  상황(S): ").append(exp.situation()).append("\n");
            if (exp.task() != null) sb.append("  과제(T): ").append(exp.task()).append("\n");
            if (exp.action() != null) sb.append("  행동(A): ").append(exp.action()).append("\n");
            if (exp.result() != null) sb.append("  결과(R): ").append(exp.result()).append("\n");
        }
        return sb.toString();
    }
}
