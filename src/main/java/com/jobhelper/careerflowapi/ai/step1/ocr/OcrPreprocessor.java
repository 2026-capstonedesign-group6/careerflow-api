package com.jobhelper.careerflowapi.ai.step1.ocr;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class OcrPreprocessor {

    public String extract(MultipartFile file) {
        if (file == null || file.isEmpty()) return "";
        String filename = file.getOriginalFilename();
        if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
            return extractFromPdf(file);
        }
        return "";
    }

    private String extractFromPdf(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.AI_SERVER_ERROR);
        }
    }
}
