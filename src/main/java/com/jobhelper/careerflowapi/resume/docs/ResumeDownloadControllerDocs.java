package com.jobhelper.careerflowapi.resume.docs;

import com.jobhelper.careerflowapi.global.docs.CommonReadErrorDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Resume", description = "이력서/자소서 API")
public interface ResumeDownloadControllerDocs {

    @Operation(summary = "이력서 다운로드 (format: pdf | docx)")
    @CommonReadErrorDocs
    ResponseEntity<Resource> download(@PathVariable Long id, @RequestParam String format);
}
