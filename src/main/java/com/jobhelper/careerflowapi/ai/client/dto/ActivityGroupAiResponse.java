package com.jobhelper.careerflowapi.ai.client.dto;

import java.util.List;

public record ActivityGroupAiResponse(List<ActivityGroup> groups) {

    public record ActivityGroup(String groupName, List<String> activities) {}
}
