package com.example.aijobhelper.service;

import com.example.aijobhelper.exception.BusinessException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * PDF 解析服务 —— 使用 PDFBox 提取文本内容
 */
@Service
public class PdfService {

    private static final Logger log = LoggerFactory.getLogger(PdfService.class);

    /**
     * 从上传的 PDF 文件中提取全部文本
     *
     * @param file MultipartFile（PDF 格式）
     * @return 提取出的纯文本
     */
    public String extractText(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "上传文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new BusinessException(400, "仅支持 PDF 格式文件");
        }

        log.info("开始解析 PDF: {} ({} bytes)", originalFilename, file.getSize());

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                throw new BusinessException(400, "PDF 文件中未能提取到文本内容，可能是扫描件或图片型 PDF");
            }

            log.info("PDF 解析完成: {}页, {} 字符", document.getNumberOfPages(), text.length());
            return text;

        } catch (IOException e) {
            log.error("PDF 解析失败", e);
            throw new BusinessException(500, "PDF 文件解析失败: " + e.getMessage());
        }
    }
}
