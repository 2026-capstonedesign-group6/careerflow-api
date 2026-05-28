package com.jobhelper.careerflowapi.ai.step1.docs;

import com.jobhelper.careerflowapi.ai.step1.presentation.dto.response.GroupingResponse;
import com.jobhelper.careerflowapi.global.docs.CommonCreateErrorDocs;
import com.jobhelper.careerflowapi.global.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Step1", description = "활동 그룹화 API")
public interface Step1ControllerDocs {

    @Operation(summary = "활동 그룹화 (텍스트/PDF → 유사 활동 클러스터)")
    @CommonCreateErrorDocs
    ResponseEntity<CommonResponse<GroupingResponse>> group(String rawText, List<MultipartFile> files);
}
