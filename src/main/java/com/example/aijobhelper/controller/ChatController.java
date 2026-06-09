package com.example.aijobhelper.controller;

import com.example.aijobhelper.model.dto.ChatRequest;
import com.example.aijobhelper.model.dto.ChatResponse;
import com.example.aijobhelper.model.dto.UploadResult;
import com.example.aijobhelper.model.vo.ApiResult;
import com.example.aijobhelper.service.DeepSeekService;
import com.example.aijobhelper.service.PdfService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对话接口 —— 支持文本问答 & PDF 上传解析
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final PdfService pdfService;
    private final DeepSeekService deepSeekService;

    public ChatController(PdfService pdfService, DeepSeekService deepSeekService) {
        this.pdfService = pdfService;
        this.deepSeekService = deepSeekService;
    }

    /**
     * 纯文本问答 —— 直接调用 DeepSeek 获取回复
     *
     * <pre>
     * POST /api/chat/message
     * Content-Type: application/json
     * {"message": "请介绍一下Spring Boot的核心特性"}
     * </pre>
     */
    @PostMapping("/message")
    public ApiResult<ChatResponse> message(@Valid @RequestBody ChatRequest request) {
        String answer = deepSeekService.chat(request.getMessage());
        return ApiResult.ok(new ChatResponse(answer));
    }

    /**
     * 上传 PDF 文件并解析文本
     *
     * <pre>
     * curl -X POST http://localhost:8080/api/chat/upload \
     *   -F "file=@简历.pdf"
     * </pre>
     */
    @PostMapping("/upload")
    public ApiResult<UploadResult> upload(@RequestParam("file") MultipartFile file) {
        String text = pdfService.extractText(file);
        UploadResult result = new UploadResult(file.getOriginalFilename(), text.length(), text);
        return ApiResult.ok("PDF 解析成功", result);
    }
}
