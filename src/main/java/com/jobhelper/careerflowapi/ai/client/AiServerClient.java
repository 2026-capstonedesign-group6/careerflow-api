package com.jobhelper.careerflowapi.ai.client;

import com.jobhelper.careerflowapi.ai.client.dto.ActivityGroupAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.ActivityGroupAiResponse;
import com.jobhelper.careerflowapi.ai.client.dto.EssayGenerationAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.EssayGenerationAiResponse;
import com.jobhelper.careerflowapi.ai.client.dto.RecommendationAiRequest;
import com.jobhelper.careerflowapi.ai.client.dto.RecommendationAiResponse;

public interface AiServerClient {

    ActivityGroupAiResponse groupActivities(ActivityGroupAiRequest request);

    RecommendationAiResponse recommendActivities(RecommendationAiRequest request);

    EssayGenerationAiResponse generateEssay(EssayGenerationAiRequest request);
}
