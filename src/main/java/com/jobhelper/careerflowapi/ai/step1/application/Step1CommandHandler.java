package com.jobhelper.careerflowapi.ai.step1.application;

import com.jobhelper.careerflowapi.ai.client.AiServerClient;
import com.jobhelper.careerflowapi.ai.client.dto.ActivityGroupAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.ActivityGroupAiResponse;
import com.jobhelper.careerflowapi.ai.step1.ocr.OcrPreprocessor;
import com.jobhelper.careerflowapi.ai.step1.presentation.dto.response.GroupingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Step1CommandHandler {

    private final AiServerClient aiServerClient;
    private final OcrPreprocessor ocrPreprocessor;

    public GroupingResponse group(String rawText, List<MultipartFile> files) {
        StringBuilder combined = new StringBuilder();
        if (rawText != null && !rawText.isBlank()) {
            combined.append(rawText);
        }
        if (files != null) {
            for (MultipartFile file : files) {
                String extracted = ocrPreprocessor.extract(file);
                if (!extracted.isBlank()) {
                    combined.append("\n").append(extracted);
                }
            }
        }
        ActivityGroupAiResponse aiResponse = aiServerClient.groupActivities(
                new ActivityGroupAiRequest(combined.toString()));
        List<GroupingResponse.ActivityGroup> groups = aiResponse.groups().stream()
                .map(g -> new GroupingResponse.ActivityGroup(g.groupName(), g.activities()))
                .toList();
        return new GroupingResponse(groups);
    }
}
