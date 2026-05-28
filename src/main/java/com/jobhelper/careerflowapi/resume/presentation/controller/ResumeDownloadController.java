package com.jobhelper.careerflowapi.resume.presentation.controller;

import com.jobhelper.careerflowapi.resume.application.ResumeDownloadService;
import com.jobhelper.careerflowapi.resume.docs.ResumeDownloadControllerDocs;
import com.jobhelper.careerflowapi.global.security.SecurityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeDownloadController implements ResumeDownloadControllerDocs {

    private final ResumeDownloadService resumeDownloadService;
    private final SecurityFacade securityFacade;

    @Override
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @PathVariable Long id,
            @RequestParam String format
    ) {
        Long userId = securityFacade.getCurrentUser().userId();
        byte[] bytes = resumeDownloadService.download(userId, id, format);

        String contentType = format.equalsIgnoreCase("pdf")
                ? "application/pdf"
                : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"resume." + format.toLowerCase() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(new ByteArrayResource(bytes));
    }
}
