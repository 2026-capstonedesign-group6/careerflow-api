package com.jobhelper.careerflowapi.ai.step1.presentation.dto.response;

import java.util.List;

public record GroupingResponse(List<ActivityGroup> groups) {

    public record ActivityGroup(String groupName, List<String> activities) {}
}
