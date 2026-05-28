package com.jobhelper.careerflowapi.resume.generator;

import com.jobhelper.careerflowapi.global.domain.ErrorCode;
import com.jobhelper.careerflowapi.global.exception.BusinessException;
import com.jobhelper.careerflowapi.resume.domain.entity.EssayItem;
import com.jobhelper.careerflowapi.resume.domain.entity.Resume;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class DocxResumeGenerator implements ResumeGenerator {

    @Override
    public byte[] generate(Resume resume) {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            writeTitle(doc, resume.getTitle());

            for (EssayItem essay : resume.getEssays()) {
                writeQuestion(doc, essay.getQuestion());
                writeContent(doc, essay.getContent() != null ? essay.getContent() : "");
            }

            doc.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.RESUME_GENERATE_FAILED);
        }
    }

    private void writeTitle(XWPFDocument doc, String title) {
        XWPFParagraph para = doc.createParagraph();
        para.setAlignment(ParagraphAlignment.CENTER);
        para.setSpacingAfter(300);
        XWPFRun run = para.createRun();
        run.setBold(true);
        run.setFontSize(18);
        run.setText(title);
    }

    private void writeQuestion(XWPFDocument doc, String question) {
        XWPFParagraph para = doc.createParagraph();
        para.setSpacingBefore(200);
        para.setSpacingAfter(100);
        XWPFRun run = para.createRun();
        run.setBold(true);
        run.setFontSize(12);
        run.setText("Q. " + question);
    }

    private void writeContent(XWPFDocument doc, String content) {
        XWPFParagraph para = doc.createParagraph();
        para.setSpacingAfter(200);
        XWPFRun run = para.createRun();
        run.setFontSize(10);
        run.setText(content);
    }
}
