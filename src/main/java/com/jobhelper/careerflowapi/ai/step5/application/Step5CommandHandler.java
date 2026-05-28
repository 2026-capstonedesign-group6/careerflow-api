package com.jobhelper.careerflowapi.ai.step5.application;

import com.jobhelper.careerflowapi.ai.client.AiServerClient;
import com.jobhelper.careerflowapi.ai.client.dto.ExperienceItem;
import com.jobhelper.careerflowapi.ai.client.dto.RecommendationAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.RecommendationAiResponse;
import com.jobhelper.careerflowapi.ai.step5.presentation.dto.request.RecommendRequest;
import com.jobhelper.careerflowapi.ai.step5.presentation.dto.response.RecommendResponse;
import com.jobhelper.careerflowapi.experience.domain.entity.Experience;
import com.jobhelper.careerflowapi.experience.infrastructure.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Step5CommandHandler {

    private final AiServerClient aiServerClient;
    private final ExperienceRepository experienceRepository;

    @Transactional(readOnly = true)
    public RecommendResponse recommend(RecommendRequest request, Long userId) {
        List<Experience> experiences = experienceRepository.findAllByIdInAndUserId(request.experienceIds(), userId);
        List<ExperienceItem> items = experiences.stream()
                .map(e -> new ExperienceItem(e.getId(), e.getTitle(), e.getSituation(), e.getTask(), e.getAction(), e.getResult()))
                .toList();
        RecommendationAiResponse aiResponse = aiServerClient.recommendActivities(
                new RecommendationAiRequest(request.question(), items));
        Map<Long, String> titleMap = experiences.stream()
                .collect(Collectors.toMap(Experience::getId, Experience::getTitle));
        List<RecommendResponse.RecommendationItem> recommendations = aiResponse.recommendations().stream()
                .map(r -> new RecommendResponse.RecommendationItem(
                        r.experienceId(), titleMap.getOrDefault(r.experienceId(), ""), r.relevance()))
                .toList();
        return new RecommendResponse(recommendations);
    }
}
