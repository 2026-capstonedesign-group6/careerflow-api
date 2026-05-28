package com.jobhelper.careerflowapi.resume.generator;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.resume.domain.entity.EssayItem;
import com.jobhelper.careerflowapi.resume.domain.entity.Resume;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfResumeGenerator implements ResumeGenerator {

    private static final float MARGIN = 50f;
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float LINE_HEIGHT = 14f;
    private static final int MAX_LINE_CHARS = 90;

    @Override
    public byte[] generate(Resume resume) {
        try (PDDocument document = new PDDocument()) {
            PDFont boldFont = loadFont(document, true);
            PDFont normalFont = loadFont(document, false);
            boolean latinOnly = boldFont instanceof PDType1Font;

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float y = PAGE_HEIGHT - MARGIN;

                y = writeLine(cs, boldFont, 16f, MARGIN, y, filter(resume.getTitle(), latinOnly));
                y -= 8f;

                for (EssayItem essay : resume.getEssays()) {
                    if (y < MARGIN + 60) break;
                    y = writeLine(cs, boldFont, 11f, MARGIN, y,
                            "Q. " + truncate(filter(essay.getQuestion(), latinOnly), MAX_LINE_CHARS));
                    y -= 2f;
                    if (essay.getContent() != null && !essay.getContent().isBlank()) {
                        y = writeLine(cs, normalFont, 10f, MARGIN, y,
                                truncate(filter(essay.getContent(), latinOnly), MAX_LINE_CHARS * 2));
                    }
                    y -= 10f;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.RESUME_GENERATE_FAILED);
        }
    }

    private float writeLine(PDPageContentStream cs, PDFont font, float size, float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - size - LINE_HEIGHT;
    }

    private PDFont loadFont(PDDocument document, boolean bold) throws IOException {
        String path = bold ? "fonts/NanumGothicBold.ttf" : "fonts/NanumGothic.ttf";
        ClassPathResource resource = new ClassPathResource(path);
        if (resource.exists()) {
            return PDType0Font.load(document, resource.getInputStream());
        }
        return new PDType1Font(bold ? Standard14Fonts.FontName.HELVETICA_BOLD : Standard14Fonts.FontName.HELVETICA);
    }

    // PDType1Font은 Latin-1만 지원 — 폴백 폰트 사용 시 비 Latin 문자 제거
    private String filter(String input, boolean latinOnly) {
        if (input == null) return "";
        if (!latinOnly) return input;
        return input.chars()
                .filter(c -> c <= 0xFF)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String truncate(String text, int maxLen) {
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
