package com.jobhelper.careerflowapi.ai.step6.application;

import com.jobhelper.careerflowapi.ai.client.AiServerClient;
import com.jobhelper.careerflowapi.ai.client.dto.EssayGenerationAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.EssayGenerationAiResponse;
import com.jobhelper.careerflowapi.ai.client.dto.ExperienceItem;
import com.jobhelper.careerflowapi.ai.step6.presentation.dto.request.EssayGenerateRequest;
import com.jobhelper.careerflowapi.ai.step6.presentation.dto.response.EssayGenerateResponse;
import com.jobhelper.careerflowapi.experience.domain.entity.Experience;
import com.jobhelper.careerflowapi.experience.infrastructure.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Step6CommandHandler {

    private final AiServerClient aiServerClient;
    private final ExperienceRepository experienceRepository;

    @Transactional(readOnly = true)
    public EssayGenerateResponse generateEssay(EssayGenerateRequest request, Long userId) {
        List<Experience> experiences = experienceRepository.findAllByIdInAndUserId(request.experienceIds(), userId);
        List<ExperienceItem> items = experiences.stream()
                .map(e -> new ExperienceItem(e.getId(), e.getTitle(), e.getSituation(), e.getTask(), e.getAction(), e.getResult()))
                .toList();
        EssayGenerationAiResponse aiResponse = aiServerClient.generateEssay(
                new EssayGenerationAiRequest(request.question(), items, request.targetLength(), request.tone().name()));
        return new EssayGenerateResponse(aiResponse.essay(), aiResponse.wordCount());
    }
}
